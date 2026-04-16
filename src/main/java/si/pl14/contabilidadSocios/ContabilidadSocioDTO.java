package si.pl14.contabilidadSocios;

/**
 * DTO que agrupa los datos de contabilidad de un socio para un mes concreto.
 * Contiene: datos personales del socio, total coste de reservas propias,
 * total coste de actividades inscritas y la suma de ambos (deuda total).
 */
public class ContabilidadSocioDTO {

    private int    idSocio;
    private String nombre;
    private String apellidos;
    private String dni;
    private double totalReservas;
    private double totalActividades;
    private double totalDeuda;

    // ── getters ──────────────────────────────────────────────────────────────

    public int getIdSocio()           { return idSocio;          }
    public String getNombre()          { return nombre;            }
    public String getApellidos()       { return apellidos;         }
    public String getDni()             { return dni;               }
    public double getTotalReservas()   { return totalReservas;     }
    public double getTotalActividades(){ return totalActividades;  }
    public double getTotalDeuda()      { return totalDeuda;        }

    // ── setters ──────────────────────────────────────────────────────────────

    public void setIdSocio(int idSocio)                      { this.idSocio = idSocio;                   }
    public void setNombre(String nombre)                      { this.nombre = nombre;                     }
    public void setApellidos(String apellidos)                { this.apellidos = apellidos;               }
    public void setDni(String dni)                            { this.dni = dni;                           }
    public void setTotalReservas(double totalReservas)        { this.totalReservas = totalReservas;       }
    public void setTotalActividades(double totalActividades)  { this.totalActividades = totalActividades; }
    public void setTotalDeuda(double totalDeuda)              { this.totalDeuda = totalDeuda;             }
}