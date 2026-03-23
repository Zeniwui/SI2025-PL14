package si.pl14.estadoPagosSocio;

import si.pl14.util.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para la historia de usuario
 * "Como socio quiero ver el estado de mis pagos de reservas y actividades".
 *
 * Devuelve todos los cargos del socio (pasados y pendientes), diferenciando
 * si el cargo es de una reserva o de una actividad.
 *
 * Indices de cada Object[] devuelto por getCargos():
 *   [0] tipo          (String)  "Reserva" | "Actividad"
 *   [1] estado_pago   (String)  "Pagado" | "Pendiente" | "Cuota" ...
 *   [2] fecha         (String)  "yyyy-MM-dd"
 *   [3] metodo_pago   (String)  puede ser null → se muestra "—"
 *   [4] coste_reserva (Double)
 */
public class EstadoPagosSocioModel {

    private final Database db = new Database();

    /**
     * ID del socio autenticado.
     * Valor 1 = "Carlos Gomez" (primer socio de datos de prueba).
     */
    public static final int ID_SOCIO_ACTUAL = 1;

    public EstadoPagosSocioModel() {
        db.createDatabase(true);
    }

    // -------------------------------------------------------------------------
    // Consulta principal
    // -------------------------------------------------------------------------

    /**
     * Devuelve todos los cargos del socio en el rango [fechaDesde, fechaHasta],
     * tanto de reservas propias como de actividades en las que esta inscrito,
     * ordenados por fecha.
     *
     * @param fechaDesde formato "yyyy-MM-dd"
     * @param fechaHasta formato "yyyy-MM-dd"
     */
    public List<Object[]> getCargos(String fechaDesde, String fechaHasta) {
        // Reservas directas del socio
        String sqlReservas =
            "SELECT 'Reserva'           AS tipo, " +
            "       estado_pago, " +
            "       fecha, " +
            "       COALESCE(metodo_pago, '-') AS metodo_pago, " +
            "       coste_reserva " +
            "FROM Reservas " +
            "WHERE id_socio = ? " +
            "  AND fecha >= ? AND fecha <= ?";

        // Actividades: reservas generadas por actividades en las que el socio
        // esta inscrito (id_actividad no nulo, id_socio nulo en Reservas,
        // pero el socio aparece en Inscripciones)
        String sqlActividades =
            "SELECT 'Actividad'         AS tipo, " +
            "       r.estado_pago, " +
            "       r.fecha, " +
            "       COALESCE(r.metodo_pago, '-') AS metodo_pago, " +
            "       r.coste_reserva " +
            "FROM Reservas r " +
            "JOIN Inscripciones i ON r.id_actividad = i.id_actividad " +
            "WHERE i.id_socio = ? " +
            "  AND r.fecha >= ? AND r.fecha <= ?";

        List<Object[]> resultado = new ArrayList<>();
        resultado.addAll(db.executeQueryArray(sqlReservas,    ID_SOCIO_ACTUAL, fechaDesde, fechaHasta));
        resultado.addAll(db.executeQueryArray(sqlActividades, ID_SOCIO_ACTUAL, fechaDesde, fechaHasta));

        // Ordenar por fecha (campo [2], String "yyyy-MM-dd" — orden lexicografico correcto)
        resultado.sort((a, b) -> {
            String fa = a[2] != null ? a[2].toString() : "";
            String fb = b[2] != null ? b[2].toString() : "";
            return fa.compareTo(fb);
        });

        return resultado;
    }

    /**
     * Calcula el importe total pendiente (estado_pago = 'Pendiente') del socio
     * en el rango dado.
     */
    public double getTotalPendiente(String fechaDesde, String fechaHasta) {
        String sql =
            "SELECT COALESCE(SUM(coste_reserva), 0) " +
            "FROM Reservas " +
            "WHERE id_socio = ? " +
            "  AND estado_pago = 'Pendiente' " +
            "  AND fecha >= ? AND fecha <= ?";
        List<Object[]> rows = db.executeQueryArray(sql, ID_SOCIO_ACTUAL, fechaDesde, fechaHasta);
        if (rows.isEmpty() || rows.get(0)[0] == null) return 0.0;
        Object v = rows.get(0)[0];
        return v instanceof Number ? ((Number) v).doubleValue() : Double.parseDouble(v.toString());
    }
}