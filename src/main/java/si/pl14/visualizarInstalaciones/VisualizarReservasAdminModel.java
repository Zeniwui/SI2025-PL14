package si.pl14.visualizarInstalaciones;

import java.util.List;

import si.pl14.model.InstalacionEntity;
import si.pl14.model.ReservaDTO;
import si.pl14.util.Database;

public class VisualizarReservasAdminModel {
	
	private final int HORA_APERTURA = 9;
	private final int HORA_CIERRE = 21;
	
	private Database db = new Database();
	
	/*
	 * Obtiene la hora de apertura del centro deportivo
	 */
	public int getHoraApertura() {
		return HORA_APERTURA; 
	}
	
	/*
	 * Obtiene la hora de cierre del centro deportivo
	 */
	public int getHoraCierre() {
		return HORA_CIERRE; 
	}
	
	/*
	 * Obtiene la lista de instalaciones
	 */
	public List<InstalacionEntity> getListaInstalaciones() {
		
		//SQL para conseguir todas las filas de la tabla Instalaciones
		String sql = "SELECT id_instalacion AS idInstalacion, " + 
                "nombre, tipo, " +
                "coste_hora AS costeHora " + 
                "FROM Instalaciones";
		
		
		return db.executeQueryPojo(InstalacionEntity.class, sql);
	}
	
	/*
	 * Obtiene las reservas que hay en un perido establecido
	 */
	public List<ReservaDTO> getReservasEnPeriodo(int idInstalacion, String fechaInicio, String fechaFin) {
		String sql = "SELECT " +
				"r.fecha AS fecha, " +
				"CAST(strftime('%H', r.hora_inicio) AS INTEGER) AS horaInicio, " +
				"CAST(strftime('%H', r.hora_fin) AS INTEGER) AS horaFin, " +
				"i.nombre AS nombreInstalacion, " +
				"(u.nombre || ' ' || u.apellidos) AS nombreSocio, " +
				"a.nombre AS nombreActividad " +
				"FROM Reservas r " +
				"INNER JOIN Instalaciones i ON r.id_instalacion = i.id_instalacion " +
				"LEFT JOIN Socios s ON r.id_socio = s.id_socio " +
				"LEFT JOIN Usuarios u ON s.dni = u.dni " +
				"LEFT JOIN Actividades a ON r.id_actividad = a.id_actividad " +
				"WHERE r.id_instalacion = ? " +
				"AND r.fecha >= ? AND r.fecha <= ?";
		
		return db.executeQueryPojo(ReservaDTO.class, sql, idInstalacion, fechaInicio, fechaFin);
	}
}