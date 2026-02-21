package si.pl14.disponibilidad;

import si.pl14.util.Database;
import si.pl14.model.InstalacionEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Modelo para la pantalla de disponibilidad de instalaciones.
 * Accede a la BD usando los métodos de utilidad de Database (executeQueryPojo / executeQueryArray).
 * 
 * Tablas usadas:
 *   Instalaciones  (id_instalacion, nombre, tipo, coste_hora)
 *   Reservas       (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad)
 *   Actividades    (id_actividad, nombre)
 */
public class DisponibilidadModel {

    private final Database db = new Database();

    // ─────────────────────────────────────────────────────────────────────────
    // Instalaciones
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve todas las instalaciones ordenadas por nombre.
     * Mapea a InstalacionEntity usando BeanListHandler (los campos deben coincidir
     * con los setters del Entity: idInstalacion, nombre, tipo, costeHora).
     */
    public List<InstalacionEntity> getInstalaciones() {
        // SQLite: alias en snake_case → BeanListHandler necesita camelCase en el setter
        // Usamos alias explícitos para que commons-beanutils los mapee correctamente
        String sql = "SELECT id_instalacion AS idInstalacion, nombre, tipo, coste_hora AS costeHora " +
                     "FROM Instalaciones ORDER BY nombre";
        return db.executeQueryPojo(InstalacionEntity.class, sql);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ocupaciones de un día para una instalación
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Clase interna DTO para transportar la información de una franja horaria ocupada.
     */
    public static class OcupacionDTO {
        /** Hora de inicio (0-23) */
        public int    horaInicio;
        /** Hora de fin (0-23) */
        public int    horaFin;
        /** Descripción: nombre de la actividad o "Reserva de socio" */
        public String descripcion;
        /** true = reserva de socio, false = actividad */
        public boolean esReservaSocio;
    }

    /**
     * Devuelve la lista de ocupaciones (reservas + actividades) de una instalación
     * en una fecha concreta.
     *
     * @param idInstalacion  id de la instalación
     * @param fechaIso       fecha en formato ISO "yyyy-MM-dd"
     */
    public List<OcupacionDTO> getOcupaciones(int idInstalacion, String fechaIso) {
        List<OcupacionDTO> resultado = new ArrayList<>();

        // ── Reservas de socios (id_socio IS NOT NULL) ──────────────────────
        String sqlSocios =
            "SELECT CAST(strftime('%H', hora_inicio) AS INTEGER) AS horaInicio, " +
            "       CAST(strftime('%H', hora_fin)    AS INTEGER) AS horaFin, " +
            "       'Reservada por otro socio'               AS descripcion " +
            "FROM Reservas " +
            "WHERE id_instalacion = ? AND fecha = ? AND id_socio IS NOT NULL";

        List<Object[]> filasSocios = db.executeQueryArray(sqlSocios, idInstalacion, fechaIso);
        for (Object[] fila : filasSocios) {
            OcupacionDTO dto = new OcupacionDTO();
            dto.horaInicio     = toInt(fila[0]);
            dto.horaFin        = toInt(fila[1]);
            dto.descripcion    = (String) fila[2];
            dto.esReservaSocio = true;
            resultado.add(dto);
        }

        // ── Reservas de actividades (id_actividad IS NOT NULL) ─────────────
        String sqlActividades =
            "SELECT CAST(strftime('%H', r.hora_inicio) AS INTEGER) AS horaInicio, " +
            "       CAST(strftime('%H', r.hora_fin)    AS INTEGER) AS horaFin, " +
            "       a.nombre                                       AS descripcion " +
            "FROM Reservas r " +
            "JOIN Actividades a ON r.id_actividad = a.id_actividad " +
            "WHERE r.id_instalacion = ? AND r.fecha = ? AND r.id_actividad IS NOT NULL";

        List<Object[]> filasAct = db.executeQueryArray(sqlActividades, idInstalacion, fechaIso);
        for (Object[] fila : filasAct) {
            OcupacionDTO dto = new OcupacionDTO();
            dto.horaInicio     = toInt(fila[0]);
            dto.horaFin        = toInt(fila[1]);
            dto.descripcion    = (String) fila[2];
            dto.esReservaSocio = false;
            resultado.add(dto);
        }

        return resultado;
    }

    /**
     * Convierte el resultado de la query (puede ser Integer o Long en SQLite) a int.
     */
    private int toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return Integer.parseInt(obj.toString());
    }

    /**
     * Agrupa las ocupaciones por hora (una entrada por hora ocupada dentro del rango horaInicio-horaFin).
     * Clave: hora (0-23), Valor: lista de descripciones para esa hora.
     */
    public Map<Integer, List<String>> getOcupacionesPorHora(int idInstalacion, String fechaIso) {
        Map<Integer, List<String>> mapa = new HashMap<>();
        List<OcupacionDTO> ocupaciones = getOcupaciones(idInstalacion, fechaIso);

        for (OcupacionDTO o : ocupaciones) {
            // Marca todas las horas del bloque como ocupadas
            int inicio = o.horaInicio;
            int fin    = (o.horaFin > o.horaInicio) ? o.horaFin : o.horaInicio + 1;
            for (int h = inicio; h < fin && h <= 23; h++) {
                String etiqueta = (o.esReservaSocio ? "[R] " : "[A] ") + o.descripcion;
                mapa.computeIfAbsent(h, k -> new ArrayList<>()).add(etiqueta);
            }
        }
        return mapa;
    }
}
