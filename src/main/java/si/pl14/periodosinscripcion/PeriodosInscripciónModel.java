package si.pl14.periodosinscripcion;

import si.pl14.model.PeriodoInscripcionEntity;
import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modelo para "Crear Periodo de Inscripcion".
 * ~~Por Martín~~
 * Reglas de negocio:
 *   1. Nombre obligatorio. Descripcion opcional.
 *   2. Formato de fecha: dd/MM/yyyy.
 *   3. inicio_socios < fin_socios  (diferencia estricta > MIN y < MAX dias)
 *   4. fin_socios    < fin_no_socios (diferencia estricta > MIN y < MAX dias)
 *   5. No puede existir otro periodo con el mismo nombre.
 *   6. No se pueden superar MAX_PERIODOS periodos en total.
 */
public class PeriodosInscripciónModel {

    private static final DateTimeFormatter FMT_ISO  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_DISP = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final int MIN_DIAS_PERIODO = 3;
    public static final int MAX_DIAS_PERIODO = 30;
    public static final int MAX_PERIODOS     = 15;

    private final Database db = new Database();

    public PeriodosInscripciónModel() {
        db.createDatabase(true);
        migrarColumnaDescripcion();
    }

    /**
     * Migración: añade la columna descripcion si la BD ya existia sin ella.
     * SQLite no soporta IF NOT EXISTS en ALTER TABLE, por eso se captura la excepcion.
     */
    private void migrarColumnaDescripcion() {
        try {
            db.executeUpdate("ALTER TABLE PeriodosInscripcion ADD COLUMN descripcion TEXT");
        } catch (Exception ignored) {
            // La columna ya existe, no hay nada que hacer
        }
    }
    // ── Consulta ──────────────────────────────────────────────────────────────

    public List<PeriodoInscripcionEntity> getPeriodos() {
        String sql =
            "SELECT id_periodo      AS idPeriodo, " +
            "       nombre, " +
            "       descripcion, " +
            "       inicio_socios   AS inicioSocios, " +
            "       fin_socios      AS finSocios, " +
            "       fin_no_socios   AS finNoSocios " +
            "FROM PeriodosInscripcion " +
            "ORDER BY id_periodo ASC";
        return db.executeQueryPojo(PeriodoInscripcionEntity.class, sql);
    }

    // ── Creacion ──────────────────────────────────────────────────────────────

    public PeriodoInscripcionEntity crearPeriodo(String nombre,
                                                  String descripcion,
                                                  String inicioSociosStr,
                                                  String finSociosStr,
                                                  String finNoSociosStr) {
        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty())
            throw new ApplicationException("El nombre del periodo no puede estar vacio.");
        nombre = nombre.trim();

        // Descripcion opcional — limpiar si viene nula
        if (descripcion == null) descripcion = "";
        descripcion = descripcion.trim();

        // Parsear fechas
        LocalDate inicioSocios = parseFecha(inicioSociosStr, "Fecha Inicio Socios");
        LocalDate finSocios    = parseFecha(finSociosStr,    "Fecha Fin Socios");
        LocalDate finNoSocios  = parseFecha(finNoSociosStr,  "Fecha Fin No Socios");

        // Limite maximo de periodos
        List<Object[]> conteo = db.executeQueryArray(
            "SELECT COUNT(*) FROM PeriodosInscripcion");
        int total = conteo.isEmpty() ? 0 : ((Number) conteo.get(0)[0]).intValue();
        if (total >= MAX_PERIODOS)
            throw new ApplicationException(
                "No se pueden crear más de " + MAX_PERIODOS +
                " periodos de inscripcion. Elimine alguno antes de continuar.");

        // Regla: inicio_socios < fin_socios
        if (!inicioSocios.isBefore(finSocios))
            throw new ApplicationException(
                "La Fecha Inicio Socios debe ser anterior a la Fecha Fin Socios.");

        long diasSocios = java.time.temporal.ChronoUnit.DAYS.between(inicioSocios, finSocios);
        if (diasSocios <= MIN_DIAS_PERIODO)
            throw new ApplicationException(
                "El periodo de socios debe durar más de " + MIN_DIAS_PERIODO +
                " dias (actualmente: " + diasSocios + " dia(s)).");
        if (diasSocios >= MAX_DIAS_PERIODO)
            throw new ApplicationException(
                "El periodo de socios debe durar menos de " + MAX_DIAS_PERIODO +
                " dias (actualmente: " + diasSocios + " dia(s)).");

        // Regla: fin_socios < fin_no_socios
        if (!finSocios.isBefore(finNoSocios))
            throw new ApplicationException(
                "La Fecha Fin No Socios debe ser posterior a la Fecha Fin Socios.");

        long diasNoSocios = java.time.temporal.ChronoUnit.DAYS.between(finSocios, finNoSocios);
        if (diasNoSocios <= MIN_DIAS_PERIODO)
            throw new ApplicationException(
                "El periodo de no socios debe durar más de " + MIN_DIAS_PERIODO +
                " dias (actualmente: " + diasNoSocios + " dia(s)).");
        if (diasNoSocios >= MAX_DIAS_PERIODO)
            throw new ApplicationException(
                "El periodo de no socios debe durar menos de " + MAX_DIAS_PERIODO +
                " dias (actualmente: " + diasNoSocios + " dia(s)).");

        // Unicidad de nombre
        List<Object[]> existe = db.executeQueryArray(
            "SELECT id_periodo FROM PeriodosInscripcion WHERE LOWER(nombre) = LOWER(?)", nombre);
        if (!existe.isEmpty())
            throw new ApplicationException(
                "Ya existe un periodo con el nombre \"" + nombre + "\".");

        // Insertar en BD
        db.executeUpdate(
            "INSERT INTO PeriodosInscripcion (nombre, descripcion, inicio_socios, fin_socios, fin_no_socios) " +
            "VALUES (?, ?, ?, ?, ?)",
            nombre,
            descripcion,
            inicioSocios.format(FMT_ISO),
            finSocios.format(FMT_ISO),
            finNoSocios.format(FMT_ISO));

        // Recuperar id asignado
        List<Object[]> rows = db.executeQueryArray(
            "SELECT id_periodo FROM PeriodosInscripcion WHERE LOWER(nombre) = LOWER(?)", nombre);
        int nuevoId = rows.isEmpty() ? -1 : ((Number) rows.get(0)[0]).intValue();

        PeriodoInscripcionEntity creado = new PeriodoInscripcionEntity();
        creado.setIdPeriodo(nuevoId);
        creado.setNombre(nombre);
        creado.setDescripcion(descripcion);
        creado.setInicioSocios(inicioSocios.format(FMT_ISO));
        creado.setFinSocios(finSocios.format(FMT_ISO));
        creado.setFinNoSocios(finNoSocios.format(FMT_ISO));
        return creado;
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    public static LocalDate parseFecha(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty())
            throw new ApplicationException("El campo \"" + campo + "\" es obligatorio.");
        try {
            return LocalDate.parse(texto.trim(), FMT_DISP);
        } catch (Exception e) {
            throw new ApplicationException(
                "Formato invalido en \"" + campo + "\". Use dd/MM/yyyy.");
        }
    }

    public static String isoADisplay(String iso) {
        try { return LocalDate.parse(iso, FMT_ISO).format(FMT_DISP); }
        catch (Exception e) { return iso; }
    }
}