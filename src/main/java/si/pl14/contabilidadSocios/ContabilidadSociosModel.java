package si.pl14.contabilidadSocios;

import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo MVC para la HU "Calcular contabilidad de socios en un mes".
 *
 * Calcula:
 *  - Total de coste de reservas directas de instalaciones (id_socio NOT NULL, id_actividad NULL)
 *    agrupadas por socio para el mes/año seleccionado.
 *  - Total de coste de actividades inscritas (precio_socio) por socio
 *    cuyas inscripciones pertenezcan al mes/año seleccionado.
 *
 * Genera un fichero .txt con los datos de cada socio y los totales finales.
 * Si no hay datos, NO genera fichero.
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
     * Devuelve la lista de DTOs de contabilidad para el mes/año indicados.
     * Lista vacía si no hay reservas de socios ese mes.
     */
    public List<ContabilidadSocioDTO> calcularContabilidad(int mes, int anio) {
        String prefijo = String.format("%04d-%02d", anio, mes);

        // 1) Reservas directas de instalaciones por socio ese mes
        String sqlRes =
            "SELECT s.id_socio, u.nombre, u.apellidos, u.dni, " +
            "       COALESCE(SUM(r.coste_reserva), 0.0) AS total " +
            "FROM Reservas r " +
            "JOIN Socios s   ON r.id_socio = s.id_socio " +
            "JOIN Usuarios u ON s.dni = u.dni " +
            "WHERE r.id_socio IS NOT NULL " +
            "  AND r.id_actividad IS NULL " +
            "  AND strftime('%Y-%m', r.fecha) = ? " +
            "GROUP BY s.id_socio, u.nombre, u.apellidos, u.dni";

        List<Object[]> filasRes = db.executeQueryArray(sqlRes, prefijo);

        // 2) Inscripciones a actividades ese mes → precio_socio como coste
        String sqlAct =
            "SELECT s.id_socio, u.nombre, u.apellidos, u.dni, " +
            "       COALESCE(SUM(a.precio_socio), 0.0) AS total " +
            "FROM Inscripciones i " +
            "JOIN Socios     s ON i.id_socio     = s.id_socio " +
            "JOIN Usuarios   u ON s.dni           = u.dni " +
            "JOIN Actividades a ON i.id_actividad = a.id_actividad " +
            "WHERE strftime('%Y-%m', i.fecha_inscripcion) = ? " +
            "GROUP BY s.id_socio, u.nombre, u.apellidos, u.dni";

        List<Object[]> filasAct = db.executeQueryArray(sqlAct, prefijo);

        return combinar(filasRes, filasAct);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Generación del fichero
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Guarda los datos en un fichero de texto plano.
     * Incluye una sección de TOTALES al final con la suma de todos los socios.
     * Si la lista está vacía, no crea fichero y devuelve null.
     *
     * @param lista   Lista de DTOs calculada.
     * @param mes     Mes del cálculo (1-12).
     * @param anio    Año del cálculo.
     * @param rutaDir Directorio destino.
     * @return Ruta completa del fichero, o null si no se creó.
     */
    public String guardarFichero(List<ContabilidadSocioDTO> lista, int mes, int anio, String rutaDir) {
        if (lista == null || lista.isEmpty()) {
            return null;
        }

        String[] nombresMes = {
            "Enero","Febrero","Marzo","Abril","Mayo","Junio",
            "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
        };

        String nombreFichero = String.format("contabilidad_%04d_%02d.txt", anio, mes);
        String ruta = rutaDir + "/" + nombreFichero;

        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {
            pw.printf("==========================================================%n");
            pw.printf("  CONTABILIDAD DE SOCIOS – %s de %04d%n", nombresMes[mes - 1], anio);
            pw.printf("==========================================================%n%n");

            double sumReservas    = 0;
            double sumActividades = 0;
            double sumTotal       = 0;

            for (ContabilidadSocioDTO dto : lista) {
                pw.printf("Socio  : %s %s%n", dto.getNombre(), dto.getApellidos());
                pw.printf("DNI    : %s%n", dto.getDni());
                pw.printf("  Coste reservas     : %8.2f EUR%n", dto.getTotalReservas());
                pw.printf("  Coste actividades  : %8.2f EUR%n", dto.getTotalActividades());
                pw.printf("  DEUDA TOTAL        : %8.2f EUR%n", dto.getTotalDeuda());
                pw.printf("----------------------------------------------------------%n");

                sumReservas    += dto.getTotalReservas();
                sumActividades += dto.getTotalActividades();
                sumTotal       += dto.getTotalDeuda();
            }

            pw.printf("%n==========================================================%n");
            pw.printf("  TOTALES GLOBALES%n");
            pw.printf("==========================================================%n");
            pw.printf("  Nº de socios con cargos : %d%n",        lista.size());
            pw.printf("  Total coste reservas    : %8.2f EUR%n", sumReservas);
            pw.printf("  Total coste actividades : %8.2f EUR%n", sumActividades);
            pw.printf("  TOTAL DEUDA             : %8.2f EUR%n", sumTotal);
            pw.printf("==========================================================%n");

        } catch (IOException e) {
            throw new ApplicationException("Error al guardar el fichero: " + e.getMessage());
        }

        return ruta;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────────

    private List<ContabilidadSocioDTO> combinar(List<Object[]> filasRes, List<Object[]> filasAct) {
        Map<Integer, ContabilidadSocioDTO> mapa = new LinkedHashMap<>();

        for (Object[] f : filasRes) {
            ContabilidadSocioDTO dto = new ContabilidadSocioDTO();
            dto.setIdSocio(   toInt(f[0])    );
            dto.setNombre(    str(f[1])       );
            dto.setApellidos( str(f[2])       );
            dto.setDni(       str(f[3])       );
            dto.setTotalReservas( toDouble(f[4]) );
            mapa.put(dto.getIdSocio(), dto);
        }

        for (Object[] f : filasAct) {
            int id = toInt(f[0]);
            ContabilidadSocioDTO dto = mapa.get(id);
            if (dto == null) {
                dto = new ContabilidadSocioDTO();
                dto.setIdSocio(   toInt(f[0]) );
                dto.setNombre(    str(f[1])    );
                dto.setApellidos( str(f[2])    );
                dto.setDni(       str(f[3])    );
                mapa.put(id, dto);
            }
            dto.setTotalActividades( toDouble(f[4]) );
        }

        List<ContabilidadSocioDTO> resultado = new ArrayList<>(mapa.values());
        for (ContabilidadSocioDTO dto : resultado) {
            dto.setTotalDeuda( dto.getTotalReservas() + dto.getTotalActividades() );
        }
        return resultado;
    }

    private int    toInt   (Object o) { return o instanceof Number ? ((Number) o).intValue()   : 0;   }
    private double toDouble(Object o) { return o instanceof Number ? ((Number) o).doubleValue() : 0.0; }
    private String str     (Object o) { return o != null ? o.toString() : "";                         }
}