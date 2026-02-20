package si.pl14.actividades;

public class ActividadDTO {

	// parametros
	private int id;
	private String nombre;
	private String descripcion;
	private int id_instalacion;
	private int aforo;
	private String fecha_inicio;
	private String fecha_fin;
	private float precio_socio;
	private float precio_no_socio;
	private int id_periodo;

	// constructor
	public ActividadDTO(int id, String nombre, String descripcion, int id_instalacion, int aforo, String fecha_inicio,
			String fecha_fin, float precio_socio, float precio_no_socio, int id_periodo) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.id_instalacion = id_instalacion;
		this.aforo = aforo;
		this.fecha_inicio = fecha_inicio;
		this.fecha_fin = fecha_fin;
		this.precio_socio = precio_socio;
		this.precio_no_socio = precio_no_socio;
		this.id_periodo = id_periodo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getId_instalacion() {
		return id_instalacion;
	}

	public void setId_instalacion(int id_instalacion) {
		this.id_instalacion = id_instalacion;
	}

	public int getAforo() {
		return aforo;
	}

	public void setAforo(int aforo) {
		this.aforo = aforo;
	}

	public String getFecha_inicio() {
		return fecha_inicio;
	}

	public void setFecha_inicio(String fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}

	public String getFecha_fin() {
		return fecha_fin;
	}

	public void setFecha_fin(String fecha_fin) {
		this.fecha_fin = fecha_fin;
	}

	public float getPrecio_socio() {
		return precio_socio;
	}

	public void setPrecio_socio(float precio_socio) {
		this.precio_socio = precio_socio;
	}

	public float getPrecio_no_socio() {
		return precio_no_socio;
	}

	public void setPrecio_no_socio(float precio_no_socio) {
		this.precio_no_socio = precio_no_socio;
	}

	public int getId_periodo() {
		return id_periodo;
	}

	public void setId_periodo(int id_periodo) {
		this.id_periodo = id_periodo;
	}

	

}
