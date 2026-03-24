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
    }

    public List<InstalacionEntity> getInstalaciones() {
        String sql = "SELECT id_instalacion AS idInstalacion, nombre, tipo, coste_hora AS costeHora " +
                     "FROM Instalaciones ORDER BY nombre";
        return db.executeQueryPojo(InstalacionEntity.class, sql);
    }

    public List<ReservasSocioDTO> getReservas(int idInstalacion, String fechaDesde, String fechaHasta) {
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

        List<Object[]> filas = db.executeQueryArray(sql.toString(), params.toArray());
        return mapearDTO(filas);
    }

    private List<ReservasSocioDTO> mapearDTO(List<Object[]> filas) {
        List<ReservasSocioDTO> lista = new ArrayList<>();
        for (Object[] f : filas) {
            ReservasSocioDTO dto = new ReservasSocioDTO();
            dto.setIdReserva(f[0] instanceof Number ? ((Number) f[0]).intValue() : 0);
            dto.setFecha(f[1] != null ? f[1].toString() : "");
            dto.setHoraInicio(f[2] != null ? f[2].toString() : "");
            dto.setHoraFin(f[3] != null ? f[3].toString() : "");
            dto.setEstadoPago(f[4] != null ? f[4].toString() : "-");
            dto.setMetodoPago(f[5] != null ? f[5].toString() : "-");
            dto.setCosteReserva(f[6] instanceof Number ? ((Number) f[6]).doubleValue() : 0.0);
            dto.setFechaCreacion(f[7] != null ? f[7].toString() : "-");
            dto.setNombreInstalacion(f[8] != null ? f[8].toString() : "-");
            lista.add(dto);
        }
        return lista;
    }
}