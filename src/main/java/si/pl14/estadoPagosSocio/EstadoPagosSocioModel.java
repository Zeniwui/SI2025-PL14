package si.pl14.estadoPagosSocio;

import si.pl14.util.Database;

import java.util.ArrayList;
import java.util.List;

public class EstadoPagosSocioModel {

    private final Database db = new Database();

    // ID del socio autenticado. Valor 1 = "Carlos Gomez" (datos de prueba).
    public static final int ID_SOCIO_ACTUAL = 1;

    public EstadoPagosSocioModel() {
        db.createDatabase(true);
    }

    /**
     * Devuelve todos los cargos del socio en el rango [fechaDesde, fechaHasta],
     * tanto de reservas propias como de actividades, ordenados por fecha.
     * Indices del Object[]: [0] tipo, [1] estado_pago, [2] fecha, [3] metodo_pago, [4] coste_reserva
     */
    public List<Object[]> getCargos(String fechaDesde, String fechaHasta) {
        String sqlReservas =
            "SELECT 'Reserva'           AS tipo, " +
            "       estado_pago, " +
            "       fecha, " +
            "       COALESCE(metodo_pago, '-') AS metodo_pago, " +
            "       coste_reserva " +
            "FROM Reservas " +
            "WHERE id_socio = ? " +
            "  AND fecha >= ? AND fecha <= ?";

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

        resultado.sort((a, b) -> {
            String fa = a[2] != null ? a[2].toString() : "";
            String fb = b[2] != null ? b[2].toString() : "";
            return fa.compareTo(fb);
        });

        return resultado;
    }

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