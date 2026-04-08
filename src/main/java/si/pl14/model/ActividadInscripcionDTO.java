package si.pl14.model;

import java.util.Date;

public class ActividadInscripcionDTO {
    private int idActividad;
    private String nombre;
    private String instalacion;
    private int plazasDisponibles;
    private Date finInscripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private double precioSocio;
    private double precioNoSocio;
    private String descripcion;

    public int getIdActividad() { return idActividad; }
    public void setIdActividad(int idActividad) { this.idActividad = idActividad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getInstalacion() { return instalacion; }
    public void setInstalacion(String instalacion) { this.instalacion = instalacion; }
    public int getPlazasDisponibles() { return plazasDisponibles; }
    public void setPlazasDisponibles(int plazasDisponibles) { this.plazasDisponibles = plazasDisponibles; }
    public Date getFinInscripcion() { return finInscripcion; }
    public void setFinInscripcion(Date finInscripcion) { this.finInscripcion = finInscripcion; }
    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
    public double getPrecioSocio() { return precioSocio; }
    public void setPrecioSocio(double precioSocio) { this.precioSocio = precioSocio; }
    public double getPrecioNoSocio() {
        return precioNoSocio;
    }

    public void setPrecioNoSocio(double precioNoSocio) {
        this.precioNoSocio = precioNoSocio;
    }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
