package si.pl14.model;

public class SocioDTO {
	private int idSocio;
    private String nombre;
    private String apellidos;
    private String estadoPagos;
    
	public int getIdSocio() {
		return idSocio;
	}
	public void setIdSocio(int idSocio) {
		this.idSocio = idSocio;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getEstadoPagos() {
		return estadoPagos;
	}
	public void setEstadoPagos(String estadoPagos) {
		this.estadoPagos = estadoPagos;
	}
}
