package si.pl14.reservasEmma;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import si.pl14.util.Database;

public class Reserva_Instalacion_Admin_Model {
    private Database db = new Database();

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(db.getUrl());
    }

    public List<String> getActividades() {
        List<String> actividades = new ArrayList<>();
        String sql = "SELECT id_actividad, nombre FROM Actividades";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) actividades.add(rs.getInt("id_actividad") + " - " + rs.getString("nombre"));
        } catch (SQLException e) { e.printStackTrace(); }
        return actividades;
    }

    public String[] getDatosActividad(int idActividad) {
        String[] datos = new String[4];
        String sql = "SELECT i.nombre, i.id_instalacion, a.fecha_inicio, a.fecha_fin FROM Actividades a " +
                     "JOIN Instalaciones i ON a.id_instalacion = i.id_instalacion WHERE a.id_actividad = ?";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idActividad);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                datos[0] = rs.getString("nombre");
                datos[1] = String.valueOf(rs.getInt("id_instalacion"));
                datos[2] = rs.getString("fecha_inicio");
                datos[3] = rs.getString("fecha_fin");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return datos;
    }

    public List<String> getInstalaciones() {
        List<String> inst = new ArrayList<>();
        String sql = "SELECT id_instalacion, nombre FROM Instalaciones";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) inst.add(rs.getInt("id_instalacion") + " - " + rs.getString("nombre"));
        } catch (SQLException e) { e.printStackTrace(); }
        return inst;
    }

    public boolean actualizarInstalacion(int idActividad, int newIdInstalacion) {
        String sql = "UPDATE Actividades SET id_instalacion = ? WHERE id_actividad = ?";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, newIdInstalacion);
            pst.setInt(2, idActividad);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<String[]> getHorariosActividad(int idAct) {
        List<String[]> res = new ArrayList<>();
        String sql = "SELECT dia_semana, hora_inicio, hora_fin FROM Horarios WHERE id_actividad = ?";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idAct);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) res.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    public boolean insertarNuevoHorario(int idAct, String dia, String ini, String fin) {
        String sql = "INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idAct);
            pst.setString(2, dia.toUpperCase());
            pst.setString(3, ini);
            pst.setString(4, fin);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean eliminarHorario(int idAct, String dia, String ini, String fin) {
        String sql = "DELETE FROM Horarios WHERE id_actividad = ? AND dia_semana = ? AND hora_inicio = ? AND hora_fin = ?";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idAct);
            pst.setString(2, dia);
            pst.setString(3, ini);
            pst.setString(4, fin);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<String[]> buscarConflictosDetallados(int idInst, String fIni, String fFin, int idAct) {
        List<String[]> conflictos = new ArrayList<>();
        // Conflictos con Horarios de otras Actividades
        String sql1 = "SELECT 'HORARIO' as t, h2.dia_semana || ' (' || h2.hora_inicio || '-' || h2.hora_fin || ')', " +
                      "'CONFLICTO: ' || a.nombre FROM Horarios h1 " +
                      "JOIN Horarios h2 ON h1.dia_semana = h2.dia_semana " +
                      "JOIN Actividades a ON h2.id_actividad = a.id_actividad " +
                      "WHERE h1.id_actividad = ? AND a.id_instalacion = ? AND a.id_actividad <> ? " +
                      "AND (h1.hora_inicio < h2.hora_fin AND h1.hora_fin > h2.hora_inicio) " +
                      "AND (a.fecha_inicio <= ? AND a.fecha_fin >= ?)";
        
        // Conflictos con Reservas de Socios
        String sql2 = "SELECT r.fecha, r.hora_inicio || '-' || r.hora_fin, 'SOCIO: ' || u.nombre " +
                      "FROM Reservas r JOIN Horarios h ON h.id_actividad = ? " +
                      "JOIN Socios s ON r.id_socio = s.id_socio JOIN Usuarios u ON s.dni = u.dni " +
                      "WHERE r.id_instalacion = ? AND r.fecha >= ? AND r.fecha <= ? " +
                      "AND strftime('%w', r.fecha) = (CASE h.dia_semana WHEN 'DOMINGO' THEN '0' WHEN 'LUNES' THEN '1' " +
                      "WHEN 'MARTES' THEN '2' WHEN 'MIERCOLES' THEN '3' WHEN 'JUEVES' THEN '4' WHEN 'VIERNES' THEN '5' " +
                      "WHEN 'SABADO' THEN '6' END) AND (r.hora_inicio < h.hora_fin AND r.hora_fin > h.hora_inicio)";

        try (Connection con = getConnection()) {
            try (PreparedStatement p1 = con.prepareStatement(sql1)) {
                p1.setInt(1, idAct); p1.setInt(2, idInst); p1.setInt(3, idAct); p1.setString(4, fFin); p1.setString(5, fIni);
                ResultSet rs = p1.executeQuery();
                while (rs.next()) conflictos.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
            }
            try (PreparedStatement p2 = con.prepareStatement(sql2)) {
                p2.setInt(1, idAct); p2.setInt(2, idInst); p2.setString(3, fIni); p2.setString(4, fFin);
                ResultSet rs = p2.executeQuery();
                while (rs.next()) conflictos.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return conflictos;
    }

    public boolean insertarReservasGeneradas(int idInst, int idAct, List<String[]> reservas) {
        String del = "DELETE FROM Reservas WHERE id_actividad = ?";
        String ins = "INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_actividad) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement pDel = con.prepareStatement(del)) { pDel.setInt(1, idAct); pDel.executeUpdate(); }
            try (PreparedStatement pIns = con.prepareStatement(ins)) {
                for (String[] r : reservas) {
                    pIns.setInt(1, idInst); pIns.setString(2, r[0]); pIns.setString(3, r[1]); pIns.setString(4, r[2]); pIns.setInt(5, idAct);
                    pIns.addBatch();
                }
                pIns.executeBatch();
            }
            con.commit(); return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}