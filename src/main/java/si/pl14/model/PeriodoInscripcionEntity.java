package si.pl14.model;

public class PeriodoInscripcionEntity {
	private int idPeriodo;
	private String nombre;
	private String inicioSocios;
	private String finSocios;
	private String finNoSocios;
	
	public int getIdPeriodo() {
		return idPeriodo;
	}
	public void setIdPeriodo(int idPeriodo) {
		this.idPeriodo = idPeriodo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getInicioSocios() {
		return inicioSocios;
	}
	public void setInicioSocios(String inicioSocios) {
		this.inicioSocios = inicioSocios;
	}
	public String getFinSocios() {
		return finSocios;
	}
	public void setFinSocios(String finSocios) {
		this.finSocios = finSocios;
	}
	public String getFinNoSocios() {
		return finNoSocios;
	}
	public void setFinNoSocios(String finNoSocios) {
		this.finNoSocios = finNoSocios;
	}
	
}
