package si.pl14.contabilidadSocios;

import si.pl14.util.Database;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de modelo (acceso a datos y lógica de negocio) para la HU
 * "Calcular contabilidad de socios en un mes".
 *
 * Consulta las reservas directas de instalaciones (id_socio NOT NULL)
 * y las reservas derivadas de inscripciones a actividades (id_actividad NOT NULL)
 * filtrando por el mes/año indicados, las agrupa por socio y genera el fichero.
 */
public class ContabilidadSociosModel {

    private final Database db = new Database();

    public ContabilidadSociosModel() {
        db.createDatabase(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cálculo principal
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Calcula la contabilidad de todos los socios para el mes y año indicados.
     *
     * @param mes  Número de mes (1-12)
     * @param anio Año con cuatro dígitos
     * @return Lista de DTOs, uno por socio que tenga al menos una reserva ese mes.
     *         Lista vacía si no hay datos.
     */
    public List<ContabilidadSocioDTO> calcularContabilidad(int mes, int anio) {

        // Prefijo del mes en formato SQLite: "YYYY-MM"
        String prefijo = String.format("%04d-%02d", anio, mes);

        // ── 1. Total reservas directas de instalaciones por socio ─────────────
        String sqlReservas =
            "SELECT s.id_socio, " +
            "       u.nombre, " +
            "       u.apellidos, " +
            "       u.dni, " +
            "       COALESCE(SUM(r.coste_reserva), 0.0) AS total_reservas " +
            "FROM Socios s " +
            "JOIN Usuarios u ON s.dni = u.dni " +
            "JOIN Reservas r ON r.id_socio = s.id_socio " +
            "WHERE r.id_socio IS NOT NULL " +
            "  AND r.id_actividad IS NULL " +
            "  AND strftime('%Y-%m', r.fecha) = ? " +
            "GROUP BY s.id_socio, u.nombre, u.apellidos, u.dni";

        List<Object[]> filasReservas = db.executeQueryArray(sqlReservas, prefijo);

        // ── 2. Total actividades (inscripciones) por socio ────────────────────
        //
        // Buscamos reservas cuyo origen sea una actividad (id_actividad NOT NULL)
        // e identificamos al socio que la inscribió a través de la tabla Inscripciones.
        String sqlActividades =
            "SELECT s.id_socio, " +
            "       u.nombre, " +
            "       u.apellidos, " +
            "       u.dni, " +
            "       COALESCE(SUM(a.precio_socio), 0.0) AS total_actividades " +
            "FROM Inscripciones i " +
            "JOIN Socios  s ON i.id_socio    = s.id_socio " +
            "JOIN Usuarios u ON s.dni         = u.dni " +
            "JOIN Actividades a ON i.id_actividad = a.id_actividad " +
            "WHERE strftime('%Y-%m', i.fecha_inscripcion) = ? " +
            "GROUP BY s.id_socio, u.nombre, u.apellidos, u.dni";

        List<Object[]> filasActividades = db.executeQueryArray(sqlActividades, prefijo);

        // ── 3. Combinar ambos resultados en DTOs ──────────────────────────────
        return combinar(filasReservas, filasActividades);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Generación del fichero
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Guarda la contabilidad en un fichero de texto plano.
     * Si la lista está vacía no crea el fichero.
     *
     * @param lista    Lista de DTOs calculada previamente.
     * @param mes      Mes del cálculo (1-12).
     * @param anio     Año del cálculo.
     * @param rutaDir  Directorio donde se guarda el fichero.
     * @return Ruta completa del fichero generado, o null si no se creó.
     */
    public String guardarFichero(List<ContabilidadSocioDTO> lista, int mes, int anio, String rutaDir) {
        if (lista == null || lista.isEmpty()) {
            return null; // HU: no generar fichero vacío
        }

        String nombreFichero = String.format("contabilidad_%04d_%02d.txt", anio, mes);
        String ruta = rutaDir + "/" + nombreFichero;

        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {
            pw.printf("=== Contabilidad de socios – %02d/%04d ===%n%n", mes, anio);

            double sumReservas    = 0;
            double sumActividades = 0;
            double sumTotal       = 0;

            for (ContabilidadSocioDTO dto : lista) {
                pw.printf("Socio: %s %s (DNI: %s)%n", dto.getNombre(), dto.getApellidos(), dto.getDni());
                pw.printf("  Total reservas     : %.2f EUR%n", dto.getTotalReservas());
                pw.printf("  Total actividades  : %.2f EUR%n", dto.getTotalActividades());
                pw.printf("  DEUDA TOTAL        : %.2f EUR%n%n", dto.getTotalDeuda());

                sumReservas    += dto.getTotalReservas();
                sumActividades += dto.getTotalActividades();
                sumTotal       += dto.getTotalDeuda();
            }

            pw.printf("─────────────────────────────────────────%n");
            pw.printf("Total socios       : %d%n",   lista.size());
            pw.printf("Total reservas     : %.2f EUR%n", sumReservas);
            pw.printf("Total actividades  : %.2f EUR%n", sumActividades);
            pw.printf("TOTAL DEUDA        : %.2f EUR%n", sumTotal);

        } catch (IOException e) {
            throw new si.pl14.util.ApplicationException("Error al guardar el fichero: " + e.getMessage());
        }

        return ruta;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────────

    private List<ContabilidadSocioDTO> combinar(List<Object[]> filasRes, List<Object[]> filasAct) {

        // Indexar reservas por idSocio
        java.util.Map<Integer, ContabilidadSocioDTO> mapa = new java.util.LinkedHashMap<>();

        for (Object[] f : filasRes) {
            ContabilidadSocioDTO dto = new ContabilidadSocioDTO();
            dto.setIdSocio(   toInt(f[0])     );
            dto.setNombre(    str(f[1])        );
            dto.setApellidos( str(f[2])        );
            dto.setDni(       str(f[3])        );
            dto.setTotalReservas( toDouble(f[4]) );
            mapa.put(dto.getIdSocio(), dto);
        }

        // Añadir/acumular actividades
        for (Object[] f : filasAct) {
            int id = toInt(f[0]);
            ContabilidadSocioDTO dto = mapa.get(id);
            if (dto == null) {
                dto = new ContabilidadSocioDTO();
                dto.setIdSocio(   toInt(f[0]) );
                dto.setNombre(    str(f[1])   );
                dto.setApellidos( str(f[2])   );
                dto.setDni(       str(f[3])   );
                mapa.put(id, dto);
            }
            dto.setTotalActividades( toDouble(f[4]) );
        }

        // Calcular deuda total
        List<ContabilidadSocioDTO> resultado = new ArrayList<>(mapa.values());
        for (ContabilidadSocioDTO dto : resultado) {
            dto.setTotalDeuda( dto.getTotalReservas() + dto.getTotalActividades() );
        }
        return resultado;
    }

    private int    toInt   (Object o) { return o instanceof Number ? ((Number) o).intValue()    : 0;   }
    private double toDouble(Object o) { return o instanceof Number ? ((Number) o).doubleValue()  : 0.0; }
    private String str     (Object o) { return o != null ? o.toString() : "";                          }
}