package si.pl14.actividades;

import java.util.ArrayList;
import java.util.List;
import si.pl14.util.Database;

public class Lista_Actividades_Periodo_Model {
	private Database db = new Database();

	public Lista_Actividades_Periodo_Model() {
		db.createDatabase(true);
		db.executeScript("src/main/resources/data.sql");
	}

	public List<Object[]> obtenerPeriodos() {
		return db.executeQueryArray("SELECT id_periodo, nombre FROM PeriodosInscripcion");
	}

	public List<ActividadDTO> obtenerActividadesPorPeriodoDTO(int idPeriodo) {
		List<ActividadDTO> listaDTO = new ArrayList<>();
		String sql = "SELECT a.nombre, a.tipo, " 
			    + " (SELECT GROUP_CONCAT(h.dia_semana || ' ' || h.hora_inicio || '-' || h.hora_fin, ', ') FROM Horarios h WHERE h.id_actividad = a.id_actividad) as horarios, " 
			    + " a.fecha_inicio, a.fecha_fin, a.aforo, a.precio_socio, a.precio_no_socio, " 
			    + " i.nombre as nombre_instalacion, " 
			    + " (SELECT h.hora_inicio FROM Horarios h WHERE h.id_actividad = a.id_actividad LIMIT 1) as h_ini, " 
			    + " (SELECT h.hora_fin FROM Horarios h WHERE h.id_actividad = a.id_actividad LIMIT 1) as h_fin " 
			    + " FROM Actividades a "
			    + " LEFT JOIN Instalaciones i ON a.id_instalacion = i.id_instalacion "
			    + " WHERE a.id_periodo = ?";

		List<Object[]> resultados = db.executeQueryArray(sql, idPeriodo);

		for (Object[] fila : resultados) {
		    listaDTO.add(new ActividadDTO(
		            fila[0].toString(), // nombre
		            fila[1].toString(), // tipo
		            fila[2] != null ? fila[2].toString() : null, // horarios
		            fila[3].toString(), // fecha_inicio
		            fila[4].toString(), // fecha_fin
		            Integer.parseInt(fila[5].toString()), // aforo
		            Double.parseDouble(fila[6].toString()), // precio_socio
		            Double.parseDouble(fila[7].toString()), // precio_no_socio
		            fila[8] != null ? fila[8].toString() : "Sin asignar", // instalacion (NUEVO)
		            fila[9] != null ? fila[9].toString() : null,  // h_ini (Antes era 8, ahora es 9)
		            fila[10] != null ? fila[10].toString() : null // h_fin (Antes era 9, ahora es 10)
		        ));
		    
		}
		return listaDTO;
	}
}