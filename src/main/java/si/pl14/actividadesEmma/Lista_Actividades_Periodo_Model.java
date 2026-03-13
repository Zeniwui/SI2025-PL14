package si.pl14.actividadesEmma;

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

	public List<ActividadDTO> obtenerActividadesPorRangoFechas(String inicio, String fin) {
		List<ActividadDTO> listaDTO = new ArrayList<>();
		String sql = "SELECT a.nombre, a.tipo, "
				+ " (SELECT GROUP_CONCAT(h.dia_semana || ' ' || h.hora_inicio || '-' || h.hora_fin, ', ') FROM Horarios h WHERE h.id_actividad = a.id_actividad) as horarios, "
				+ " a.fecha_inicio, a.fecha_fin, a.aforo, a.precio_socio, a.precio_no_socio, "
				+ " i.nombre as nombre_instalacion, "
				+ " (SELECT h.hora_inicio FROM Horarios h WHERE h.id_actividad = a.id_actividad LIMIT 1) as h_ini, "
				+ " (SELECT h.hora_fin FROM Horarios h WHERE h.id_actividad = a.id_actividad LIMIT 1) as h_fin "
				+ " FROM Actividades a " + " LEFT JOIN Instalaciones i ON a.id_instalacion = i.id_instalacion "
				+ " WHERE a.fecha_inicio >= ? AND a.fecha_fin <= ?";

		List<Object[]> resultados = db.executeQueryArray(sql, inicio, fin);

		for (Object[] fila : resultados) {
			listaDTO.add(new ActividadDTO(fila[0].toString(), fila[1].toString(),
					fila[2] != null ? fila[2].toString() : null, fila[3].toString(), fila[4].toString(),
					Integer.parseInt(fila[5].toString()), Double.parseDouble(fila[6].toString()),
					Double.parseDouble(fila[7].toString()), fila[8] != null ? fila[8].toString() : "Sin asignar",
					fila[9] != null ? fila[9].toString() : null, fila[10] != null ? fila[10].toString() : null));
		}
		return listaDTO;
	}
}