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
            "    s.id_socio, " +
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
            "        SELECT ins.nombre FROM ( " +
            "            SELECT r2.id_instalacion FROM Reservas r2 " +
            "            WHERE r2.id_socio = s.id_socio " +
            "              AND r2.fecha BETWEEN ? AND ? " +
            "            UNION ALL " +
            "            SELECT a2.id_instalacion FROM Inscripciones i2 " +
            "            JOIN Actividades a2 ON i2.id_actividad = a2.id_actividad " +
            "            WHERE i2.id_socio = s.id_socio " +
            "              AND a2.fecha_inicio BETWEEN ? AND ? " +
            "        ) usos " +
            "        JOIN Instalaciones ins ON usos.id_instalacion = ins.id_instalacion " +
            "        GROUP BY usos.id_instalacion " +
            "        ORDER BY COUNT(*) DESC LIMIT 1 " +
            "    ), '-') AS instalacion_favorita " +
            "FROM Socios s " +
            "JOIN Usuarios u ON s.dni = u.dni " +
            "LEFT JOIN Reservas r ON r.id_socio = s.id_socio " +
            "                     AND r.fecha BETWEEN ? AND ? " +
            "GROUP BY s.id_socio, u.nombre, u.apellidos " +
            "HAVING COUNT(DISTINCT r.id_reserva) > 0 " +
            "    OR (SELECT COUNT(*) FROM Inscripciones i2 " +
            "        JOIN Actividades a2 ON i2.id_actividad = a2.id_actividad " +
            "        WHERE i2.id_socio = s.id_socio " +
            "          AND a2.fecha_inicio BETWEEN ? AND ?) > 0";

        // Query auxiliar: top-3 instalaciones (reservas + actividades) para un socio
        String sqlTop3 =
            "SELECT ins.nombre, COUNT(*) AS usos FROM ( " +
            "    SELECT r2.id_instalacion FROM Reservas r2 " +
            "    WHERE r2.id_socio = ? AND r2.fecha BETWEEN ? AND ? " +
            "    UNION ALL " +
            "    SELECT a2.id_instalacion FROM Inscripciones i2 " +
            "    JOIN Actividades a2 ON i2.id_actividad = a2.id_actividad " +
            "    WHERE i2.id_socio = ? AND a2.fecha_inicio BETWEEN ? AND ? " +
            ") usos " +
            "JOIN Instalaciones ins ON usos.id_instalacion = ins.id_instalacion " +
            "GROUP BY usos.id_instalacion " +
            "ORDER BY usos DESC LIMIT 3";

        List<Object[]> filas = db.executeQueryArray(sql,
            fechaDesde, fechaHasta,   // num_actividades
            fechaDesde, fechaHasta,   // instalacion_favorita – reservas
            fechaDesde, fechaHasta,   // instalacion_favorita – actividades
            fechaDesde, fechaHasta,   // LEFT JOIN Reservas
            fechaDesde, fechaHasta);  // HAVING actividades

        List<InformeDetalladoSocioDTO> resultado = new ArrayList<>();
        for (Object[] f : filas) {
            int    idSocio  = toInt(f[0]);
            String nombre   = f[1] != null ? f[1].toString() : "-";
            int    reservas = toInt(f[2]);
            int    activ    = toInt(f[3]);
            double deuda    = toDouble(f[4]);
            String favInst  = f[5] != null ? f[5].toString() : "-";

            // Construir string top-3: "Pista de Tenis 1 (4) | Sala Polivalente (2) | Pádel 1 (1)"
            List<Object[]> top3 = db.executeQueryArray(sqlTop3,
                idSocio, fechaDesde, fechaHasta,
                idSocio, fechaDesde, fechaHasta);
            StringBuilder sb = new StringBuilder();
            for (Object[] row : top3) {
                if (sb.length() > 0) sb.append(" | ");
                sb.append(row[0]).append(" (").append(toInt(row[1])).append(")");
            }
            String topInst = sb.length() > 0 ? sb.toString() : "-";

            resultado.add(new InformeDetalladoSocioDTO(
                nombre, reservas, activ, deuda, favInst, topInst));
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