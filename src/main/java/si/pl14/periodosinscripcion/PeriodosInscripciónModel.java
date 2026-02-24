package si.pl14.periodosinscripcion;

import si.pl14.model.PeriodoInscripcionEntity;
import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modelo para "Crear Periodo de Inscripcion".
 *
 * Reglas de negocio:
 *   1. Todos los campos son obligatorios.
 *   2. Formato de fecha: dd/MM/yyyy.
 *   3. inicio_socios < fin_socios  (diferencia estricta > 3 dias)
 *   4. fin_socios    < fin_no_socios (diferencia estricta > 3 dias)
 *   5. No puede existir otro periodo con el mismo nombre.
 */
public class PeriodosInscripciónModel {

    private static final DateTimeFormatter FMT_ISO  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_DISP = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Duracion minima en dias que debe tener cada sub-periodo. */
    private static final int MIN_DIAS_PERIODO = 3;

    private final Database db = new Database();

    public PeriodosInscripciónModel() {
        db.createDatabase(true);
    }

    // ── Consulta ──────────────────────────────────────────────────────────────

    /**
     * Devuelve todos los periodos almacenados, del mas reciente al mas antiguo.
     * Usado por el controlador para refrescar la tabla de periodos guardados.
     */
    public List<PeriodoInscripcionEntity> getPeriodos() {
        String sql =
            "SELECT id_periodo   AS idPeriodo, " +
            "       nombre, " +
            "       inicio_socios   AS inicioSocios, " +
            "       fin_socios      AS finSocios, " +
            "       fin_no_socios   AS finNoSocios " +
            "FROM PeriodosInscripcion " +
            "ORDER BY inicio_socios DESC";
        return db.executeQueryPojo(PeriodoInscripcionEntity.class, sql);
    }

    // ── Creacion ──────────────────────────────────────────────────────────────

    /**
     * Valida y persiste un nuevo periodo de inscripcion.
     * Devuelve la entidad creada con el id asignado por la BD.
     */
    public PeriodoInscripcionEntity crearPeriodo(String nombre,
                                                  String inicioSociosStr,
                                                  String finSociosStr,
                                                  String finNoSociosStr) {
        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty())
            throw new ApplicationException("El nombre del periodo no puede estar vacio.");
        nombre = nombre.trim();

        // Parsear fechas
        LocalDate inicioSocios = parseFecha(inicioSociosStr, "Fecha Inicio Socios");
        LocalDate finSocios    = parseFecha(finSociosStr,    "Fecha Fin Socios");
        LocalDate finNoSocios  = parseFecha(finNoSociosStr,  "Fecha Fin No Socios");

        // Regla: inicio_socios < fin_socios
        if (!inicioSocios.isBefore(finSocios))
            throw new ApplicationException(
                "La Fecha Inicio Socios debe ser anterior a la Fecha Fin Socios.");

        // Regla: periodo socios > 3 dias
        long diasSocios = java.time.temporal.ChronoUnit.DAYS.between(inicioSocios, finSocios);
        if (diasSocios <= MIN_DIAS_PERIODO)
            throw new ApplicationException(
                "El periodo de socios debe durar más de " + MIN_DIAS_PERIODO +
                " dias (actualmente: " + diasSocios + " dia(s)).");

        // Regla: fin_socios < fin_no_socios
        if (!finSocios.isBefore(finNoSocios))
            throw new ApplicationException(
                "La Fecha Fin No Socios debe ser posterior a la Fecha Fin Socios.");

        // Regla: periodo no socios (desde fin_socios hasta fin_no_socios) > 3 dias
        long diasNoSocios = java.time.temporal.ChronoUnit.DAYS.between(finSocios, finNoSocios);
        if (diasNoSocios <= MIN_DIAS_PERIODO)
            throw new ApplicationException(
                "El periodo de no socios debe durar más de " + MIN_DIAS_PERIODO +
                " dias (actualmente: " + diasNoSocios + " dia(s)).");

        // Unicidad de nombre
        List<Object[]> existe = db.executeQueryArray(
            "SELECT id_periodo FROM PeriodosInscripcion WHERE LOWER(nombre) = LOWER(?)", nombre);
        if (!existe.isEmpty())
            throw new ApplicationException(
                "Ya existe un periodo con el nombre \"" + nombre + "\".");

        // Insertar en BD
        db.executeUpdate(
            "INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) " +
            "VALUES (?, ?, ?, ?)",
            nombre,
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

    /** Convierte fecha ISO yyyy-MM-dd a dd/MM/yyyy para mostrar en pantalla. */
    public static String isoADisplay(String iso) {
        try { return LocalDate.parse(iso, FMT_ISO).format(FMT_DISP); }
        catch (Exception e) { return iso; }
    }
}