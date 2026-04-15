package si.pl14.informeSocios;

public class InformeDetalladoSocioDTO {

    private String nombreSocio;
    private int numReservas;
    private int numActividades;
    private double deuda;
    private String instalacionFavorita;

    public InformeDetalladoSocioDTO(String nombreSocio, int numReservas,
                                     int numActividades, double deuda,
                                     String instalacionFavorita) {
        this.nombreSocio        = nombreSocio;
        this.numReservas        = numReservas;
        this.numActividades     = numActividades;
        this.deuda              = deuda;
        this.instalacionFavorita = instalacionFavorita;
    }

    public String getNombreSocio()        { return nombreSocio;        }
    public int    getNumReservas()        { return numReservas;        }
    public int    getNumActividades()     { return numActividades;     }
    public double getDeuda()              { return deuda;              }
    public String getInstalacionFavorita(){ return instalacionFavorita;}
}