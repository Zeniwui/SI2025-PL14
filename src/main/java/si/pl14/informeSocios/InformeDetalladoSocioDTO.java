package si.pl14.informeSocios;

public class InformeDetalladoSocioDTO {

    private final String nombreSocio;
    private final int    numReservas;
    private final int    numActividades;
    private final double deuda;
    private final String instalacionFavorita;   // solo la #1, para la tabla
    private final String topInstalaciones;      // top-3 con conteos, para el CSV

    public InformeDetalladoSocioDTO(String nombreSocio, int numReservas,
                                     int numActividades, double deuda,
                                     String instalacionFavorita,
                                     String topInstalaciones) {
        this.nombreSocio         = nombreSocio;
        this.numReservas         = numReservas;
        this.numActividades      = numActividades;
        this.deuda               = deuda;
        this.instalacionFavorita = instalacionFavorita;
        this.topInstalaciones    = topInstalaciones;
    }

    public String getNombreSocio()         { return nombreSocio;        }
    public int    getNumReservas()         { return numReservas;        }
    public int    getNumActividades()      { return numActividades;     }
    public double getDeuda()               { return deuda;              }
    public String getInstalacionFavorita() { return instalacionFavorita;}
    public String getTopInstalaciones()    { return topInstalaciones;   }
}