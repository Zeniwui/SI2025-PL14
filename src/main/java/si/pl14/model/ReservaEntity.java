package si.pl14.model;

public class ReservaEntity {
	private int idReserva;
	private int idInstalacion;
	private String fecha;
	private int horaInicio;
	private int horaFin;
	
	private Integer idSocio;
	private Integer idActividad;
	
	private float costeReserva;
	private String estadoPago;
	private String metodoPago;
	private String fechaCreacion;
	
	public int getIdReserva() {
		return idReserva;
	}
	public void setIdReserva(int idReserva) {
		this.idReserva = idReserva;
	}
	public int getIdInstalacion() {
		return idInstalacion;
	}
	public void setIdInstalacion(int idInstalacion) {
		this.idInstalacion = idInstalacion;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public int getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(int horaInicio) {
		this.horaInicio = horaInicio;
	}
	public int getHoraFin() {
		return horaFin;
	}
	public void setHoraFin(int horaFin) {
		this.horaFin = horaFin;
	}
	public Integer getIdSocio() {
		return idSocio;
	}
	public void setIdSocio(Integer idSocio) {
		this.idSocio = idSocio;
	}
	public Integer getIdActividad() {
		return idActividad;
	}
	public void setIdActividad(Integer idActividad) {
		this.idActividad = idActividad;
	}
	public float getCosteReserva() {
		return costeReserva;
	}
	public void setCosteReserva(float costeReserva) {
		this.costeReserva = costeReserva;
	}
	public String getEstadoPago() {
		return estadoPago;
	}
	public void setEstadoPago(String estadoPago) {
		this.estadoPago = estadoPago;
	}
	public String getMetodoPago() {
		return metodoPago;
	}
	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}
	public String getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

}
