package ReservasEmma;

import java.util.List;
import si.pl14.util.Database;

public class AnulacionModel {
    private Database db = new Database();

    public static class ReservaDetalleDTO {
        private int idReserva;
        private String instalacion;
        private String fecha;
        private String horaInicio;
        private String horaFin;
        private double costeReserva;
        private int idSocio;
        private String dniSocio;
        private String nombreSocio;

        public int getIdReserva() { return idReserva; }
        public void setIdReserva(int id) { this.idReserva = id; }
        public String getInstalacion() { return instalacion; }
        public void setInstalacion(String i) { this.instalacion = i; }
        public String getFecha() { return fecha; }
        public void setFecha(String f) { this.fecha = f; }
        public String getHoraInicio() { return horaInicio; }
        public void setHoraInicio(String h) { this.horaInicio = h; }
        public String getHoraFin() { return horaFin; }
        public void setHoraFin(String h) { this.horaFin = h; }
        public double getCosteReserva() { return costeReserva; }
        public void setCosteReserva(double c) { this.costeReserva = c; }
        public int getIdSocio() { return idSocio; }
        public void setIdSocio(int id) { this.idSocio = id; }
        public String getDniSocio() { return dniSocio; }
        public void setDniSocio(String d) { this.dniSocio = d; }
        public String getNombreSocio() { return nombreSocio; }
        public void setNombreSocio(String n) { this.nombreSocio = n; }
    }

    private String getBaseQuery() {
        return "SELECT r.id_reserva AS idReserva, i.nombre AS instalacion, r.fecha AS fecha, " +
               "r.hora_inicio AS horaInicio, r.hora_fin AS horaFin, r.coste_reserva AS costeReserva, " +
               "s.id_socio AS idSocio, u.dni AS dniSocio, (u.nombre || ' ' || u.apellidos) AS nombreSocio " +
               "FROM Reservas r " +
               "LEFT JOIN Instalaciones i ON r.id_instalacion = i.id_instalacion " +
               "LEFT JOIN Socios s ON r.id_socio = s.id_socio " +
               "LEFT JOIN Usuarios u ON s.dni = u.dni ";
    }

    private String getTimeFilter() {
        return " WHERE ((date(r.fecha) > date('now')) " +
               " OR (date(r.fecha) = date('now') AND time(r.hora_inicio) > time('now', 'localtime'))) ";
    }

    public List<ReservaDetalleDTO> getReservasPorNombre(String nombre) {
        String sql = getBaseQuery() + getTimeFilter() +  
                     " AND (u.nombre LIKE ? OR u.apellidos LIKE ?) ORDER BY r.fecha ASC";
        return db.executeQueryPojo(ReservaDetalleDTO.class, sql, "%" + nombre + "%", "%" + nombre + "%");
    }

    public List<ReservaDetalleDTO> getReservasPorDni(String dni) {
        String sql = getBaseQuery() + getTimeFilter() + " AND u.dni = ? ORDER BY r.fecha ASC";
        return db.executeQueryPojo(ReservaDetalleDTO.class, sql, dni);
    }

    public List<ReservaDetalleDTO> getReservasPorId(int idSocio) {
        String sql = getBaseQuery() + getTimeFilter() + " AND s.id_socio = ? ORDER BY r.fecha ASC";
        return db.executeQueryPojo(ReservaDetalleDTO.class, sql, idSocio);
    }

    public void anularReserva(int idReserva) {
        db.executeUpdate("DELETE FROM Pagos WHERE id_reserva = ?", idReserva);
        db.executeUpdate("DELETE FROM Reservas WHERE id_reserva = ?", idReserva);
    }
}
