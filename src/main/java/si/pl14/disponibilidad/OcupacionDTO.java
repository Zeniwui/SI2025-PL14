package si.pl14.disponibilidad;

/**
 * DTO que representa una franja horaria ocupada en una instalacion.
 * Usado por DisponibilidadModel y DisponibilidadController.
 */
public class OcupacionDTO {

    public int     horaInicio;
    public int     horaFin;
    public String  descripcion;

    
    /** true = reserva de socio (propio o ajeno), false = actividad */
    public boolean esReservaSocio;

    /** true = esta reserva pertenece al socio actual (ID_SOCIO_ACTUAL) */
    public boolean esMiaReserva;
}
