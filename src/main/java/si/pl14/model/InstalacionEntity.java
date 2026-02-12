package si.pl14.model;

public class InstalacionEntity {
	private int idInstalacion;
	private String nombre;
	private String tipo;
	private float costeHora;
	
	public int getIdInstalacion() {
		return idInstalacion;
	}
	public void setIdInstalacion(int idInstalacion) {
		this.idInstalacion = idInstalacion;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public float getCosteHora() {
		return costeHora;
	}
	public void setCosteHora(float costeHora) {
		this.costeHora = costeHora;
	}
}
