package ReservasEmma;

import java.util.List;

import si.pl14.model.ReservaEntity;
import si.pl14.util.Database;

public class AnulacionModel {
    private Database db = new Database();

    public List<ReservaEntity> getReservasFuturasSocio(int idSocio) {
        String sql = "SELECT id_reserva, id_instalacion, fecha, hora_inicio, hora_fin, coste_reserva " +
                     "FROM Reservas WHERE id_socio = ? AND (fecha > CURRENT_DATE OR (fecha = CURRENT_DATE AND hora_inicio > strftime('%H', 'now'))) " +
                     "ORDER BY fecha ASC, hora_inicio ASC";
        return db.executeQueryPojo(ReservaEntity.class, sql, idSocio);
    }

    public void anularReserva(int idReserva, String motivo) {
        String sqlReserva = "DELETE FROM Reservas WHERE id_reserva = ?";
        db.executeUpdate(sqlReserva, idReserva);
        String sqlPago = "DELETE FROM Pagos WHERE id_reserva = ? AND estado_pago = 'Pendiente'";
        db.executeUpdate(sqlPago, idReserva);
        System.out.println("Notificación enviada al socio. Motivo: " + motivo);
    }
}
