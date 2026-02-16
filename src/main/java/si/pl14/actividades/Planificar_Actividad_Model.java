package si.pl14.actividades;

import si.pl14.model.ActividadEntity;
import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

/**
 * Acceso a los datos de actividades.
 * Implementa la lógica de persistencia para la planificación de actividades
 * siguiendo el patrón del ejemplo CarrerasModel.
 */
public class Planificar_Actividad_Model {

	private Database db = new Database();

	/**
	 * inserta una nueva actividad en la base de datos y
	 * comprueba que los campos obligatorios no sean nulos y que la lógica esté bien
	 */
	public void insertarActividad(ActividadEntity actividad) {
		
		// validar condiciones
		validateNotNull(actividad.getNombre(), "El nombre de la actividad no puede estar vacío");
		validateCondition(actividad.getAforo() > 0, "El aforo debe ser un número positivo");
		validateCondition(actividad.getPrecioSocio() >= 0, "El precio de socio no puede ser negativo");
		validateCondition(actividad.getPrecioNoSocio() >= actividad.getPrecioSocio(), 
				"El precio para no socios no debería ser inferior al de socios");
		
		//insertar en sql
		String sql = "INSERT INTO Actividades (nombre, descripcion, id_instalacion, aforo, "
				+ "fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	// --- métodos auxiliares de validación (replicados del ejemplo de referencia)

	private void validateNotNull(Object obj, String message) {
		if (obj == null || (obj instanceof String && ((String) obj).trim().isEmpty())) {
			throw new ApplicationException(message);
		}
	}

	private void validateCondition(boolean condition, String message) {
		if (!condition) {
			throw new ApplicationException(message);
		}
	}
}
