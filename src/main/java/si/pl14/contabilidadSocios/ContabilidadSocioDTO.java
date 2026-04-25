package si.pl14.contabilidadSocios;

/**
 * DTO que agrupa los datos de contabilidad de un socio para un mes concreto.
 */
public class ContabilidadSocioDTO {

    private int    idSocio;
    private String nombre;
    private String apellidos;
    private String dni;
    private double totalReservas;
    private double totalActividades;
    private double totalDeuda;

    public int    getIdSocio()            { return idSocio;           }
    public String getNombre()             { return nombre;             }
    public String getApellidos()          { return apellidos;          }
    public String getDni()                { return dni;                }
    public double getTotalReservas()      { return totalReservas;      }
    public double getTotalActividades()   { return totalActividades;   }
    public double getTotalDeuda()         { return totalDeuda;         }

    public void setIdSocio(int v)             { this.idSocio = v;           }
    public void setNombre(String v)           { this.nombre = v;            }
    public void setApellidos(String v)        { this.apellidos = v;         }
    public void setDni(String v)              { this.dni = v;               }
    public void setTotalReservas(double v)    { this.totalReservas = v;     }
    public void setTotalActividades(double v) { this.totalActividades = v;  }
    public void setTotalDeuda(double v)       { this.totalDeuda = v;        }
}