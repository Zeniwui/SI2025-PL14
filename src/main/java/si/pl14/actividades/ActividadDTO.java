package si.pl14.actividades;

import java.time.LocalTime;
import java.time.Duration;

public class ActividadDTO {
    private String nombre;
    private String tipo; // "Deporte", "Conferencia", etc.
    private String horarios;
    private String fechaInicio;
    private String fechaFin;
    private int plazas;
    private double precioSocio;
    private double precioNoSocio;
    private String hIni; 
    private String hFin;

    public ActividadDTO(String nombre, String tipo, String horarios, String fechaInicio, 
                        String fechaFin, int plazas, double precioSocio, 
                        double precioNoSocio, String hIni, String hFin) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.horarios = horarios;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.plazas = plazas;
        this.precioSocio = precioSocio;
        this.precioNoSocio = precioNoSocio;
        this.hIni = hIni;
        this.hFin = hFin;
    }

    public String getDuracion() {
        try {
            if (hIni == null || hFin == null) return "N/A";
            long minutos = Duration.between(LocalTime.parse(hIni), LocalTime.parse(hFin)).toMinutes();
            long horas = minutos / 60;
            long resto = minutos % 60;
            return (horas > 0 ? horas + "h " : "") + (resto > 0 ? resto + "min" : "");
        } catch (Exception e) { return "Error"; }
    }

    public Object[] toArray() {
        return new Object[]{
            nombre,
            tipo,
            getDuracion(),
            horarios != null ? horarios : "Sin horario",
            fechaInicio,
            fechaFin,
            plazas,
            String.format("%.2f€", precioSocio),
            String.format("%.2f€", precioNoSocio)
        };
    }
}
