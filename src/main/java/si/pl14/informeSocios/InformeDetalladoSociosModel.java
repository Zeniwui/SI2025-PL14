package si.pl14.informeSocios;

import si.pl14.util.Database;

import java.util.ArrayList;
import java.util.List;

public class InformeDetalladoSociosModel {

    private final Database db = new Database();

    public InformeDetalladoSociosModel() {
        db.createDatabase(true);
    }

    public List<InformeDetalladoSocioDTO> getInforme(String fechaDesde, String fechaHasta) {
        String sql =
            "SELECT " +
            "    u.nombre || ' ' || u.apellidos AS nombre_socio, " +
            "    COUNT(DISTINCT r.id_reserva) AS num_reservas, " +
            "    COALESCE(( " +
            "        SELECT COUNT(*) FROM Inscripciones i " +
            "        JOIN Actividades a ON i.id_actividad = a.id_actividad " +
            "        WHERE i.id_socio = s.id_socio " +
            "          AND a.fecha_inicio BETWEEN ? AND ? " +
            "    ), 0) AS num_actividades, " +
            "    COALESCE(SUM(CASE WHEN r.estado_pago = 'Pendiente' " +
            "                      THEN r.coste_reserva ELSE 0.0 END), 0.0) AS deuda, " +
            "    COALESCE(( " +
            "        SELECT ins.nombre FROM Reservas r2 " +
            "        JOIN Instalaciones ins ON r2.id_instalacion = ins.id_instalacion " +
            "        WHERE r2.id_socio = s.id_socio " +
            "          AND r2.fecha BETWEEN ? AND ? " +
            "        GROUP BY r2.id_instalacion " +
            "        ORDER BY COUNT(*) DESC LIMIT 1 " +
            "    ), '-') AS instalacion_favorita " +
            "FROM Socios s " +
            "JOIN Usuarios u ON s.dni = u.dni " +
            "LEFT JOIN Reservas r ON r.id_socio = s.id_socio " +
            "                     AND r.fecha BETWEEN ? AND ? " +
            "GROUP BY s.id_socio, u.nombre, u.apellidos " +
            // Solo se incluyen socios que tuvieron reservas o actividades en el periodo
            "HAVING COUNT(DISTINCT r.id_reserva) > 0 " +
            "    OR (SELECT COUNT(*) FROM Inscripciones i2 " +
            "        JOIN Actividades a2 ON i2.id_actividad = a2.id_actividad " +
            "        WHERE i2.id_socio = s.id_socio " +
            "          AND a2.fecha_inicio BETWEEN ? AND ?) > 0";

        List<Object[]> filas = db.executeQueryArray(sql,
            fechaDesde, fechaHasta,   // num_actividades (subquery SELECT)
            fechaDesde, fechaHasta,   // instalacion_favorita (subquery SELECT)
            fechaDesde, fechaHasta,   // LEFT JOIN Reservas (fecha del periodo)
            fechaDesde, fechaHasta);  // HAVING actividades (subquery)

        List<InformeDetalladoSocioDTO> resultado = new ArrayList<>();
        for (Object[] f : filas) {
            String nombre   = f[0] != null ? f[0].toString() : "-";
            int    reservas = toInt(f[1]);
            int    activ    = toInt(f[2]);
            double deuda    = toDouble(f[3]);
            String favInst  = f[4] != null ? f[4].toString() : "-";
            resultado.add(new InformeDetalladoSocioDTO(nombre, reservas, activ, deuda, favInst));
        }
        return resultado;
    }

    private static int toInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return 0; }
    }

    private static double toDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0.0; }
    }
}