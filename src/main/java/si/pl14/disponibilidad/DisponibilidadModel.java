package si.pl14.disponibilidad;

import si.pl14.util.Database;
import si.pl14.model.InstalacionEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Modelo para la pantalla de disponibilidad de instalaciones.
 *
 * Tablas usadas:
 *   Instalaciones  (id_instalacion, nombre, tipo, coste_hora)
 *   Reservas       (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad)
 *   Actividades    (id_actividad, nombre)
 */
public class DisponibilidadModel {

    private final Database db = new Database();

    /**
     * ID del socio que esta usando la aplicacion en este momento.
     * Con valor 0 corresponde al usuario "Usuario Actual" creado en data.sql.
     * Cambiar este valor para simular otro usuario.
     */
    public static final int ID_SOCIO_ACTUAL = 0;

    public DisponibilidadModel() {
        // Migración automática: añadir columna es_evento_social si no existe
        // (necesario cuando el .db fue creado con el schema antiguo)
        try {
            db.executeUpdate(
                "ALTER TABLE Actividades ADD COLUMN es_evento_social INTEGER NOT NULL DEFAULT 0");
        } catch (Exception ignored) {
            // La columna ya existe — ignorar el error es seguro
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Instalaciones
    // ─────────────────────────────────────────────────────────────────────────

    public List<InstalacionEntity> getInstalaciones() {
        String sql = "SELECT id_instalacion AS idInstalacion, nombre, tipo, coste_hora AS costeHora " +
                     "FROM Instalaciones ORDER BY nombre";
        return db.executeQueryPojo(InstalacionEntity.class, sql);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DTO de ocupacion
    // ─────────────────────────────────────────────────────────────────────────

    public static class OcupacionDTO {
        public int     horaInicio;
        public int     horaFin;
        public String  descripcion;
        /** true = reserva de socio (propio o ajeno), false = actividad o evento */
        public boolean esReservaSocio;
        /** true = esta reserva pertenece al socio actual (ID_SOCIO_ACTUAL) */
        public boolean esMiaReserva;
        /** true = evento social (actividad con es_evento_social=1) */
        public boolean esEventoSocial;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Consultas de ocupacion
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve todas las ocupaciones de una instalacion en una fecha:
     *  - Mis reservas        -> esMiaReserva = true
     *  - Reservas de otros   -> esReservaSocio = true,  esMiaReserva = false
     *  - Actividades         -> esReservaSocio = false, esMiaReserva = false
     */
    public List<OcupacionDTO> getOcupaciones(int idInstalacion, String fechaIso) {
        List<OcupacionDTO> resultado = new ArrayList<>();

        // 1. Mis reservas
        String sqlMias =
            "SELECT CAST(strftime('%H', hora_inicio) AS INTEGER) AS horaInicio, " +
            "       CAST(strftime('%H', hora_fin)    AS INTEGER) AS horaFin, " +
            "       'Mi reserva' AS descripcion " +
            "FROM Reservas " +
            "WHERE id_instalacion = ? AND fecha = ? AND id_socio = ?";
        for (Object[] f : db.executeQueryArray(sqlMias, idInstalacion, fechaIso, ID_SOCIO_ACTUAL)) {
            OcupacionDTO dto = new OcupacionDTO();
            dto.horaInicio    = toInt(f[0]);
            dto.horaFin       = toInt(f[1]);
            dto.descripcion   = (String) f[2];
            dto.esReservaSocio = true;
            dto.esMiaReserva  = true;
            dto.esEventoSocial = false;
            resultado.add(dto);
        }

        // 2. Reservas de otros socios
        String sqlOtros =
            "SELECT CAST(strftime('%H', hora_inicio) AS INTEGER) AS horaInicio, " +
            "       CAST(strftime('%H', hora_fin)    AS INTEGER) AS horaFin, " +
            "       'Reservada por otro socio' AS descripcion " +
            "FROM Reservas " +
            "WHERE id_instalacion = ? AND fecha = ? AND id_socio IS NOT NULL AND id_socio != ?";
        for (Object[] f : db.executeQueryArray(sqlOtros, idInstalacion, fechaIso, ID_SOCIO_ACTUAL)) {
            OcupacionDTO dto = new OcupacionDTO();
            dto.horaInicio    = toInt(f[0]);
            dto.horaFin       = toInt(f[1]);
            dto.descripcion   = (String) f[2];
            dto.esReservaSocio = true;
            dto.esMiaReserva  = false;
            dto.esEventoSocial = false;
            resultado.add(dto);
        }

        // 3. Actividades regulares (es_evento_social = 0)
        String sqlActividades =
            "SELECT CAST(strftime('%H', r.hora_inicio) AS INTEGER) AS horaInicio, " +
            "       CAST(strftime('%H', r.hora_fin)    AS INTEGER) AS horaFin, " +
            "       a.nombre AS descripcion " +
            "FROM Reservas r " +
            "JOIN Actividades a ON r.id_actividad = a.id_actividad " +
            "WHERE r.id_instalacion = ? AND r.fecha = ? AND r.id_actividad IS NOT NULL " +
            "  AND a.es_evento_social = 0";
        for (Object[] f : db.executeQueryArray(sqlActividades, idInstalacion, fechaIso)) {
            OcupacionDTO dto = new OcupacionDTO();
            dto.horaInicio     = toInt(f[0]);
            dto.horaFin        = toInt(f[1]);
            dto.descripcion    = (String) f[2];
            dto.esReservaSocio = false;
            dto.esMiaReserva   = false;
            dto.esEventoSocial = false;
            resultado.add(dto);
        }

        // 4. Eventos sociales (es_evento_social = 1)
        String sqlEventos =
            "SELECT CAST(strftime('%H', r.hora_inicio) AS INTEGER) AS horaInicio, " +
            "       CAST(strftime('%H', r.hora_fin)    AS INTEGER) AS horaFin, " +
            "       a.nombre AS descripcion " +
            "FROM Reservas r " +
            "JOIN Actividades a ON r.id_actividad = a.id_actividad " +
            "WHERE r.id_instalacion = ? AND r.fecha = ? AND r.id_actividad IS NOT NULL " +
            "  AND a.es_evento_social = 1";
        for (Object[] f : db.executeQueryArray(sqlEventos, idInstalacion, fechaIso)) {
            OcupacionDTO dto = new OcupacionDTO();
            dto.horaInicio     = toInt(f[0]);
            dto.horaFin        = toInt(f[1]);
            dto.descripcion    = (String) f[2];
            dto.esReservaSocio = false;
            dto.esMiaReserva   = false;
            dto.esEventoSocial = true;
            resultado.add(dto);
        }

        return resultado;
    }

    /**
     * Agrupa TODAS las ocupaciones por hora para la pestana "Disponibilidad".
     * Prefijos: [M] mis reservas, [R] otros socios, [A] actividades.
     */
    public Map<Integer, List<String>> getOcupacionesPorHora(int idInstalacion, String fechaIso) {
        Map<Integer, List<String>> mapa = new HashMap<>();
        for (OcupacionDTO o : getOcupaciones(idInstalacion, fechaIso)) {
            String prefijo;
            if      (o.esMiaReserva)   prefijo = "[M] ";
            else if (o.esReservaSocio) prefijo = "[R] ";
            else if (o.esEventoSocial) prefijo = "[E] ";
            else                       prefijo = "[A] ";
            int fin = (o.horaFin > o.horaInicio) ? o.horaFin : o.horaInicio + 1;
            for (int h = o.horaInicio; h < fin && h <= 23; h++) {
                mapa.computeIfAbsent(h, k -> new ArrayList<>()).add(prefijo + o.descripcion);
            }
        }
        return mapa;
    }

    /**
     * Devuelve las reservas del socio actual en una instalacion y dia concretos,
     * con todos los detalles para mostrar en la pestana "Mis Reservas".
     * Columnas devueltas: horaInicio(int), horaFin(int), hora_inicio(str),
     *                     hora_fin(str), estado_pago, metodo_pago, coste_reserva
     */
    public List<Object[]> getMisReservasDelDia(int idInstalacion, String fechaIso) {
        String sql =
            "SELECT CAST(strftime('%H', hora_inicio) AS INTEGER) AS horaInicioInt, " +
            "       CAST(strftime('%H', hora_fin)    AS INTEGER) AS horaFinInt, " +
            "       hora_inicio, hora_fin, " +
            "       estado_pago, metodo_pago, coste_reserva " +
            "FROM Reservas " +
            "WHERE id_instalacion = ? AND fecha = ? AND id_socio = ? " +
            "ORDER BY hora_inicio";
        return db.executeQueryArray(sql, idInstalacion, fechaIso, ID_SOCIO_ACTUAL);
    }

    /**
     * Devuelve TODAS las reservas del socio actual en una instalacion
     * dentro del rango [fechaDesde, fechaHasta] (formato "yyyy-MM-dd").
     * Columnas: fecha(str), hora_inicio(str), hora_fin(str),
     *           estado_pago, metodo_pago, coste_reserva
     */
    public List<Object[]> getMisReservasPeriodo(int idInstalacion, String fechaDesde, String fechaHasta) {
        String sql =
            "SELECT fecha, hora_inicio, hora_fin, " +
            "       estado_pago, metodo_pago, coste_reserva " +
            "FROM Reservas " +
            "WHERE id_instalacion = ? AND id_socio = ? " +
            "  AND fecha >= ? AND fecha <= ? " +
            "ORDER BY fecha, hora_inicio";
        return db.executeQueryArray(sql, idInstalacion, ID_SOCIO_ACTUAL, fechaDesde, fechaHasta);
    }

    private int toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return Integer.parseInt(obj.toString());
    }
}