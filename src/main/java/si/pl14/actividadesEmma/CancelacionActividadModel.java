package si.pl14.actividadesEmma;

import java.util.List;
import si.pl14.util.Database;

public class CancelacionActividadModel {
    private Database db = new Database();

    public List<ActividadResumenDTO> getActividadesBajoMinimo(int minimo) {
        String sql = "SELECT a.id_actividad AS idActividad, a.nombre, " +
                     "COUNT(i.id_inscripcion) AS inscritos, a.aforo AS plazas, inst.nombre AS instalacion " +
                     "FROM Actividades a " +
                     "LEFT JOIN Inscripciones i ON a.id_actividad = i.id_actividad " +
                     "LEFT JOIN Instalaciones inst ON a.id_instalacion = inst.id_instalacion " +
                     "GROUP BY a.id_actividad " +
                     "HAVING inscritos < ?";
        return db.executeQueryPojo(ActividadResumenDTO.class, sql, minimo);
    }

    public void cancelarActividadCompleta(int idActividad) {
        String sqlPagos = "DELETE FROM Pagos WHERE id_inscripcion IN (" +
                          "SELECT id_inscripcion FROM Inscripciones WHERE id_actividad = ?)";
        db.executeUpdate(sqlPagos, idActividad);


        db.executeUpdate("DELETE FROM Reservas WHERE id_actividad = ?", idActividad);

        db.executeUpdate("DELETE FROM Actividades WHERE id_actividad = ?", idActividad);
    }

    public List<String> getEmailsInscritos(int idActividad) {
        String sql = "SELECT u.email FROM Usuarios u " +
                     "JOIN Socios s ON u.dni = s.dni " +
                     "JOIN Inscripciones i ON s.id_socio = i.id_socio " +
                     "WHERE i.id_actividad = ?";
        return db.executeQueryPojo(String.class, sql, idActividad);
    }
}