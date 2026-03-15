package si.pl14.visualizareservas;


import si.pl14.model.InstalacionEntity;
import si.pl14.util.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para la historia de usuario "Visualizar mis reservas durante un periodo de tiempo".
 *
 * Tablas usadas:
 *   Reservas       (id_reserva, id_instalacion, fecha, hora_inicio, hora_fin,
 *                   id_socio, coste_reserva, estado_pago, metodo_pago, fecha_creacion)
 *   Instalaciones  (id_instalacion, nombre)
 *
 * Índices de cada Object[] devuelto por getReservas():
 *   [0] id_reserva          (Integer)
 *   [1] fecha               (String  "yyyy-MM-dd")
 *   [2] hora_inicio         (String  "HH:mm:ss")
 *   [3] hora_fin            (String  "HH:mm:ss")
 *   [4] estado_pago         (String)
 *   [5] metodo_pago         (String, nunca null — COALESCE a '—')
 *   [6] coste_reserva       (Double)
 *   [7] fecha_creacion      (String  "dd/MM/yyyy HH:mm")
 *   [8] nombre_instalacion  (String)
 */
public class VisualizarReservasSocioModel {

    private final Database db = new Database();

    /**
     * ID del socio autenticado.
     * Valor 1 = "Carlos Gomez" (primer socio de los datos de prueba).
     * Cambiar para simular otro usuario.
     */
    public static final int ID_SOCIO_ACTUAL = 1;

    public VisualizarReservasSocioModel() {
        db.createDatabase(true);
    }

    // -------------------------------------------------------------------------
    // Instalaciones (para el combo de filtro)
    // -------------------------------------------------------------------------

    /** Devuelve todas las instalaciones ordenadas por nombre. */
    public List<InstalacionEntity> getInstalaciones() {
        String sql = "SELECT id_instalacion AS idInstalacion, nombre, tipo, coste_hora AS costeHora " +
                     "FROM Instalaciones ORDER BY nombre";
        return db.executeQueryPojo(InstalacionEntity.class, sql);
    }

    // -------------------------------------------------------------------------
    // Consulta principal
    // -------------------------------------------------------------------------

    /**
     * Devuelve las reservas del socio actual en el rango [fechaDesde, fechaHasta],
     * ordenadas por fecha y hora (criterio de aceptacion).
     *
     * @param idInstalacion  0 = todas las instalaciones
     * @param fechaDesde     formato "yyyy-MM-dd"
     * @param fechaHasta     formato "yyyy-MM-dd"
     * @return lista de Object[] — ver indices en el javadoc de la clase
     */
    public List<Object[]> getReservas(int idInstalacion, String fechaDesde, String fechaHasta) {
        StringBuilder sql = new StringBuilder(
            "SELECT r.id_reserva, " +
            "       r.fecha, " +
            "       r.hora_inicio, " +
            "       r.hora_fin, " +
            "       r.estado_pago, " +
            "       COALESCE(r.metodo_pago, '-') AS metodo_pago, " +
            "       r.coste_reserva, " +
            "       strftime('%d/%m/%Y %H:%M', r.fecha_creacion) AS fecha_creacion, " +
            "       i.nombre AS nombre_instalacion " +
            "FROM Reservas r " +
            "JOIN Instalaciones i ON r.id_instalacion = i.id_instalacion " +
            "WHERE r.id_socio = ? " +
            "  AND r.fecha >= ? AND r.fecha <= ?"
        );

        List<Object> params = new ArrayList<>();
        params.add(ID_SOCIO_ACTUAL);
        params.add(fechaDesde);
        params.add(fechaHasta);

        if (idInstalacion != 0) {
            sql.append(" AND r.id_instalacion = ?");
            params.add(idInstalacion);
        }

        sql.append(" ORDER BY r.fecha, r.hora_inicio");

        return db.executeQueryArray(sql.toString(), params.toArray());
    }
}