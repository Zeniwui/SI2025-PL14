package si.pl14.reservasEmma;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import si.pl14.util.Database;

public class Reserva_Instalacion_Admin_Model {
	private Database db = new Database();

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(db.getUrl());
	}

	public List<String> getActividades() {
		List<String> actividades = new ArrayList<>();
		String sql = "SELECT id_actividad, nombre FROM Actividades";
		try (Connection con = getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {
			while (rs.next()) {
				actividades.add(rs.getInt("id_actividad") + " - " + rs.getString("nombre"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return actividades;
	}

	public String[] getDatosActividad(int idActividad) {
		String[] datos = new String[4];
		String sql = "SELECT i.nombre, i.id_instalacion, a.fecha_inicio, a.fecha_fin "
				+ "FROM Actividades a JOIN Instalaciones i ON a.id_instalacion = i.id_instalacion WHERE a.id_actividad = ?";
		try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, idActividad);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				datos[0] = rs.getString("nombre");
				datos[1] = String.valueOf(rs.getInt("id_instalacion"));
				datos[2] = rs.getString("fecha_inicio");
				datos[3] = rs.getString("fecha_fin");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return datos;
	}

	public List<String> getInstalaciones() {
		List<String> inst = new ArrayList<>();
		String sql = "SELECT id_instalacion, nombre FROM Instalaciones";
		try (Connection con = getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {
			while (rs.next()) {
				inst.add(rs.getInt("id_instalacion") + " - " + rs.getString("nombre"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return inst;
	}

	public boolean actualizarInstalacion(int idActividad, int newIdInstalacion) {
		String updateAct = "UPDATE Actividades SET id_instalacion = ? WHERE id_actividad = ?";
		String updateRes = "UPDATE Reservas SET id_instalacion = ? WHERE id_actividad = ?";
		try (Connection con = getConnection()) {
			con.setAutoCommit(false);
			try (PreparedStatement p1 = con.prepareStatement(updateAct);
					PreparedStatement p2 = con.prepareStatement(updateRes)) {
				p1.setInt(1, newIdInstalacion);
				p1.setInt(2, idActividad);
				p1.executeUpdate();
				p2.setInt(1, newIdInstalacion);
				p2.setInt(2, idActividad);
				p2.executeUpdate();
				con.commit();
				return true;
			} catch (SQLException ex) {
				con.rollback();
				throw ex;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// NUEVO: Obtener el horario previamente creado en la HU de "Crear Actividad"
	public List<String[]> getHorariosActividad(int idActividad) {
		List<String[]> horarios = new ArrayList<>();
		String sql = "SELECT dia_semana, hora_inicio, hora_fin FROM Horarios WHERE id_actividad = ?";
		try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, idActividad);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				horarios.add(new String[] { rs.getString("dia_semana"), rs.getString("hora_inicio"),
						rs.getString("hora_fin") });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return horarios;
	}

	// MODIFICADO: Busca conflictos aplicando el filtro de fechas
	public List<String[]> buscarOcupacionConFiltro(int idInst, String fInicio, String fFin) {
		List<String[]> ocupacion = new ArrayList<>();
		String sql = "SELECT fecha, hora_inicio, hora_fin, id_socio, id_actividad FROM Reservas "
				+ "WHERE id_instalacion = ? AND fecha >= ? AND fecha <= ? ORDER BY fecha, hora_inicio";
		try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, idInst);
			pst.setString(2, fInicio);
			pst.setString(3, fFin);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				String ocupante = rs.getString("id_socio") != null ? "Socio ID: " + rs.getString("id_socio")
						: "Actividad ID: " + rs.getString("id_actividad");
				ocupacion.add(new String[] { rs.getString("fecha"), rs.getString("hora_inicio"),
						rs.getString("hora_fin"), ocupante });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ocupacion;
	}

	// Para validar si hay choque antes de insertar (Lógica interna)
	public boolean existeConflicto(int idInst, String fecha, String hIni, String hFin) {
		String sql = "SELECT 1 FROM Reservas WHERE id_instalacion = ? AND fecha = ? AND (hora_inicio < ? AND hora_fin > ?)";
		try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, idInst);
			pst.setString(2, fecha);
			pst.setString(3, hFin);
			pst.setString(4, hIni);
			ResultSet rs = pst.executeQuery();
			return rs.next(); // True si hay al menos un registro (conflicto)
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		} 
	}

	// Solo inserta Reservas (Horarios ya está insertado por la HU anterior)
	public boolean insertarReservasGeneradas(int idInstalacion, int idActividad, List<String[]> reservasACrear) {
		String sql = "INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_actividad) VALUES (?, ?, ?, ?, ?)";
		try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
			con.setAutoCommit(false);
			for (String[] res : reservasACrear) {
				pst.setInt(1, idInstalacion);
				pst.setString(2, res[0]); // Fecha
				pst.setString(3, res[1]); // Hora Inicio
				pst.setString(4, res[2]); // Hora Fin
				pst.setInt(5, idActividad);
				pst.addBatch();
			}
			pst.executeBatch();
			con.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<String[]> buscarConflictosDetallados(int idInst, String fInicio, String fFin, int idActActual) {
		List<String[]> conflictos = new ArrayList<>();
		// Esta consulta busca qué hay en la reserva que NO sea la propia actividad que
		// estamos gestionando
		String sql = "SELECT r.fecha, r.hora_inicio, r.hora_fin, "
				+ "CASE WHEN r.id_socio IS NOT NULL THEN 'OCUPADO POR SOCIO: ' || u.nombre || ' ' || u.apellidos "
				+ "     WHEN r.id_actividad IS NOT NULL THEN 'OCUPADO POR ACTIVIDAD: ' || a.nombre "
				+ "     ELSE 'BLOQUEO DE INSTALACIÓN' END as detalle " + "FROM Reservas r "
				+ "LEFT JOIN Actividades a ON r.id_actividad = a.id_actividad "
				+ "LEFT JOIN Socios s ON r.id_socio = s.id_socio " + "LEFT JOIN Usuarios u ON s.dni = u.dni "
				+ "WHERE r.id_instalacion = ? AND r.fecha >= ? AND r.fecha <= ? "
				+ "AND (r.id_actividad IS NULL OR r.id_actividad <> ?) " + "ORDER BY r.fecha, r.hora_inicio";

		try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setInt(1, idInst);
			pst.setString(2, fInicio);
			pst.setString(3, fFin);
			pst.setInt(4, idActActual);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				conflictos.add(new String[] { rs.getString("fecha"),
						rs.getString("hora_inicio") + " - " + rs.getString("hora_fin"), rs.getString("ocupante") });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conflictos;
	}

	public boolean actualizarHorarioActividad(int idActividad, String nuevoDia, String nuevaHIni, String nuevaHFin) {
		String sql = "UPDATE Horarios SET dia_semana = ?, hora_inicio = ?, hora_fin = ? WHERE id_actividad = ?";
		try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, nuevoDia.toUpperCase());
			pst.setString(2, nuevaHIni);
			pst.setString(3, nuevaHFin);
			pst.setInt(4, idActividad);
			int affected = pst.executeUpdate();

			String sqlDelReservas = "DELETE FROM Reservas WHERE id_actividad = ?";
			try (PreparedStatement pstDel = con.prepareStatement(sqlDelReservas)) {
				pstDel.setInt(1, idActividad);
				pstDel.executeUpdate();
			}

			return affected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}