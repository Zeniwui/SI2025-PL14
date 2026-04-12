package si.pl14.actividadesEmma;

//DTO específico para la tabla de la IU (HU cancelacion actividades)
public class ActividadResumenDTO {
    private int idActividad;
    private String nombre;
    private int inscritos;
    private int plazas;
    private String instalacion;

    public int getIdActividad() { return idActividad; }
    public void setIdActividad(int id) { this.idActividad = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String n) { this.nombre = n; }
    public int getInscritos() { return inscritos; }
    public void setInscritos(int i) { this.inscritos = i; }
    public int getPlazas() { return plazas; }
    public void setPlazas(int p) { this.plazas = p; }
    public String getInstalacion() { return instalacion; }
    public void setInstalacion(String i) { this.instalacion = i; }
}