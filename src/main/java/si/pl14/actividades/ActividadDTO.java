package si.pl14.actividades;

public class ActividadDTO {
    private String nombre, tipo, horarios, fechaInicio, fechaFin, hIni, hFin, instalacion;
    private int plazas;
    private double precioSocio, precioNoSocio;

    public ActividadDTO(String nombre, String tipo, String horarios, String fechaInicio, 
                        String fechaFin, int plazas, double precioSocio, 
                        double precioNoSocio, String instalacion, String hIni, String hFin) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.horarios = horarios;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.plazas = plazas;
        this.precioSocio = precioSocio;
        this.precioNoSocio = precioNoSocio;
        this.instalacion = (instalacion != null) ? instalacion : "No asignada";
        this.hIni = hIni;
        this.hFin = hFin;
    }

    public String getDuracion() {
        try {
            // El error solía dar aquí si hIni o hFin llegaban vacíos o en índices incorrectos
            if (hIni == null || hFin == null || hIni.isEmpty()) return "N/A";
            long minutos = java.time.Duration.between(java.time.LocalTime.parse(hIni), java.time.LocalTime.parse(hFin)).toMinutes();
            return (minutos / 60) + "h " + (minutos % 60) + "min";
        } catch (Exception e) { return "N/A"; }
    }

    public Object[] toArray() {
        return new Object[]{
            nombre,
            tipo,
            instalacion,      // <--- Añadido a la tabla
            getDuracion(),
            horarios != null ? horarios : "Sin horario",
            fechaInicio,
            fechaFin,
            plazas,
            String.format("%.2f€", precioSocio),
            String.format("%.2f€", precioNoSocio)
        };
    }
    
    // Getters necesarios para el controller...
    public String getNombre() { return nombre; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public String gethIni() { return hIni; }
    public String gethFin() { return hFin; }
}
