package si.pl14.reservas;

import java.util.List;

import si.pl14.model.InstalacionEntity;
import si.pl14.model.ReservaEntity;
import si.pl14.model.SocioEntity;
import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

public class ReservaModel {
	
	private Database db = new Database();
	
	/*
	 * Obtiene la lista de instalaciones
	 */
	public List<InstalacionEntity> getListaInstalaciones() {
		
		//SQL para conseguir todas las filas de la tabla Instalaciones
		String sql = "SELECT  * FROM Instalaciones";
		
		return db.executeQueryPojo(InstalacionEntity.class, sql);
	}
	
	/*
	 * Comprueba que el socio está al corriente con los pagos
	 */
	public boolean estaAlCorriente(int idSocio) {
		
		String sql = "SELECT estado_pagos FROM Socios WHERE id_socio = ?";
		
		List<SocioEntity> socios = db.executeQueryPojo(SocioEntity.class, sql, idSocio);
		
		return "Al Corriente".equalsIgnoreCase(socios.get(0).getEstadoPagos());
	}
	
	/*
	 * Obtiene las reservas que hay en una fecha determinada, en una instalación
	 */
	public List<ReservaEntity> getReservasEnFecha(String fecha, int idInstalacion) {
		String sql = "SELECT * FROM Reservas WHERE fecha = ?"
				+ " AND id_instalacion = ?";
		
		return db.executeQueryPojo(ReservaEntity.class, sql, fecha, idInstalacion);
	}
	
	/*
	 * Comprueba las horas en las que hay reservas en una instalacion concreta para
	 * evitar solapamientos
	 */
	public boolean estaLibreAEsasHoras(String fecha, int horaInicio, int horaFin, int idInstalacion) {
		
		List<ReservaEntity> reservasEnFecha = getReservasEnFecha(fecha, idInstalacion);
		
		for (ReservaEntity reserva: reservasEnFecha) {
			if (horaInicio < reserva.getHoraFin() && horaFin > reserva.getHoraInicio()) {
				return false;
			}
		}
		return true;
	}
	/*
	 * Obtiene el precio de reservar una instalacion el número de horas dicho
	 */
	public float getPrecioReserva(ReservaEntity reserva) {
		String sql = "SELECT coste_hora FROM Instalaciones WHERE id_instalacion = ?";
		
		List<InstalacionEntity> instalaciones = db.executeQueryPojo(InstalacionEntity.class, sql, reserva.getIdInstalacion());
		
		if (instalaciones.isEmpty()) {
			System.err.println("Error en getPrecioReserva()");
			throw new ApplicationException("La instalación no existe: " + reserva.getIdInstalacion());
		}
		
		int horasReservas = reserva.getHoraFin() - reserva.getHoraInicio();
		
		return horasReservas * instalaciones.get(0).getCosteHora();
	}
	
	/*
	 * Realiza la reserva y la guarda en la base de datos
	 */
	public void realizarReserva(ReservaEntity reserva) {
	    String sql = "INSERT INTO Reservas (id_instalacion, id_socio, fecha, hora_inicio, hora_fin, estado_pago) " +
	                 "VALUES (?, ?, ?, ?, ?, ?)";

	    db.executeUpdate(sql, 
	        reserva.getIdInstalacion(),
	        reserva.getIdSocio(),
	        reserva.getFecha(),
	        reserva.getHoraInicio(),
	        reserva.getHoraFin(),
	        reserva.getEstadoPago()
	    );
	}
	
	/*
	 * Generar resguardo de la reserva
	 */
	public String generarResguardo(ReservaEntity reserva) {
		
		String resguardo = "--- RESGUARDO DE RESERVA ---" +
				"\n Instalación ID: " + reserva.getIdInstalacion() +
				"\n Para socio: " + reserva.getIdSocio() +
				"\n Par la fecha: " + reserva.getFecha() +
				"\n Hora inicio: " + reserva.getHoraInicio() +
				"\n Hora fin: " + reserva.getHoraFin() + 
				"\n Precio reserva: " + getPrecioReserva(reserva) + 
				"\n Pâgado: " + reserva.getEstadoPago();
		
		return resguardo;
	}
	
}
