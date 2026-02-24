package si.pl14.model;

public class PeriodoInscripcionEntity {

    private int    idPeriodo;
    private String nombre;
    private String inicioSocios;
    private String finSocios;
    private String finNoSocios;

    public int    getIdPeriodo()            { return idPeriodo; }
    public void   setIdPeriodo(int v)       { this.idPeriodo = v; }

    public String getNombre()               { return nombre; }
    public void   setNombre(String v)       { this.nombre = v; }

    public String getInicioSocios()         { return inicioSocios; }
    public void   setInicioSocios(String v) { this.inicioSocios = v; }

    public String getFinSocios()            { return finSocios; }
    public void   setFinSocios(String v)    { this.finSocios = v; }

    public String getFinNoSocios()          { return finNoSocios; }
    public void   setFinNoSocios(String v)  { this.finNoSocios = v; }
}