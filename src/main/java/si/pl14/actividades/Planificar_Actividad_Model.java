package si.pl14.actividades;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import si.pl14.model.ActividadEntity;
import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

public class Planificar_Actividad_Model {

	private Database db = new Database();

	public Planificar_Actividad_Model() {
		db.createDatabase(true);
		db.executeScript("src/main/resources/data.sql");
	}

	public void insertarActividad(ActividadEntity actividad, List<HorarioEntity> horarios) {
		validarTodo(actividad);

		try (Connection conn = db.getConnection()) {
			conn.setAutoCommit(false);
			try {
				// insertar Actividad
				String sqlAct = "INSERT INTO Actividades (nombre, descripcion, id_instalacion, aforo, "
				        + "fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) "
				        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

				int idActividadGenerated;
				try (PreparedStatement pst = conn.prepareStatement(sqlAct, Statement.RETURN_GENERATED_KEYS)) {
					pst.setString(1, actividad.getNombre());
				    pst.setString(2, actividad.getDescripcion());
				    pst.setInt(3, actividad.getIdInstalacion());
				    pst.setInt(4, actividad.getAforo());
				    pst.setDate(5, java.sql.Date.valueOf(actividad.getFechaInicio())); // Inicio Socios
				    pst.setDate(6, java.sql.Date.valueOf(actividad.getFechaFin()));    // Fin Socios
				    pst.setFloat(7, (float) actividad.getPrecioSocio());
				    pst.setFloat(8, (float) actividad.getPrecioNoSocio());
				    pst.setInt(9, actividad.getIdPeriodo());
				    pst.executeUpdate();

					ResultSet rs = pst.getGeneratedKeys();
					if (rs.next())
						idActividadGenerated = rs.getInt(1);
					else
						throw new SQLException("Error al obtener ID de actividad");
				}

				// insertar Horarios tras verificar disponibilidad
				String sqlHor = "INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)";
				String sqlReserva = "INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_actividad) VALUES (?, ?, ?, ?, ?)";
				
				LocalDate start = LocalDate.parse(actividad.getFechaInicio());
	            LocalDate end = LocalDate.parse(actividad.getFechaFin());
	            
				for (HorarioEntity h : horarios) {
					if (!isInstalacionLibre(conn, actividad.getIdInstalacion(), h)) {
						throw new ApplicationException("Conflicto: La instalación está ocupada el " + h.getDiaSemana()
								+ " a las " + h.getHoraInicio());
					}
					try (PreparedStatement pstH = conn.prepareStatement(sqlHor)) {
						pstH.setInt(1, idActividadGenerated);
						pstH.setString(2, h.getDiaSemana().toUpperCase());
						pstH.setString(3, h.getHoraInicio());
						pstH.setString(4, h.getHoraFin());
						pstH.executeUpdate();
					}
					
					java.time.DayOfWeek diaBuscado = traducirDiaSemana(h.getDiaSemana());
					
					for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
	                    if (date.getDayOfWeek() == diaBuscado) {
	                        // Verificamos si la pista está libre para este día concreto
	                        if (!isPistaLibreParaFecha(conn, actividad.getIdInstalacion(), date.toString(), h.getHoraInicio(), h.getHoraFin())) {
	                            throw new ApplicationException("Conflicto: La instalación está ocupada el " + date + " (" + h.getDiaSemana() + ")");
	                        }

	                        try (PreparedStatement pstR = conn.prepareStatement(sqlReserva)) {
	                            pstR.setInt(1, actividad.getIdInstalacion());
	                            pstR.setString(2, date.toString()); // Formato AAAA-MM-DD
	                            pstR.setString(3, h.getHoraInicio());
	                            pstR.setString(4, h.getHoraFin());
	                            pstR.setInt(5, idActividadGenerated);
	                            pstR.executeUpdate();
	                        }
	                    }
	                }
					
				}
				conn.commit();
			} catch (Exception e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			throw new ApplicationException("Error de BD: " + e.getMessage());
		}
	}

	private boolean isInstalacionLibre(Connection conn, int idInstalacion, HorarioEntity h) throws SQLException {
		// verifica si hay OTRA actividad en la misma instalación, mismo día y rango
		// horario solapado
		String sql = "SELECT COUNT(*) FROM Horarios h " + "JOIN Actividades a ON h.id_actividad = a.id_actividad "
				+ "WHERE a.id_instalacion = ? AND h.dia_semana = ? "
				+ "AND ((h.hora_inicio < ? AND h.hora_fin > ?) OR (h.hora_inicio < ? AND h.hora_fin > ?))";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setInt(1, idInstalacion);
			pst.setString(2, h.getDiaSemana());
			pst.setString(3, h.getHoraFin());
			pst.setString(4, h.getHoraInicio());
			pst.setString(5, h.getHoraFin());
			pst.setString(6, h.getHoraInicio());
			ResultSet rs = pst.executeQuery();
			return rs.next() && rs.getInt(1) == 0;
		}
	}

	/**
	 * Centraliza todas las validaciones y acumula los mensajes de error.
	 */
	public void validarTodo(ActividadEntity a) {
		List<String> errores = new ArrayList<>();

		// Nombre
		if (a.getNombre() == null || a.getNombre().trim().isEmpty()) {
			errores.add("- El nombre es obligatorio.");
		}

		// Aforo
		if (a.getAforo() <= 0) {
			errores.add("- El aforo debe ser un número positivo.");
		}

		// Precios
		if (a.getPrecioSocio() < 0) {
			errores.add("- El precio de socio no puede ser negativo.");
		}
		if (a.getPrecioNoSocio() < a.getPrecioSocio()) {
			errores.add("- El precio para no socios no puede ser inferior al de socios.");
		}

		// IDs de combo boxes/selección
		if (a.getIdInstalacion() <= 0) {
			errores.add("- Debe seleccionar una instalación válida.");
		}
		if (a.getIdPeriodo() <= 0) {
			errores.add("- Debe seleccionar un periodo válido.");
		}

		// Fechas
		LocalDate inicio = null;
		LocalDate fin = null;

		if (a.getFechaInicio() == null || a.getFechaInicio().isEmpty()) {
			errores.add("- La fecha de inicio es obligatoria.");
		} else {
			try {
				inicio = LocalDate.parse(a.getFechaInicio());
				if (inicio.isBefore(LocalDate.now())) {
					errores.add("- La fecha de inicio no puede ser anterior a hoy.");
				}
			} catch (DateTimeParseException e) {
				errores.add("- Formato de fecha de inicio inválido (AAAA-MM-DD).");
			}
		}

		if (a.getFechaFin() == null || a.getFechaFin().isEmpty()) {
			errores.add("- La fecha de fin es obligatoria.");
		} else {
			try {
				fin = LocalDate.parse(a.getFechaFin());
			} catch (DateTimeParseException e) {
				errores.add("- Formato de fecha de fin inválido (AAAA-MM-DD).");
			}
		}

		if (inicio != null && fin != null && !fin.isAfter(inicio)) {
			errores.add("- La fecha de fin debe ser posterior a la de inicio.");
		}

		// Si hay errores, lanzamos la excepción con la lista unida por saltos de línea
		if (!errores.isEmpty()) {
			throw new ApplicationException("Se han detectado errores:\n" + String.join("\n", errores));
		}
	}

	// conecxion con la clase periodos inscripcion
	public List<Object[]> obtenerPeriodos() {
		String sql = "SELECT id_periodo, nombre FROM PeriodosInscripcion ORDER BY nombre";
		List<Object[]> res = db.executeQueryArray(sql);
		System.out.println("DEBUG: Periodos encontrados en la BD: " + res.size());
		return res;
	}

	// conecxion con la clase instalaciones
	public List<Object[]> obtenerInstalaciones() {
		String sql = "SELECT id_instalacion, nombre FROM Instalaciones ORDER BY nombre";
		List<Object[]> res = db.executeQueryArray(sql);
		System.out.println("DEBUG: Instalaciones encontradas en la BD: " + res.size());
		return res;
	}

	// conexion con la clase periodos inscripcion
	public Object[] obtenerDetallesPeriodo(int idPeriodo) {
		String sql = "SELECT inicio_socios, fin_socios, fin_no_socios FROM PeriodosInscripcion WHERE id_periodo = ?";
		List<Object[]> lista = db.executeQueryArray(sql, idPeriodo);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	private java.time.DayOfWeek traducirDiaSemana(String dia) {
	    switch (dia.toLowerCase()) {
	        case "lunes": return java.time.DayOfWeek.MONDAY;
	        case "martes": return java.time.DayOfWeek.TUESDAY;
	        case "miércoles": case "miercoles": return java.time.DayOfWeek.WEDNESDAY;
	        case "jueves": return java.time.DayOfWeek.THURSDAY;
	        case "viernes": return java.time.DayOfWeek.FRIDAY;
	        case "sábado": case "sabado": return java.time.DayOfWeek.SATURDAY;
	        case "domingo": return java.time.DayOfWeek.SUNDAY;
	        default: throw new IllegalArgumentException("Día no válido: " + dia);
	    }
	}
	
	private boolean isPistaLibreParaFecha(Connection conn, int idInstalacion, String fecha, String hIni, String hFin) throws SQLException {
	    String sql = "SELECT COUNT(*) FROM Reservas WHERE id_instalacion = ? AND fecha = ? " +
	                 "AND ((hora_inicio < ? AND hora_fin > ?))";
	    try (PreparedStatement pst = conn.prepareStatement(sql)) {
	        pst.setInt(1, idInstalacion);
	        pst.setString(2, fecha);
	        pst.setString(3, hFin);
	        pst.setString(4, hIni);
	        ResultSet rs = pst.executeQuery();
	        return rs.next() && rs.getInt(1) == 0;
	    }
	}
}
