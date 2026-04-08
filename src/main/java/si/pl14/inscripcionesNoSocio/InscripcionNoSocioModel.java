package si.pl14.inscripcionesNoSocio;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import si.pl14.model.ActividadInscripcionDTO;
import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

public class InscripcionNoSocioModel {
	
	private Database db = new Database(); 
	
	/*
	 * Obtiene las actividades cuyo periodo de inscripcion esta abierto para NO SOCIOS en fecha actual
	 */
	public List<ActividadInscripcionDTO> getActividadesInscripcionActual() {
		String sql = "SELECT a.id_actividad AS idActividad, " +
                     "a.nombre AS nombre, " +
                     "i.nombre AS instalacion, " +
                     "(a.aforo - (SELECT COUNT(*) FROM Inscripciones ins WHERE ins.id_actividad = a.id_actividad)) AS plazasDisponibles, " +
                     "p.fin_no_socios AS finInscripcion, " +
                     "a.fecha_inicio AS fechaInicio, " +
                     "a.fecha_fin AS fechaFin, " +
                     "a.precio_no_socio AS precioNoSocio, " +
                     "a.descripcion AS descripcion " +
                     "FROM Actividades a " +
                     "INNER JOIN PeriodosInscripcion p ON a.id_periodo = p.id_periodo " +
                     "INNER JOIN Instalaciones i ON a.id_instalacion = i.id_instalacion " +
                     "WHERE CURRENT_DATE > p.fin_socios AND CURRENT_DATE <= p.fin_no_socios " +
                     "ORDER BY p.fin_no_socios ASC";
		
		return db.executeQueryPojo(ActividadInscripcionDTO.class, sql);
	}
	/*
	 * Realiza la inscripcio a la actividad gestionando posibles problemas
	 */
	public void realizarInscripcion(String dni, String nombre, String apellidos, int telefono, String email, ActividadInscripcionDTO actividad) {
		if (actividad.getPlazasDisponibles() <= 0) {
			throw new ApplicationException("El aforo máximo para esta actividad se ha completado.");
		}

		// 1. Gestionar el Usuario
		if (!existeUsuario(dni)) {
			db.executeUpdate("INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES (?, ?, ?, ?, ?)", 
					dni, nombre, apellidos, telefono, email);
		}
		
		// 2. Gestionar el NoSocio
		int idNoSocio = obtenerIdNoSocio(dni);
		if (idNoSocio == -1) {
			db.executeUpdate("INSERT INTO NoSocios (dni) VALUES (?)", dni);
			idNoSocio = obtenerIdNoSocio(dni);
		}
		
		if (estaInscrito(idNoSocio, actividad.getIdActividad())) {
			throw new ApplicationException("Este usuario ya está inscrito en esta actividad.");
		}
		
		// 3. Insertar Inscripción
		String sqlInscripcion = "INSERT INTO Inscripciones (id_no_socio, id_actividad, fecha_inscripcion, precio_inscripcion) VALUES (?, ?, CURRENT_DATE, ?)";
		db.executeUpdate(sqlInscripcion, idNoSocio, actividad.getIdActividad(), actividad.getPrecioNoSocio());
		
		int idInscripcion = ((Number) db.executeQueryArray("SELECT last_insert_rowid()").get(0)[0]).intValue();
		
		// 4. Insertar en la tabla de pagos del no socio
		String concepto = "Inscripción No Socio: " + actividad.getNombre();
		String sqlPago = "INSERT INTO PagosNoSocios (id_no_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion) VALUES (?, ?, 'Tarjeta/Efectivo', 'Pagado', ?, ?)";
		db.executeUpdate(sqlPago, idNoSocio, actividad.getPrecioNoSocio(), concepto, idInscripcion);
	}

	/*
	 * Comprueba si un no socio ya esta registrado en la base de datos
	 */
	private boolean existeUsuario(String dni) {
		return !db.executeQueryArray("SELECT dni FROM Usuarios WHERE dni = ?", dni).isEmpty();
	}

	/*
	 * Obtiene el id del socio a partir de su dni
	 */
	private int obtenerIdNoSocio(String dni) {
		List<Object[]> res = db.executeQueryArray("SELECT id_no_socio FROM NoSocios WHERE dni = ?", dni);
		return res.isEmpty() ? -1 : ((Number) res.get(0)[0]).intValue();
	}
	
	/*
	 * Comprueba si un no socio esta inscrito ya en la actividad que se le pasa por parametro
	 */
	private boolean estaInscrito(int idNoSocio, int idActividad) {
		List<Object[]> res = db.executeQueryArray("SELECT COUNT(*) FROM Inscripciones WHERE id_no_socio = ? AND id_actividad = ?", idNoSocio, idActividad);
		return ((Number) res.get(0)[0]).intValue() > 0;
	}
	
	/*
	 * Genera un resguardo con los datos de la inscripcion para el no socio
	 */
	public String generaResguardoInscripcion(String nombreCompleto, String nombreActividad, double precio) {
		String fecha = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
		return "=== RESGUARDO DE INSCRIPCIÓN (NO SOCIO) ===\n" +
               "Participante: " + nombreCompleto + "\n" +
               "Actividad: " + nombreActividad + "\n" +
               "Importe Abonado: " + precio + " €\n" +
               "Estado: PAGADO\n" +
               "Fecha de operación: " + fecha + "\n" +
               "=======================================";
	}
}