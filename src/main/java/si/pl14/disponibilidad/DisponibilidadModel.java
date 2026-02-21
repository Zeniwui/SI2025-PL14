package si.pl14.disponibilidad;

import si.pl14.util.Database;
import si.pl14.model.InstalacionEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;

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
        ensureDatabase();
    }

    /**
     * Garantiza que la base de datos tiene todos los datos necesarios.
     * Reconstruye desde cero si el socio 0 no existe (indica carga incompleta).
     */
    private void ensureDatabase() {
        // Comprobar si el socio 0 existe
        try {
            List<Object[]> rows = db.executeQueryArray(
                "SELECT id_socio FROM Socios WHERE id_socio = 0");
            if (!rows.isEmpty()) {
                // BD ya correcta — solo migrar columna si falta
                migrateEventoSocial();
                return;
            }
        } catch (Exception ignored) {}

        // Socio 0 no existe: reconstruir BD completa statement a statement
        rebuildDatabase();
    }

    private void migrateEventoSocial() {
        try {
            db.executeUpdate(
                "ALTER TABLE Actividades ADD COLUMN es_evento_social INTEGER NOT NULL DEFAULT 0");
        } catch (Exception ignored) {}
    }

    /**
     * Reconstruye schema + datos ejecutando cada sentencia individualmente
     * con foreign_keys desactivado para evitar que un fallo aborte todo el batch.
     */
    private void rebuildDatabase() {
        java.io.File schemaFile = new java.io.File("src/main/resources/schema.sql");
        java.io.File dataFile   = new java.io.File("src/main/resources/data.sql");
        if (!schemaFile.exists() || !dataFile.exists()) return;

        try (java.sql.Connection con = java.sql.DriverManager.getConnection(
                new Database().getUrl());
             java.sql.Statement stmt = con.createStatement()) {

            con.setAutoCommit(false);
            stmt.execute("PRAGMA foreign_keys = OFF");

            // Schema
            for (String sql : parseSql(schemaFile)) {
                try { stmt.execute(sql); } catch (Exception ignored) {}
            }

            // Datos
            for (String sql : parseSql(dataFile)) {
                try { stmt.execute(sql); } catch (Exception ignored) {}
            }

            stmt.execute("PRAGMA foreign_keys = ON");
            con.commit();

        } catch (Exception e) {
            throw new si.pl14.util.ApplicationException("Error reconstruyendo BD: " + e.getMessage());
        }
    }

    private java.util.List<String> parseSql(java.io.File file) throws Exception {
        java.util.List<String> result = new java.util.ArrayList<>();
        java.util.List<String> lines  = java.nio.file.Files.readAllLines(file.toPath());
        StringBuilder current = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
            // Strip inline comments
            int commentIdx = trimmed.indexOf("--");
            if (commentIdx > 0) trimmed = trimmed.substring(0, commentIdx).trim();
            current.append(trimmed).append(" ");
            if (trimmed.endsWith(";")) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            }
        }
        return result;
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
     * Columnas devueltas (en orden):
     *   [0] id_reserva, [1] fecha, [2] hora_inicio, [3] hora_fin,
     *   [4] duracion_horas (int), [5] estado_pago, [6] metodo_pago,
     *   [7] coste_reserva, [8] fecha_creacion
     */
    public List<Object[]> getMisReservasPeriodo(int idInstalacion, String fechaDesde, String fechaHasta) {
        String sql =
            "SELECT id_reserva, " +
            "       fecha, " +
            "       hora_inicio, " +
            "       hora_fin, " +
            "       (CAST(strftime('%H', hora_fin) AS INTEGER) - CAST(strftime('%H', hora_inicio) AS INTEGER)) AS duracion_horas, " +
            "       estado_pago, " +
            "       COALESCE(metodo_pago, '—') AS metodo_pago, " +
            "       coste_reserva, " +
            "       strftime('%d/%m/%Y %H:%M', fecha_creacion) AS fecha_creacion " +
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