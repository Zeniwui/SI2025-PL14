package si.pl14.informeSocios;

/**
 * DTO que transporta los datos de un socio desde el Modelo a la Vista.
 *
 * Los campos se declaran {@code final} para garantizar la inmutabilidad:
 * una vez construido el DTO, ningún método puede modificar sus valores
 * accidentalmente, por ejemplo durante la ordenación ({@code aplicarOrden})
 * en el Controlador.
 */
public class InformeDetalladoSocioDTO {

    private final String nombreSocio;
    private final int    numReservas;
    private final int    numActividades;
    private final double deuda;
    private final String instalacionFavorita;

    public InformeDetalladoSocioDTO(String nombreSocio, int numReservas,
                                     int numActividades, double deuda,
                                     String instalacionFavorita) {
        this.nombreSocio         = nombreSocio;
        this.numReservas         = numReservas;
        this.numActividades      = numActividades;
        this.deuda               = deuda;
        this.instalacionFavorita = instalacionFavorita;
    }

    public String getNombreSocio()         { return nombreSocio;        }
    public int    getNumReservas()         { return numReservas;        }
    public int    getNumActividades()      { return numActividades;     }
    public double getDeuda()               { return deuda;              }
    public String getInstalacionFavorita() { return instalacionFavorita;}
}