package si.pl14.ReservasEmma;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import si.pl14.util.Database;

public class Generacion_Automatica_Model {
    private Database db = new Database();

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(db.getUrl());
    }

    public List<String> getActividadesPendientes() {
        List<String> actividades = new ArrayList<>();
        // SUGERENCIA APLICADA: Uso de NOT EXISTS para evitar problemas con valores NULL
        String sql = "SELECT a.id_actividad, a.nombre FROM Actividades a " +
                     "WHERE NOT EXISTS (SELECT 1 FROM Reservas r WHERE r.id_actividad = a.id_actividad)";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                actividades.add(rs.getInt("id_actividad") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return actividades;
    }

    public String[] getDatosActividad(int idActividad) {
        String[] datos = new String[3];
        String sql = "SELECT id_instalacion, fecha_inicio, fecha_fin FROM Actividades WHERE id_actividad = ?";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idActividad);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                datos[0] = String.valueOf(rs.getInt("id_instalacion"));
                datos[1] = rs.getString("fecha_inicio");
                datos[2] = rs.getString("fecha_fin");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return datos;
    }

    public List<String[]> getHorariosActividad(int idActividad) {
        List<String[]> horarios = new ArrayList<>();
        String sql = "SELECT dia_semana, hora_inicio, hora_fin FROM Horarios WHERE id_actividad = ?";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idActividad);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                horarios.add(new String[]{rs.getString("dia_semana"), rs.getString("hora_inicio"), rs.getString("hora_fin")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return horarios;
    }

    public List<String[]> comprobarOcupacion(int idInst, String fecha, String hIni, String hFin) {
        List<String[]> ocupantes = new ArrayList<>();
        String sql = "SELECT id_reserva, id_socio, id_actividad FROM Reservas " +
                     "WHERE id_instalacion = ? AND fecha = ? AND (hora_inicio < ? AND hora_fin > ?)";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idInst); pst.setString(2, fecha); pst.setString(3, hFin); pst.setString(4, hIni);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getObject("id_socio") != null) {
                    ocupantes.add(new String[]{"SOCIO", String.valueOf(rs.getInt("id_reserva"))});
                } else if (rs.getObject("id_actividad") != null) {
                    ocupantes.add(new String[]{"ACTIVIDAD", String.valueOf(rs.getInt("id_actividad"))});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ocupantes;
    }

    public String anularReservaSocio(int idReserva) {
        String infoPago = "Estado de pago desconocido";
        String selectPagoSql = "SELECT estado_pago, coste_reserva FROM Reservas WHERE id_reserva = ?";
        String delReservaSql = "DELETE FROM Reservas WHERE id_reserva = ?";
        
        try (Connection con = getConnection()) {
            con.setAutoCommit(false); 
            try {
                try (PreparedStatement pCheck = con.prepareStatement(selectPagoSql)) {
                    pCheck.setInt(1, idReserva);
                    ResultSet rs = pCheck.executeQuery();
                    if (rs.next()) {
                        String estado = rs.getString("estado_pago");
                        double coste = rs.getDouble("coste_reserva");
                        
                        if ("Pagado".equalsIgnoreCase(estado) || "Completado".equalsIgnoreCase(estado)) {
                            infoPago = "Devolución pendiente (" + coste + "€)";
                        } else {
                            infoPago = "Sin coste a devolver (Estado: " + estado + ")";
                        }
                    }
                } 
                
                try (PreparedStatement pDel = con.prepareStatement(delReservaSql)) {
                    pDel.setInt(1, idReserva);
                    pDel.executeUpdate();
                }
                
                con.commit();
                return infoPago;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return null; 
        }
    }

    public boolean insertarReserva(int idInst, String fecha, String hIni, String hFin, int idAct) {
        String sql = "INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_actividad) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idInst);
            pst.setString(2, fecha);
            pst.setString(3, hIni);
            pst.setString(4, hFin);
            pst.setInt(5, idAct);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
