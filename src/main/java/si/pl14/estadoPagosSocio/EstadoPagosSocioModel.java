package si.pl14.estadoPagosSocio;

import si.pl14.util.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo del caso de uso "Visualizar Estado de Pagos del Socio".
 * Encapsula todas las consultas SQL necesarias para obtener los cargos
 * del socio (reservas propias y actividades) en un rango de fechas.
 */
public class EstadoPagosSocioModel {

    private final Database db = new Database();

    /** ID del socio autenticado. Valor 1 = "Carlos Gomez" (datos de prueba). */
    public static final int ID_SOCIO_ACTUAL = 1;

    public EstadoPagosSocioModel() {
        db.createDatabase(true);
    }

    /**
     * Devuelve todos los cargos del socio en el rango [fechaDesde, fechaHasta],
     * tanto de reservas propias como de actividades, ordenados por fecha.
     * <p>
     * Índices del Object[]:
     * [0] tipo ("Reserva" | "Actividad"),
     * [1] estado_pago,
     * [2] fecha,
     * [3] metodo_pago,
     * [4] coste (coste_reserva para reservas propias; precio_socio para actividades)
     * </p>
     *
     * @param fechaDesde  fecha de inicio en formato ISO (yyyy-MM-dd)
     * @param fechaHasta  fecha de fin   en formato ISO (yyyy-MM-dd)
     * @return lista de filas ordenadas por fecha ascendente
     */
    public List<Object[]> getCargos(String fechaDesde, String fechaHasta) {
        // Reservas directas del socio (instalación reservada por él mismo)
        String sqlReservas =
            "SELECT 'Reserva'                       AS tipo, " +
            "       estado_pago, " +
            "       fecha, " +
            "       COALESCE(metodo_pago, '-')       AS metodo_pago, " +
            "       coste_reserva " +
            "FROM Reservas " +
            "WHERE id_socio = ? " +
            "  AND fecha >= ? AND fecha <= ?";

        // Actividades en las que el socio está inscrito:
        // el coste que paga el socio es precio_socio de la tabla Actividades,
        // no el coste_reserva de la instalación.
        String sqlActividades =
            "SELECT 'Actividad'                     AS tipo, " +
            "       r.estado_pago, " +
            "       r.fecha, " +
            "       COALESCE(r.metodo_pago, '-')     AS metodo_pago, " +
            "       a.precio_socio " +
            "FROM Reservas r " +
            "JOIN Inscripciones i ON r.id_actividad = i.id_actividad " +
            "JOIN Actividades    a ON r.id_actividad = a.id_actividad " +
            "WHERE i.id_socio = ? " +
            "  AND r.fecha >= ? AND r.fecha <= ?";

        List<Object[]> resultado = new ArrayList<>();
        resultado.addAll(db.executeQueryArray(sqlReservas,    ID_SOCIO_ACTUAL, fechaDesde, fechaHasta));
        resultado.addAll(db.executeQueryArray(sqlActividades, ID_SOCIO_ACTUAL, fechaDesde, fechaHasta));

        resultado.sort((a, b) -> {
            String fa = a[2] != null ? a[2].toString() : "";
            String fb = b[2] != null ? b[2].toString() : "";
            return fa.compareTo(fb);
        });

        return resultado;
    }

    /**
     * Calcula el importe total de cargos con estado "Pendiente" del socio
     * en el rango [fechaDesde, fechaHasta].
     * <p>
     * Para reservas directas se usa {@code coste_reserva}; para actividades
     * se usa {@code precio_socio} de la tabla Actividades.
     * </p>
     *
     * @param fechaDesde  fecha de inicio en formato ISO (yyyy-MM-dd)
     * @param fechaHasta  fecha de fin   en formato ISO (yyyy-MM-dd)
     * @return suma de importes pendientes (0.0 si no hay ninguno)
     */
    public double getTotalPendiente(String fechaDesde, String fechaHasta) {
        // Importe pendiente de reservas directas del socio
        String sqlReservas =
            "SELECT COALESCE(SUM(coste_reserva), 0) " +
            "FROM Reservas " +
            "WHERE id_socio = ? " +
            "  AND estado_pago = 'Pendiente' " +
            "  AND fecha >= ? AND fecha <= ?";

        // Importe pendiente de actividades: se suma precio_socio, no coste_reserva
        String sqlActividades =
            "SELECT COALESCE(SUM(a.precio_socio), 0) " +
            "FROM Reservas r " +
            "JOIN Inscripciones i ON r.id_actividad = i.id_actividad " +
            "JOIN Actividades    a ON r.id_actividad = a.id_actividad " +
            "WHERE i.id_socio = ? " +
            "  AND r.estado_pago = 'Pendiente' " +
            "  AND r.fecha >= ? AND r.fecha <= ?";

        double total = 0.0;
        List<Object[]> r1 = db.executeQueryArray(sqlReservas,    ID_SOCIO_ACTUAL, fechaDesde, fechaHasta);
        List<Object[]> r2 = db.executeQueryArray(sqlActividades, ID_SOCIO_ACTUAL, fechaDesde, fechaHasta);
        if (!r1.isEmpty() && r1.get(0)[0] != null) total += toDouble(r1.get(0)[0]);
        if (!r2.isEmpty() && r2.get(0)[0] != null) total += toDouble(r2.get(0)[0]);
        return total;
    }

    private static double toDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0.0; }
    }
}