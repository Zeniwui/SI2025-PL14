package si.pl14.visualizareservas;

public class ReservasSocioDTO {

    private int    idReserva;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String estadoPago;
    private String metodoPago;
    private double costeReserva;
    private String fechaCreacion;
    private String nombreInstalacion;

    public int    getIdReserva()          { return idReserva; }
    public String getFecha()              { return fecha; }
    public String getHoraInicio()         { return horaInicio; }
    public String getHoraFin()            { return horaFin; }
    public String getEstadoPago()         { return estadoPago; }
    public String getMetodoPago()         { return metodoPago; }
    public double getCosteReserva()       { return costeReserva; }
    public String getFechaCreacion()      { return fechaCreacion; }
    public String getNombreInstalacion()  { return nombreInstalacion; }

    public void setIdReserva(int idReserva)                    { this.idReserva = idReserva; }
    public void setFecha(String fecha)                         { this.fecha = fecha; }
    public void setHoraInicio(String horaInicio)               { this.horaInicio = horaInicio; }
    public void setHoraFin(String horaFin)                     { this.horaFin = horaFin; }
    public void setEstadoPago(String estadoPago)               { this.estadoPago = estadoPago; }
    public void setMetodoPago(String metodoPago)               { this.metodoPago = metodoPago; }
    public void setCosteReserva(double costeReserva)           { this.costeReserva = costeReserva; }
    public void setFechaCreacion(String fechaCreacion)         { this.fechaCreacion = fechaCreacion; }
    public void setNombreInstalacion(String nombreInstalacion) { this.nombreInstalacion = nombreInstalacion; }
}