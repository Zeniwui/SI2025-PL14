package si.pl14.reservasAdmin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import si.pl14.model.InstalacionEntity;
import si.pl14.model.ReservaEntity;
import si.pl14.model.SocioDTO;
import si.pl14.model.SocioEntity;
import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

public class ReservaModelAdmin {
	
	private final int HORA_APERTURA = 9;
	private final int HORA_CIERRE = 21;
	private final int HORAS_MAXIMAS_SEGUIDAS = 2;
	private final int HORAS_MAXIMAS_DIA = 3;
	private final int HORAS_MAXIMAS_MES = 8;
	private final int DIAS_MAXIMOS_ANTELACION = 30;
	
	private Database db = new Database();
	
	public int getHoraApertura() {
		return HORA_APERTURA;
	}

	public int getHoraCierre() {
		return HORA_CIERRE;
	}
	
	public int getDiasMaximosAntelacion() {
		return DIAS_MAXIMOS_ANTELACION;
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
	
	public List<Object[]> getListaInstalacionesArray() {
		
		String sql = "SELECT  * FROM Instalaciones";
		
		return db.executeQueryArray(sql);
	}
	
	/*
	 * Comprueba que el socio está al corriente con los pagos
	 */
	public boolean estaAlCorriente(int idSocio) {
		
		String sql = "SELECT estado_pagos AS estadoPagos FROM Socios WHERE id_socio = ?";
		
		List<SocioEntity> socios = db.executeQueryPojo(SocioEntity.class, sql, idSocio);
		
		return "Al Corriente".equalsIgnoreCase(socios.get(0).getEstadoPagos());
	}
	
	/*
	 * Obtiene las reservas que hay en una fecha determinada, en una instalación
	 */
	public List<ReservaEntity> getReservasEnFecha(String fecha, int idInstalacion) {
		String sql = "SELECT " +
                "id_reserva AS idReserva, " +
                "id_instalacion AS idInstalacion, " +
                "id_socio AS idSocio, " +
                "fecha, " +
                "CAST(strftime('%H', hora_inicio) AS INTEGER) AS horaInicio, " +
                "CAST(strftime('%H', hora_fin) AS INTEGER) AS horaFin, " +
                "coste_reserva AS costeReserva, " +
                "estado_pago AS estadoPago, " +
                "metodo_pago AS metodoPago " +
                "FROM Reservas " +
                "WHERE fecha = ? AND id_instalacion = ?";
		
		return db.executeQueryPojo(ReservaEntity.class, sql, fecha, idInstalacion);
	}
	
	/*
	 * Obtiene las reservas de un socio en una fecha concreta
	 */
	public List<ReservaEntity> getReservasSocioEnDia(int idSocio, String fecha) {
		String sql = "SELECT id_reserva AS idReserva, id_instalacion AS idInstalacion, " +
                "id_socio AS idSocio, fecha, " +
                "CAST(strftime('%H', hora_inicio) AS INTEGER) AS horaInicio, " +
                "CAST(strftime('%H', hora_fin) AS INTEGER) AS horaFin, " +
                "coste_reserva AS costeReserva, estado_pago AS estadoPago, metodo_pago AS metodoPago " +
                "FROM Reservas WHERE id_socio = ? AND fecha = ?";
		
		return db.executeQueryPojo(ReservaEntity.class, sql, idSocio, fecha);
	}
	
	/*
	 * Obtiene las reservas de un socio en un mes concreto (formato YYYY-MM)
	 */
	public List<ReservaEntity> getReservasSocioEnMes(int idSocio, String mesAnio) {
		String sql = "SELECT id_reserva AS idReserva, id_instalacion AS idInstalacion, " +
                "id_socio AS idSocio, fecha, " +
                "CAST(strftime('%H', hora_inicio) AS INTEGER) AS horaInicio, " +
                "CAST(strftime('%H', hora_fin) AS INTEGER) AS horaFin, " +
                "coste_reserva AS costeReserva, estado_pago AS estadoPago, metodo_pago AS metodoPago " +
                "FROM Reservas WHERE id_socio = ? AND fecha LIKE ?";
		
		return db.executeQueryPojo(ReservaEntity.class, sql, idSocio, mesAnio + "%");
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
	public float getPrecioReserva(int idInstalacion, int horaInicio, int horaFin) {
		String sql = "SELECT coste_hora AS costeHora FROM Instalaciones WHERE id_instalacion = ?";
		
		List<InstalacionEntity> instalaciones = db.executeQueryPojo(InstalacionEntity.class, sql, idInstalacion);
		
		if (instalaciones.isEmpty()) {
			System.err.println("Error en getPrecioReserva()");
			throw new ApplicationException("La instalación no existe: " + idInstalacion);
		}
		
		int horasReservas = horaFin - horaInicio;
		
		return horasReservas * instalaciones.get(0).getCosteHora();
	}
	
	/*
	 * Comprueba que la reserva no supere las horas seguidas preestablecidas
	 */
	public boolean superaHorasSeguidas(List<ReservaEntity> reservasDia, int nuevaHoraInicio, int nuevaHoraFin) {
		boolean[] horasActivas = new boolean[24];
		
		for (ReservaEntity r : reservasDia) {
			for (int i = r.getHoraInicio(); i < r.getHoraFin(); i++) {
				horasActivas[i] = true;
			}
		}
		for (int i = nuevaHoraInicio; i < nuevaHoraFin; i++) {
			horasActivas[i] = true;
		}
		
		int maxConsecutivas = 0;
		int actualesConsecutivas = 0;
		for (int i = 0; i < 24; i++) {
			if (horasActivas[i]) {
				actualesConsecutivas++;
				if (actualesConsecutivas > maxConsecutivas) {
					maxConsecutivas = actualesConsecutivas;
				}
			} else {
				actualesConsecutivas = 0;
			}
		}
		return maxConsecutivas > HORAS_MAXIMAS_SEGUIDAS;
	}
	
	/*
	 * Valida que se cumplan todas las condiciones
	 */
	public String validarReglas(int idSocio, Date fechaDate, int horaInicio, int horaFin, int idInstalacion) {
		
		// 1. Cálculos de tiempo
		LocalDate fechaSeleccionadaLocal = fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate hoy = LocalDate.now();
		long diasAntelacion = ChronoUnit.DAYS.between(hoy, fechaSeleccionadaLocal);
		int horaActual = LocalTime.now().getHour();
		
		String fechaSeleccionada = si.pl14.util.Util.dateToIsoString(fechaDate);
		String mesAnio = fechaSeleccionada.substring(0, 7); 
		
		// 2. Extraer datos de la BD
		List<ReservaEntity> reservasSocioDia = getReservasSocioEnDia(idSocio, fechaSeleccionada);
		List<ReservaEntity> reservasSocioMes = getReservasSocioEnMes(idSocio, mesAnio);
		
		int horasAReservar = horaFin - horaInicio;
		
		int horasYaReservadasDia = 0;
		for (ReservaEntity r : reservasSocioDia) { horasYaReservadasDia += (r.getHoraFin() - r.getHoraInicio()); }

		int horasYaReservadasMes = 0;
		for (ReservaEntity r : reservasSocioMes) { horasYaReservadasMes += (r.getHoraFin() - r.getHoraInicio()); }

		// 3. Aplicar las reglas de negocio en orden
		if (!estaAlCorriente(idSocio)) return "SOCIO CON PAGOS PENDIENTES. NO PUEDE RESERVAR";
		if (diasAntelacion < 0) return "NO SE PUEDE RESERVAR EN FECHAS PASADAS";
		if (diasAntelacion == 0 && horaInicio <= horaActual) return "NO SE PUEDE RESERVAR EN UNA HORA QUE YA HA PASADO";
		if (diasAntelacion > DIAS_MAXIMOS_ANTELACION) return "SÓLO SE PUEDE RESERVAR CON " + DIAS_MAXIMOS_ANTELACION + " DÍAS DE ANTELACIÓN";
		if (horasAReservar > HORAS_MAXIMAS_SEGUIDAS) return "NO SE PERMITE RESERVAR MAS DE " + HORAS_MAXIMAS_SEGUIDAS + " HORAS SEGUIDAS";
		if (superaHorasSeguidas(reservasSocioDia, horaInicio, horaFin)) return "NO SE PERMITE RESERVAR MÁS DE " + HORAS_MAXIMAS_SEGUIDAS + " HORAS SEGUIDAS";
		if ((horasYaReservadasDia + horasAReservar) > HORAS_MAXIMAS_DIA) return "LÍMITE DIARIO: NO SE PUEDEN RESERVAR MÁS DE " + HORAS_MAXIMAS_DIA + " HORAS AL DÍA";
		if ((horasYaReservadasMes + horasAReservar) > HORAS_MAXIMAS_MES) return "LÍMITE MENSUAL: NO SE PUEDEN RESERVAR MÁS DE " + HORAS_MAXIMAS_MES + " HORAS AL MES";
		if (!estaLibreAEsasHoras(fechaSeleccionada, horaInicio, horaFin, idInstalacion)) return "LA INSTALACIÓN NO ESTÁ DISPONIBLE EN ESE HORARIO";

		// Si pasa todos los IF, la reserva es válida
		return null;
	}
	
	/*
	 * Realiza la reserva y la guarda en la base de datos
	 */
	public void realizarReserva(ReservaEntity reserva, String nombreInstalacion) {
		String sql = "INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	    
	    String horaInicioStr = String.format("%02d:00", reserva.getHoraInicio());
	    String horaFinStr = String.format("%02d:00", reserva.getHoraFin());

	    db.executeUpdate(sql, 
	        reserva.getIdInstalacion(),
	        reserva.getFecha(),
	        horaInicioStr,
	        horaFinStr,
	        reserva.getIdSocio(),
	        reserva.getCosteReserva(),
	        reserva.getEstadoPago(),
	        reserva.getMetodoPago()
	    );
	    
	    String sqlGetId = "SELECT MAX(id_reserva) FROM Reservas";
		List<Object[]> res = db.executeQueryArray(sqlGetId);
		int idReserva = ((Number) res.get(0)[0]).intValue();
		
		// 3. Insertar el registro en la tabla Pagos asociado a esta reserva
		String concepto = "Reserva instalación: " + nombreInstalacion;
		
		String sqlPago = "INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_reserva) " +
				"VALUES (?, ?, ?, ?, ?, ?)";
		
		db.executeUpdate(sqlPago, 
				reserva.getIdSocio(),
				reserva.getCosteReserva(),
				reserva.getMetodoPago(),
				reserva.getEstadoPago(), // "Pagado" o "Pendiente" según lo que eligió el admin
				concepto,
				idReserva
		);
	}
	
	/*
	 * Generar resguardo de la reserva
	 */
	public String generarResguardo(ReservaEntity reserva, String nombreInstalacion, String nombreSocio) {
		
		String resguardo = "--- RESGUARDO DE RESERVA ---" +
		        "\n Instalación: " + nombreInstalacion +
		        "\n Para socio: " + nombreSocio +
		        "\n Para la fecha: " + reserva.getFecha() +
		        "\n Hora inicio: " + reserva.getHoraInicio() +
		        "\n Hora fin: " + reserva.getHoraFin() + 
		        "\n Precio reserva: " + String.format("%.2f €", reserva.getCosteReserva()) + 
		        "\n Método pago: " + reserva.getMetodoPago();
		
		return resguardo;
	}
	
	/*
	 * Obtiene el socio asociado al DNI pasado por parámetro
	 */
	public SocioDTO getSocioByDni (String dni) {
		
		String sql = "SELECT " +
	             "s.id_socio AS idSocio, " +
	             "u.nombre AS nombre, " +
	             "u.apellidos AS apellidos, " +
	             "s.estado_pagos AS estadoPagos " +
	             "FROM Socios s " +
	             "INNER JOIN Usuarios u ON s.dni = u.dni " +
	             "WHERE u.dni = ?";
		
		List<SocioDTO> socios = db.executeQueryPojo(SocioDTO.class, sql, dni);
		
		if (socios.isEmpty()) {
			return null; 
		}
		
		return socios.get(0);		
	}
	
}
