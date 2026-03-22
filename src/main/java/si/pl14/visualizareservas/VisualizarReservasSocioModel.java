package si.pl14.visualizareservas;

import si.pl14.model.InstalacionEntity;
import si.pl14.util.Database;

import java.util.ArrayList;
import java.util.List;

public class VisualizarReservasSocioModel {

    private final Database db = new Database();

    public static final int ID_SOCIO_ACTUAL = 1;

    public VisualizarReservasSocioModel() {
        db.createDatabase(true);
        db.loadDatabase();
    }

    public List<InstalacionEntity> getInstalaciones() {
        String sql = "SELECT id_instalacion AS idInstalacion, nombre, tipo, coste_hora AS costeHora " +
                     "FROM Instalaciones ORDER BY nombre";
        return db.executeQueryPojo(InstalacionEntity.class, sql);
    }

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