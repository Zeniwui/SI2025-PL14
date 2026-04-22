package ReservasEmma;

public class ReservaDetalleDTO {
        private int idReserva;
        private String instalacion;
        private String fecha;
        private String horaInicio;
        private String horaFin;
        private double costeReserva;
        private int idSocio;
        private String dniSocio;
        private String nombreSocio;

        public int getIdReserva() { return idReserva; }
        public void setIdReserva(int id) { this.idReserva = id; }
        public String getInstalacion() { return instalacion; }
        public void setInstalacion(String i) { this.instalacion = i; }
        public String getFecha() { return fecha; }
        public void setFecha(String f) { this.fecha = f; }
        public String getHoraInicio() { return horaInicio; }
        public void setHoraInicio(String h) { this.horaInicio = h; }
        public String getHoraFin() { return horaFin; }
        public void setHoraFin(String h) { this.horaFin = h; }
        public double getCosteReserva() { return costeReserva; }
        public void setCosteReserva(double c) { this.costeReserva = c; }
        public int getIdSocio() { return idSocio; }
        public void setIdSocio(int id) { this.idSocio = id; }
        public String getDniSocio() { return dniSocio; }
        public void setDniSocio(String d) { this.dniSocio = d; }
        public String getNombreSocio() { return nombreSocio; }
        public void setNombreSocio(String n) { this.nombreSocio = n; }
    }
