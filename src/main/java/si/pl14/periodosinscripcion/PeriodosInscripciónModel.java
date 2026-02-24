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
 * Historia de usuario:
 *   - Fecha inicio socios   (obligatoria)
 *   - Fecha fin socios      (obligatoria)
 *   - Fecha fin no socios   (obligatoria)
 *   - Nombre del periodo    (obligatorio)
 *
 * Reglas de negocio:
 *   1. Todos los campos son obligatorios.
 *   2. Formato de fecha: dd/MM/yyyy.
 *   3. inicio_socios <= fin_socios
 *   4. fin_socios <= fin_no_socios
 *   5. No puede existir otro periodo con el mismo nombre.
 */
public class PeriodosInscripciónModel {

    private static final DateTimeFormatter FMT_ISO  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_DISP = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Database db = new Database();

    public PeriodosInscripciónModel() {
        db.createDatabase(true);
    }

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

        // Regla: inicio_socios <= fin_socios
        if (inicioSocios.isAfter(finSocios))
            throw new ApplicationException(
                "La Fecha Inicio Socios no puede ser posterior a la Fecha Fin Socios.");

        // Regla: fin_socios <= fin_no_socios
        if (finSocios.isAfter(finNoSocios))
            throw new ApplicationException(
                "La Fecha Fin Socios no puede ser posterior a la Fecha Fin No Socios.");

        // Unicidad de nombre
        List<Object[]> existe = db.executeQueryArray(
            "SELECT id_periodo FROM PeriodosInscripcion WHERE LOWER(nombre) = LOWER(?)", nombre);
        if (!existe.isEmpty())
            throw new ApplicationException(
                "Ya existe un periodo con el nombre \"" + nombre + "\".");

        // Insertar
        db.executeUpdate(
            "INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) " +
            "VALUES (?, ?, ?, ?)",
            nombre,
            inicioSocios.format(FMT_ISO),
            finSocios.format(FMT_ISO),
            finNoSocios.format(FMT_ISO));

        // Recuperar id
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
}
