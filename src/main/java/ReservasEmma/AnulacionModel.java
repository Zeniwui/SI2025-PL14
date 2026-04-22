package ReservasEmma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import si.pl14.util.Database;

public class AnulacionModel {
	private Database db = new Database();

	private String getBaseQuery() {
		return "SELECT r.id_reserva AS idReserva, i.nombre AS instalacion, r.fecha AS fecha, "
				+ "r.hora_inicio AS horaInicio, r.hora_fin AS horaFin, r.coste_reserva AS costeReserva, "
				+ "s.id_socio AS idSocio, u.dni AS dniSocio, (u.nombre || ' ' || u.apellidos) AS nombreSocio "
				+ "FROM Reservas r " + "LEFT JOIN Instalaciones i ON r.id_instalacion = i.id_instalacion "
				+ "LEFT JOIN Socios s ON r.id_socio = s.id_socio " + "LEFT JOIN Usuarios u ON s.dni = u.dni ";
	}

	private String getTimeFilter() {
		return " WHERE ((date(r.fecha) > date('now')) "
				+ " OR (date(r.fecha) = date('now') AND time(r.hora_inicio) > time('now', 'localtime'))) ";
	}

	public List<ReservaDetalleDTO> getReservasPorNombre(String nombre) {
		String sql = getBaseQuery() + getTimeFilter()
				+ " AND (u.nombre LIKE ? OR u.apellidos LIKE ?) ORDER BY r.fecha ASC";
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

	public void anularReserva(int idReserva) throws SQLException {
		try {
			db.getConnection().setAutoCommit(false); // Iniciamos la transacción

			db.executeUpdate("DELETE FROM Pagos WHERE id_reserva = ?", idReserva);
			db.executeUpdate("DELETE FROM Reservas WHERE id_reserva = ?", idReserva);

			db.getConnection().commit();
		} catch (Exception e) {
			db.getConnection().rollback();
		} finally {
			db.getConnection().setAutoCommit(true);
		}
	}

	void guardarNotificacionTxt(String nombreSocio, String motivo) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("notificaciones_cancelaciones.txt", true))) {
			String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
			String mensaje = "[" + fechaHora + "] SOCIO: " + nombreSocio + " | MOTIVO: " + motivo;

			writer.write(mensaje);
			writer.newLine();

			System.out.println("Se ha guardado la notificación en 'notificaciones_cancelaciones.txt'");
		} catch (IOException e) {
			System.err.println("Error al escribir el archivo txt: " + e.getMessage());
		}
	}
}
