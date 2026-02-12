package si.pl14.model;

public class ActividadEntity {
	private int idActividad;
    private String nombre;
    private String descripcion;
    private int idInstalacion;
    private int aforo;
    private String fechaInicio;
    private String fechaFin;    
    private double precioSocio;
    private double precioNoSocio;
    private int idPeriodo;
    
	public int getIdActividad() {
		return idActividad;
	}
	public void setIdActividad(int idActividad) {
		this.idActividad = idActividad;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getIdInstalacion() {
		return idInstalacion;
	}
	public void setIdInstalacion(int idInstalacion) {
		this.idInstalacion = idInstalacion;
	}
	public int getAforo() {
		return aforo;
	}
	public void setAforo(int aforo) {
		this.aforo = aforo;
	}
	public String getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public String getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}
	public double getPrecioSocio() {
		return precioSocio;
	}
	public void setPrecioSocio(double precioSocio) {
		this.precioSocio = precioSocio;
	}
	public double getPrecioNoSocio() {
		return precioNoSocio;
	}
	public void setPrecioNoSocio(double precioNoSocio) {
		this.precioNoSocio = precioNoSocio;
	}
	public int getIdPeriodo() {
		return idPeriodo;
	}
	public void setIdPeriodo(int idPeriodo) {
		this.idPeriodo = idPeriodo;
	}
}
