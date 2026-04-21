package si.pl14.contabilidadSocios;

import si.pl14.util.ApplicationException;
import si.pl14.util.Database;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Modelo MVC para la HU "Calcular contabilidad de socios en un mes".
 *
 * Calcula:
 *  - Total de coste de reservas directas de instalaciones (id_socio NOT NULL, id_actividad NULL)
 *    agrupadas por socio para el mes/año seleccionado.
 *  - Total de coste de actividades inscritas (precio_socio) por socio
 *    cuyas inscripciones pertenezcan al mes/año seleccionado.
 *
 * Genera un fichero .txt con los datos de cada socio, los totales finales
 * y un bloque de estadísticas descriptivas (media, mediana, moda y desviación
 * típica) calculadas sobre las deudas totales de cada socio.
 * Si no hay datos, NO genera fichero.
 *
 * Restricción: solo se permiten consultas desde el mes actual hasta hace un año.
 * No se pueden generar informes de fechas futuras ni anteriores a 12 meses.
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
     * Lista vacía si no hay reservas ni inscripciones de socios ese mes.
     *
     * @throws ApplicationException si el mes/año solicitado es futuro
     *                              o anterior a 12 meses desde hoy.
     */
    public List<ContabilidadSocioDTO> calcularContabilidad(int mes, int anio) {
        // ── Validación de rango de fechas ──────────────────────────────────
        LocalDate fechaSolicitada = LocalDate.of(anio, mes, 1);
        LocalDate fechaHoy        = LocalDate.now().withDayOfMonth(1);
        LocalDate haceUnAnio      = fechaHoy.minusYears(1);

        if (fechaSolicitada.isAfter(fechaHoy)) {
            throw new ApplicationException(
                "No se puede generar un informe para un mes futuro.\n" +
                "Solo se permiten consultas hasta el mes actual (" +
                fechaHoy.getMonthValue() + "/" + fechaHoy.getYear() + ")."
            );
        }
        if (fechaSolicitada.isBefore(haceUnAnio)) {
            throw new ApplicationException(
                "No se puede consultar más de un año hacia atrás.\n" +
                "La fecha mínima permitida es " +
                haceUnAnio.getMonthValue() + "/" + haceUnAnio.getYear() + "."
            );
        }
        // ──────────────────────────────────────────────────────────────────

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
     * Incluye:
     *   · Un bloque por socio con sus costes desglosados.
     *   · Una sección de TOTALES GLOBALES.
     *   · Una sección de ESTADÍSTICAS DESCRIPTIVAS sobre las deudas totales
     *     de todos los socios (media, mediana, moda y desviación típica poblacional).
     *
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

        // Extraer las deudas totales para las estadísticas
        List<Double> deudas = lista.stream()
            .map(ContabilidadSocioDTO::getTotalDeuda)
            .collect(Collectors.toList());

        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {

            // ── Cabecera ──────────────────────────────────────────────────
            pw.printf("==========================================================%n");
            pw.printf("  CONTABILIDAD DE SOCIOS – %s de %04d%n", nombresMes[mes - 1], anio);
            pw.printf("==========================================================%n%n");

            // ── Detalle por socio ─────────────────────────────────────────
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

            // ── Totales globales ──────────────────────────────────────────
            pw.printf("%n==========================================================%n");
            pw.printf("  TOTALES GLOBALES%n");
            pw.printf("==========================================================%n");
            pw.printf("  Nº de socios con cargos : %d%n",        lista.size());
            pw.printf("  Total coste reservas    : %8.2f EUR%n", sumReservas);
            pw.printf("  Total coste actividades : %8.2f EUR%n", sumActividades);
            pw.printf("  TOTAL DEUDA             : %8.2f EUR%n", sumTotal);
            pw.printf("==========================================================%n");

            // ── Estadísticas descriptivas sobre las deudas totales ────────
            pw.printf("%n==========================================================%n");
            pw.printf("  ESTADÍSTICAS DESCRIPTIVAS (deuda total por socio)%n");
            pw.printf("==========================================================%n");
            pw.printf("  Media              : %8.2f EUR%n", calcularMedia(deudas));
            pw.printf("  Mediana            : %8.2f EUR%n", calcularMediana(deudas));
            pw.printf("  Moda               : %s%n",        calcularModaTexto(deudas));
            pw.printf("  Desviación típica  : %8.2f EUR%n", calcularDesviacionTipica(deudas));
            pw.printf("==========================================================%n");

        } catch (IOException e) {
            throw new ApplicationException("Error al guardar el fichero: " + e.getMessage());
        }

        return ruta;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos estadísticos privados
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Media aritmética de los valores de la lista.
     * Devuelve 0 si la lista está vacía.
     */
    private double calcularMedia(List<Double> valores) {
        if (valores.isEmpty()) return 0.0;
        double suma = 0;
        for (double v : valores) suma += v;
        return suma / valores.size();
    }

    /**
     * Mediana: valor central al ordenar la lista.
     * Si el número de elementos es par, devuelve la media de los dos centrales.
     */
    private double calcularMediana(List<Double> valores) {
        if (valores.isEmpty()) return 0.0;
        List<Double> ordenados = new ArrayList<>(valores);
        Collections.sort(ordenados);
        int n = ordenados.size();
        if (n % 2 == 1) {
            return ordenados.get(n / 2);
        } else {
            return (ordenados.get(n / 2 - 1) + ordenados.get(n / 2)) / 2.0;
        }
    }

    /**
     * Desviación típica poblacional (σ): raíz cuadrada de la varianza poblacional.
     * Se usa la versión poblacional (dividir entre N) ya que se trabaja con el
     * conjunto completo de socios del mes, no con una muestra.
     */
    private double calcularDesviacionTipica(List<Double> valores) {
        if (valores.size() < 2) return 0.0;
        double media    = calcularMedia(valores);
        double varianza = 0;
        for (double v : valores) {
            double diff = v - media;
            varianza += diff * diff;
        }
        varianza /= valores.size();
        return Math.sqrt(varianza);
    }

    /**
     * Moda: valor(es) que aparece(n) con mayor frecuencia.
     * Los valores se redondean a 2 decimales para la comparación,
     * evitando falsos negativos por errores de punto flotante.
     *
     * Casos posibles:
     *  · Un único valor más frecuente  → "XX.XX EUR (aparece N veces)"
     *  · Varios valores empatados      → lista de valores con su frecuencia
     *  · Todos con frecuencia 1        → "No hay moda única (todos los valores son distintos)"
     */
    private String calcularModaTexto(List<Double> valores) {
        if (valores.isEmpty()) return "–";

        // Contar frecuencias redondeando a 2 decimales
        Map<Long, Integer> frecuencias = new LinkedHashMap<>();
        for (double v : valores) {
            long clave = Math.round(v * 100); // equivale a redondear a 2 dec
            frecuencias.merge(clave, 1, Integer::sum);
        }

        int maxFrec = Collections.max(frecuencias.values());

        // Si todos aparecen solo una vez → no hay moda única
        if (maxFrec == 1) {
            return "No hay moda única (todos los valores son distintos)";
        }

        // Recoger todos los valores con la frecuencia máxima
        List<String> modas = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : frecuencias.entrySet()) {
            if (e.getValue() == maxFrec) {
                double valor = e.getKey() / 100.0;
                modas.add(String.format("%.2f EUR", valor));
            }
        }

        if (modas.size() == 1) {
            return String.format("%s (aparece %d veces)", modas.get(0), maxFrec);
        } else {
            return String.join(", ", modas) +
                   String.format(" (cada una aparece %d veces)", maxFrec);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados de combinación de resultados SQL
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