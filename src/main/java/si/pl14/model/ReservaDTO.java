package si.pl14.model;

public class ReservaDTO {
	private String fecha;
	private int horaInicio;
	private int horaFin;
	
	private String nombreInstalacion;
	private String nombreSocio;
	private String nombreActividad;
	
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
	public String getNombreInstalacion() {
		return nombreInstalacion;
	}
	public void setNombreInstalacion(String nombreInstalacion) {
		this.nombreInstalacion = nombreInstalacion;
	}
	public String getNombreSocio() {
		return nombreSocio;
	}
	public void setNombreSocio(String nombreSocio) {
		this.nombreSocio = nombreSocio;
	}
	public String getNombreActividad() {
		return nombreActividad;
	}
	public void setNombreActividad(String nombreActividad) {
		this.nombreActividad = nombreActividad;
	}
}
