package si.pl14.inscripcionesSocio;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import si.pl14.model.SocioEntity;
import si.pl14.model.ActividadInscripcionDTO;
import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

public class InscripcionSocioModel {
	
	private Database db = new Database(); 
	
	/*
	 * Obtiene las actividades cuyo periodo de inscripcion esta abierto en fecha actual
	 * */	
	public List<ActividadInscripcionDTO> getActividadesInscripcionActual() {
		String sql = "SELECT a.id_actividad AS idActividad, " +
                     "a.nombre AS nombre, " +
                     "i.nombre AS instalacion, " +
                     "(a.aforo - (SELECT COUNT(*) FROM Inscripciones ins WHERE ins.id_actividad = a.id_actividad)) AS plazasDisponibles, " +
                     "p.fin_socios AS finInscripcion, " +
                     "a.fecha_inicio AS fechaInicio, " +
                     "a.fecha_fin AS fechaFin, " +
                     "a.precio_socio AS precioSocio, " +
                     "a.descripcion AS descripcion " +
                     "FROM Actividades a " +
                     "INNER JOIN PeriodosInscripcion p ON a.id_periodo = p.id_periodo " +
                     "INNER JOIN Instalaciones i ON a.id_instalacion = i.id_instalacion " +
                     "WHERE CURRENT_DATE >= p.inicio_socios AND CURRENT_DATE <= p.fin_socios " +
                     "ORDER BY p.fin_socios ASC";
		
		return db.executeQueryPojo(ActividadInscripcionDTO.class, sql);
	}
	
	/*
	 * Comprueba que el socio esté al corriente con los pagos tanto de reservas como de actividades
	 * */
	public boolean estaAlCorriente (int idSocio) {
		String sql = "SELECT estado_pagos AS estadoPagos FROM Socios WHERE id_socio = ?";
		
		List<SocioEntity> socios = db.executeQueryPojo(SocioEntity.class, sql, idSocio);
		
		return "Al Corriente".equalsIgnoreCase(socios.get(0).getEstadoPagos());
	}
	
	/*
	 * Comprueba que un socio no este ya inscrito en la actividad que se le pasa por parametro
	 * */
	public boolean estaInscrito (int idSocio, int idActividad) {
		String sql = "SELECT COUNT(*) FROM Inscripciones WHERE id_socio = ? AND id_actividad = ?";
		List<Object[]> res = db.executeQueryArray(sql, idSocio, idActividad);
		return ((Number) res.get(0)[0]).intValue() > 0;
	}
	
	/*
	 * Realiza la inscripcion
	 * */
	public void realizarInscripcion(int idSocio, ActividadInscripcionDTO actividad) {
				// Comprobaciones para que el socio se pueda inscribir
				if (!estaAlCorriente(idSocio)) {
					throw new ApplicationException("No puedes inscribirte: No estás al corriente de pago.");
				}
				
				if (estaInscrito(idSocio, actividad.getIdActividad())) {
					throw new ApplicationException("Ya estás inscrito en esta actividad.");
				}
				
				if (actividad.getPlazasDisponibles() <= 0) {
					throw new ApplicationException("El aforo máximo para esta actividad se ha completado.");
				}
				
				// Insertamos la inscripción
				String sqlInscripcion = "INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion, precio_inscripcion) VALUES (?, ?, CURRENT_DATE, ?)";
				db.executeUpdate(sqlInscripcion, idSocio, actividad.getIdActividad(), actividad.getPrecioSocio());
				
				String sqlGetId = "SELECT last_insert_rowid()";
				List<Object[]> res = db.executeQueryArray(sqlGetId);
				int idInscripcion = ((Number) res.get(0)[0]).intValue();
				
				// Generamos el pago y lo insertamos
				String concepto = "Cuota actividad: " + actividad.getNombre();
				String metodoPago = "Cuota Mensual";
				
				String sqlPago = "INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion) VALUES (?, ?, ?, 'Pendiente', ?, ?)";
				db.executeUpdate(sqlPago, idSocio, actividad.getPrecioSocio(), metodoPago, concepto, idInscripcion);
				
	}
	
	/*
	 * Genera un resguardo con los datos de la inscripcion para el socio
	 * */
	public String generaResguardoInscripcion(String nombreSocio, String nombreActividad, double precio) {
		String fecha = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
		String resguardo = "=== RESGUARDO DE INSCRIPCIÓN ===\n" +
                           "Socio: " + nombreSocio + "\n" +
                           "Actividad: " + nombreActividad + "\n" +
                           "Cuota mensual añadida: " + precio + " €\n" +
                           "Fecha de operación: " + fecha + "\n" +
                           "===============================";
		return resguardo;
	}
	
	/*
	 * Obtiene el nombre completo de un socio por su ID
	 */
	public String getNombreSocioById(int idSocio) {
		String sql = "SELECT u.nombre, u.apellidos " +
                     "FROM Usuarios u " +
                     "INNER JOIN Socios s ON u.dni = s.dni " +
                     "WHERE s.id_socio = ?";
		
		List<Object[]> res = db.executeQueryArray(sql, idSocio);
		
		if (!res.isEmpty()) {
			return res.get(0)[0] + " " + res.get(0)[1];
		}
		return "Socio Desconocido";
	}
}
