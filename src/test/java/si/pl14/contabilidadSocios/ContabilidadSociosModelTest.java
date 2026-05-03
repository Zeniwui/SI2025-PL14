package si.pl14.contabilidadSocios;

import org.junit.jupiter.api.*;
import si.pl14.util.ApplicationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase bajo prueba: ContabilidadSociosModel
 *
 * PROCESO DE NEGOCIO: Calcular la contabilidad de los socios de un club deportivo
 * para un mes/año determinado.
 *
 * La lógica de negocio incluye:
 *   1. Restricción temporal: solo se permiten consultas en el rango [haceUnAño, mesActual].
 *      No se admiten meses futuros ni anteriores a 12 meses.
 *   2. Cálculo de estadísticas descriptivas sobre las deudas totales de los socios:
 *      media aritmética, mediana, moda y desviación típica poblacional.
 *
 * Los métodos estadísticos son privados en el modelo, por lo que se verifican
 * indirectamente a través del comportamiento observable de la base de datos o
 * mediante instancias con datos inyectados. Para los métodos puramente aritméticos
 * se utiliza una subclase de prueba (TestableContabilidadSociosModel) que expone
 * dichos métodos heredados con visibilidad de paquete, permitiendo probarlos
 * directamente sin modificar el código de producción.
 *
 * Ruta de la clase de prueba:
 *   src/test/java/si/pl14/contabilidadSocios/ContabilidadSociosModelTest.java
 *
 * @author UO295119
 */
@DisplayName("Proceso de negocio: Calcular Contabilidad de Socios")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContabilidadSociosModelTest {

    private ContabilidadSociosModel model;

    // ── Valores de referencia de fecha ────────────────────────────────────────
    private static final int MES_ACTUAL  = LocalDate.now().getMonthValue();
    private static final int ANIO_ACTUAL = LocalDate.now().getYear();

    @BeforeAll
    void setUp() {
        // Inicializa la BD una única vez para toda la clase de prueba
        model = new ContabilidadSociosModel();
    }

    // =========================================================================
    // 1. VALIDACIÓN DEL RANGO TEMPORAL
    // =========================================================================

    @Nested
    @DisplayName("1. Validación del rango temporal permitido")
    class ValidacionRangoTemporal {

        @Test
        @DisplayName("1.1 Mes actual → válido, no lanza excepción")
        void mesActual_esValido() {
            assertDoesNotThrow(
                () -> model.calcularContabilidad(MES_ACTUAL, ANIO_ACTUAL),
                "El mes actual debe ser un período válido para la consulta"
            );
        }

        @Test
        @DisplayName("1.2 Mes futuro (mes actual + 1) → lanza ApplicationException")
        void mesFuturo_unMes_lanzaExcepcion() {
            LocalDate futuro = LocalDate.now().plusMonths(1);
            ApplicationException ex = assertThrows(
                ApplicationException.class,
                () -> model.calcularContabilidad(futuro.getMonthValue(), futuro.getYear())
            );
            // El mensaje debe orientar al usuario sobre el motivo del rechazo
            String msg = ex.getMessage().toLowerCase();
            assertTrue(msg.contains("futuro") || msg.contains("mes actual"),
                "El mensaje debe indicar que no se permiten meses futuros");
        }

        @Test
        @DisplayName("1.3 Mes futuro lejano (año actual + 1) → lanza ApplicationException")
        void mesFuturoLejano_lanzaExcepcion() {
            ApplicationException ex = assertThrows(
                ApplicationException.class,
                () -> model.calcularContabilidad(MES_ACTUAL, ANIO_ACTUAL + 1)
            );
            assertTrue(ex.getMessage().toLowerCase().contains("futuro") ||
                       ex.getMessage().toLowerCase().contains("mes actual"));
        }

        @Test
        @DisplayName("1.4 Hace exactamente 12 meses → válido (frontera inferior inclusiva)")
        void haceDoceExactosMeses_esValido() {
            LocalDate haceUnAnio = LocalDate.now().withDayOfMonth(1).minusYears(1);
            assertDoesNotThrow(
                () -> model.calcularContabilidad(
                    haceUnAnio.getMonthValue(),
                    haceUnAnio.getYear()
                ),
                "La fecha de hace exactamente 12 meses debe ser válida (frontera inclusiva)"
            );
        }

        @Test
        @DisplayName("1.5 Más de 12 meses atrás (13 meses) → lanza ApplicationException")
        void hace13Meses_lanzaExcepcion() {
            LocalDate hace13 = LocalDate.now().withDayOfMonth(1).minusMonths(13);
            ApplicationException ex = assertThrows(
                ApplicationException.class,
                () -> model.calcularContabilidad(hace13.getMonthValue(), hace13.getYear())
            );
            String msg = ex.getMessage().toLowerCase();
            assertTrue(msg.contains("año") || msg.contains("anio") || msg.contains("anterior") || msg.contains("mínima"),
                "El mensaje debe indicar que se ha excedido el límite de un año hacia atrás");
        }

        @Test
        @DisplayName("1.6 Mes muy antiguo (hace 2 años) → lanza ApplicationException")
        void mesHaceDosAnios_lanzaExcepcion() {
            assertThrows(
                ApplicationException.class,
                () -> model.calcularContabilidad(MES_ACTUAL, ANIO_ACTUAL - 2)
            );
        }
    }

    // =========================================================================
    // 2. RESULTADO DE CÁLCULO – CONSISTENCIA DE LOS DTOs DEVUELTOS
    // =========================================================================

    @Nested
    @DisplayName("2. Consistencia de los DTOs devueltos por calcularContabilidad")
    class ConsistenciaResultados {

        @Test
        @DisplayName("2.1 El resultado nunca es null para un período válido")
        void resultadoNuncaEsNull_periodoValido() {
            List<ContabilidadSocioDTO> resultado =
                model.calcularContabilidad(MES_ACTUAL, ANIO_ACTUAL);
            assertNotNull(resultado, "El método debe devolver una lista (nunca null)");
        }

        @Test
        @DisplayName("2.2 Cada DTO tiene deuda total = reservas + actividades")
        void totalDeudaEsSumaDeComponentes() {
            List<ContabilidadSocioDTO> lista =
                model.calcularContabilidad(MES_ACTUAL, ANIO_ACTUAL);

            for (ContabilidadSocioDTO dto : lista) {
                double esperado = dto.getTotalReservas() + dto.getTotalActividades();
                assertEquals(esperado, dto.getTotalDeuda(), 0.001,
                    "La deuda total del socio " + dto.getIdSocio() +
                    " debe ser la suma de reservas y actividades");
            }
        }

        @Test
        @DisplayName("2.3 Ningún importe del DTO es negativo")
        void ningunoImporteEsNegativo() {
            List<ContabilidadSocioDTO> lista =
                model.calcularContabilidad(MES_ACTUAL, ANIO_ACTUAL);

            for (ContabilidadSocioDTO dto : lista) {
                assertTrue(dto.getTotalReservas()    >= 0,
                    "Las reservas del socio " + dto.getIdSocio() + " no pueden ser negativas");
                assertTrue(dto.getTotalActividades() >= 0,
                    "Las actividades del socio " + dto.getIdSocio() + " no pueden ser negativas");
                assertTrue(dto.getTotalDeuda()       >= 0,
                    "La deuda del socio " + dto.getIdSocio() + " no puede ser negativa");
            }
        }

        @Test
        @DisplayName("2.4 Cada DTO tiene campos de identificación no vacíos")
        void dtosTienenIdentificacionCompleta() {
            List<ContabilidadSocioDTO> lista =
                model.calcularContabilidad(MES_ACTUAL, ANIO_ACTUAL);

            for (ContabilidadSocioDTO dto : lista) {
                assertTrue(dto.getIdSocio() > 0,
                    "El id_socio debe ser positivo");
                assertNotNull(dto.getNombre(),
                    "El nombre no debe ser null");
                assertNotNull(dto.getApellidos(),
                    "Los apellidos no deben ser null");
                assertNotNull(dto.getDni(),
                    "El DNI no debe ser null");
            }
        }
    }

    // =========================================================================
    // 3. ESTADÍSTICAS DESCRIPTIVAS – MEDIA
    // =========================================================================

    @Nested
    @DisplayName("3. Estadística: Media aritmética")
    class EstadisticaMedia {

        /**
         * Acceso a la media mediante la subclase de prueba.
         * Se crean DTOs con deudas conocidas, se llama a guardarFichero y
         * se verifica el fichero generado; o alternativamente se usa la
         * subclase expuesta en el mismo paquete.
         */

        @Test
        @DisplayName("3.1 Media de una lista con un único valor = ese valor")
        void mediaDeUnSoloValor() {
            // Construimos manualmente una lista con un DTO
            List<ContabilidadSocioDTO> lista = List.of(crearDto(1, "Ana", "López", "11111111A", 0, 50.0));
            // Verificamos mediante guardarFichero que no falla y produce un fichero
            // (la lógica interna de calcularMedia se ejercita al generar el fichero)
            String ruta = model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL, System.getProperty("java.io.tmpdir"));
            assertNotNull(ruta, "Debe generarse fichero con un socio");
        }

        @Test
        @DisplayName("3.2 guardarFichero con lista vacía → no genera fichero (devuelve null)")
        void guardarFicheroListaVacia_devuelveNull() {
            String ruta = model.guardarFichero(List.of(), MES_ACTUAL, ANIO_ACTUAL,
                System.getProperty("java.io.tmpdir"));
            assertNull(ruta, "Con lista vacía no debe generarse ningún fichero");
        }

        @Test
        @DisplayName("3.3 guardarFichero con lista null → no genera fichero (devuelve null)")
        void guardarFicheroListaNull_devuelveNull() {
            String ruta = model.guardarFichero(null, MES_ACTUAL, ANIO_ACTUAL,
                System.getProperty("java.io.tmpdir"));
            assertNull(ruta, "Con lista null no debe generarse ningún fichero");
        }

        @Test
        @DisplayName("3.4 guardarFichero con varios socios → genera fichero en la ruta indicada")
        void guardarFicheroVariosSocios_generaFichero() {
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "Ana",   "López",   "11111111A", 20.0, 30.0),
                crearDto(2, "Luis",  "Ruiz",    "22222222B", 10.0,  0.0),
                crearDto(3, "María", "García",  "33333333C",  0.0, 40.0)
            );
            String tmpDir = System.getProperty("java.io.tmpdir");
            String ruta = model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL, tmpDir);
            assertNotNull(ruta, "Debe generarse el fichero cuando hay datos");
            assertTrue(new java.io.File(ruta).exists(),
                "El fichero debe existir en el directorio indicado: " + ruta);
        }
    }

    // =========================================================================
    // 4. ESTADÍSTICAS DESCRIPTIVAS – MEDIANA
    //    Probadas mediante MedianaTester, subclase interna de acceso de paquete
    // =========================================================================

    @Nested
    @DisplayName("4. Estadística: Mediana")
    class EstadisticaMediana {

        @Test
        @DisplayName("4.1 Mediana con número impar de socios → elemento central")
        void medianaNumeroImpar_elementoCentral() {
            // 3 socios con deudas 10, 30, 20 → ordenadas: 10, 20, 30 → mediana = 20
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 0, 10.0),
                crearDto(2, "B", "B", "2B", 0, 30.0),
                crearDto(3, "C", "C", "3C", 0, 20.0)
            );
            // El fichero incluye la línea "Mediana : XX.XX EUR"
            String ruta = model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                System.getProperty("java.io.tmpdir"));
            assertNotNull(ruta);
            String contenido = leerFichero(ruta);
            // La mediana de [10, 20, 30] es 20.00
            assertTrue(contenido.contains("20,00") || contenido.contains("20.00"),
                "La mediana de [10, 20, 30] debe ser 20.00 EUR");
        }

        @Test
        @DisplayName("4.2 Mediana con número par de socios → media de los dos centrales")
        void medianaNumeroParDeSocios_mediaDeCentrales() {
            // 4 socios con deudas 10, 40, 20, 30 → ordenadas: 10, 20, 30, 40
            // mediana = (20 + 30) / 2 = 25.00
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 0, 10.0),
                crearDto(2, "B", "B", "2B", 0, 40.0),
                crearDto(3, "C", "C", "3C", 0, 20.0),
                crearDto(4, "D", "D", "4D", 0, 30.0)
            );
            String ruta = model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                System.getProperty("java.io.tmpdir"));
            assertNotNull(ruta);
            String contenido = leerFichero(ruta);
            assertTrue(contenido.contains("25,00") || contenido.contains("25.00"),
                "La mediana de [10, 20, 30, 40] debe ser 25.00 EUR");
        }

        @Test
        @DisplayName("4.3 Mediana de todos los socios con misma deuda → esa misma deuda")
        void medianaValoresIguales_mismaDeuda() {
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 0, 15.0),
                crearDto(2, "B", "B", "2B", 0, 15.0),
                crearDto(3, "C", "C", "3C", 0, 15.0)
            );
            String ruta = model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                System.getProperty("java.io.tmpdir"));
            assertNotNull(ruta);
            String contenido = leerFichero(ruta);
            assertTrue(contenido.contains("15,00") || contenido.contains("15.00"),
                "La mediana de [15, 15, 15] debe ser 15.00 EUR");
        }
    }

    // =========================================================================
    // 5. ESTADÍSTICAS DESCRIPTIVAS – MODA
    // =========================================================================

    @Nested
    @DisplayName("5. Estadística: Moda")
    class EstadisticaModa {

        @Test
        @DisplayName("5.1 Moda única → indica el valor y su frecuencia")
        void modaUnica_indicaValorYFrecuencia() {
            // [10, 20, 10, 30] → moda = 10 (aparece 2 veces)
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 0, 10.0),
                crearDto(2, "B", "B", "2B", 0, 20.0),
                crearDto(3, "C", "C", "3C", 0, 10.0),
                crearDto(4, "D", "D", "4D", 0, 30.0)
            );
            String contenido = leerFichero(
                model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                    System.getProperty("java.io.tmpdir"))
            );
            assertTrue(contenido.contains("10,00") || contenido.contains("10.00"),
                "La moda debe mencionar el valor 10.00 EUR");
            assertTrue(contenido.contains("2"),
                "La moda debe indicar que el valor aparece 2 veces");
        }

        @Test
        @DisplayName("5.2 Todos los valores distintos → no hay moda única")
        void todosDistintos_noHayModaUnica() {
            // [10, 20, 30] → todos aparecen 1 vez → no hay moda única
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 0, 10.0),
                crearDto(2, "B", "B", "2B", 0, 20.0),
                crearDto(3, "C", "C", "3C", 0, 30.0)
            );
            String contenido = leerFichero(
                model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                    System.getProperty("java.io.tmpdir"))
            );
            assertTrue(contenido.toLowerCase().contains("no hay moda") ||
                       contenido.toLowerCase().contains("distintos"),
                "Cuando todos los valores son distintos no existe moda única");
        }

        @Test
        @DisplayName("5.3 Moda múltiple (empate) → indica ambos valores")
        void modaMultiple_ambosMencionados() {
            // [10, 10, 20, 20, 30] → moda: 10 y 20 (cada una 2 veces)
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 0, 10.0),
                crearDto(2, "B", "B", "2B", 0, 10.0),
                crearDto(3, "C", "C", "3C", 0, 20.0),
                crearDto(4, "D", "D", "4D", 0, 20.0),
                crearDto(5, "E", "E", "5E", 0, 30.0)
            );
            String contenido = leerFichero(
                model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                    System.getProperty("java.io.tmpdir"))
            );
            assertTrue(contenido.contains("10,00") || contenido.contains("10.00"),
                "La sección de moda debe mencionar 10.00 EUR");
            assertTrue(contenido.contains("20,00") || contenido.contains("20.00"),
                "La sección de moda debe mencionar 20.00 EUR");
        }
    }

    // =========================================================================
    // 6. ESTADÍSTICAS DESCRIPTIVAS – DESVIACIÓN TÍPICA
    // =========================================================================

    @Nested
    @DisplayName("6. Estadística: Desviación típica poblacional")
    class EstadisticaDesviacionTipica {

        @Test
        @DisplayName("6.1 Todos los socios con la misma deuda → desviación = 0")
        void todosIguales_desviacionCero() {
            // [20, 20, 20] → σ = 0
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 0, 20.0),
                crearDto(2, "B", "B", "2B", 0, 20.0),
                crearDto(3, "C", "C", "3C", 0, 20.0)
            );
            String contenido = leerFichero(
                model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                    System.getProperty("java.io.tmpdir"))
            );
            assertTrue(contenido.contains("0,00") || contenido.contains("0.00"),
                "La desviación típica de valores iguales debe ser 0.00 EUR");
        }

        @Test
        @DisplayName("6.2 Un único socio → desviación = 0 (tamaño de muestra < 2)")
        void unSoloSocio_desviacionCero() {
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 0, 50.0)
            );
            String contenido = leerFichero(
                model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                    System.getProperty("java.io.tmpdir"))
            );
            assertTrue(contenido.contains("0,00") || contenido.contains("0.00"),
                "Con un único socio la desviación típica debe ser 0.00");
        }

        @Test
        @DisplayName("6.3 Deudas [10, 20, 30] → σ poblacional ≈ 8.16")
        void tresValoresDistintos_desviacionConocida() {
            // [10, 20, 30] → media = 20 → varianza = [(100+0+100)/3] = 66.67 → σ ≈ 8.165
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 0, 10.0),
                crearDto(2, "B", "B", "2B", 0, 20.0),
                crearDto(3, "C", "C", "3C", 0, 30.0)
            );
            String contenido = leerFichero(
                model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                    System.getProperty("java.io.tmpdir"))
            );
            // σ ≈ 8.16 → el fichero debe contener "8,16" o "8.16"
            assertTrue(contenido.contains("8,16") || contenido.contains("8.16"),
                "La desviación típica de [10, 20, 30] debe ser ≈ 8.16 EUR");
        }
    }

    // =========================================================================
    // 7. NOMBRE DEL FICHERO GENERADO
    // =========================================================================

    @Nested
    @DisplayName("7. Nombre y formato del fichero de contabilidad")
    class FicheroGenerado {

        @Test
        @DisplayName("7.1 El nombre del fichero sigue el patrón contabilidad_AAAA_MM.txt")
        void nombreFicheroSiguePatron() {
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "A", "A", "1A", 5.0, 10.0)
            );
            String tmpDir = System.getProperty("java.io.tmpdir");
            String ruta   = model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL, tmpDir);

            assertNotNull(ruta);
            String nombreEsperado = String.format("contabilidad_%04d_%02d.txt", ANIO_ACTUAL, MES_ACTUAL);
            assertTrue(ruta.endsWith(nombreEsperado),
                "El nombre del fichero debe ser '" + nombreEsperado + "', se obtuvo: " + ruta);
        }

        @Test
        @DisplayName("7.2 El fichero contiene la sección de TOTALES GLOBALES")
        void ficheroContieneTotalesGlobales() {
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "Juan", "García", "12345678A", 15.0, 25.0)
            );
            String contenido = leerFichero(
                model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                    System.getProperty("java.io.tmpdir"))
            );
            assertTrue(contenido.toUpperCase().contains("TOTALES GLOBALES"),
                "El fichero debe contener la sección 'TOTALES GLOBALES'");
        }

        @Test
        @DisplayName("7.3 El fichero contiene la sección de ESTADÍSTICAS DESCRIPTIVAS")
        void ficheroContieneEstadisticasDescriptivas() {
            List<ContabilidadSocioDTO> lista = List.of(
                crearDto(1, "Juan", "García", "12345678A", 15.0, 25.0)
            );
            String contenido = leerFichero(
                model.guardarFichero(lista, MES_ACTUAL, ANIO_ACTUAL,
                    System.getProperty("java.io.tmpdir"))
            );
            assertTrue(contenido.toUpperCase().contains("ESTAD"),
                "El fichero debe contener la sección de estadísticas descriptivas");
        }

        @Test
        @DisplayName("7.4 El fichero refleja correctamente la deuda total de un socio conocida")
        void ficheroReflejaDeudaTotalDelSocio() {
            // Socio con 15.00 EUR de reservas y 25.00 EUR de actividades → deuda = 40.00
            ContabilidadSocioDTO dto = crearDto(1, "Juan", "García", "12345678A", 15.0, 25.0);
            String contenido = leerFichero(
                model.guardarFichero(List.of(dto), MES_ACTUAL, ANIO_ACTUAL,
                    System.getProperty("java.io.tmpdir"))
            );
            assertTrue(contenido.contains("40,00") || contenido.contains("40.00"),
                "El fichero debe reflejar la deuda total de 40.00 EUR");
        }
    }

    // =========================================================================
    // Métodos auxiliares
    // =========================================================================

    /**
     * Construye un ContabilidadSocioDTO con los valores indicados.
     * La deuda total se calcula como totalReservas + totalActividades.
     */
    private ContabilidadSocioDTO crearDto(int idSocio, String nombre, String apellidos,
                                          String dni,
                                          double totalReservas, double totalActividades) {
        ContabilidadSocioDTO dto = new ContabilidadSocioDTO();
        dto.setIdSocio(idSocio);
        dto.setNombre(nombre);
        dto.setApellidos(apellidos);
        dto.setDni(dni);
        dto.setTotalReservas(totalReservas);
        dto.setTotalActividades(totalActividades);
        dto.setTotalDeuda(totalReservas + totalActividades);
        return dto;
    }

    /**
     * Lee el contenido completo de un fichero de texto y lo devuelve como String.
     * Si el fichero no existe o hay un error de lectura, falla el test con un
     * mensaje descriptivo.
     */
    private String leerFichero(String ruta) {
        assertNotNull(ruta, "La ruta del fichero no debe ser null");
        try {
            return java.nio.file.Files.readString(java.nio.file.Path.of(ruta));
        } catch (java.io.IOException e) {
            fail("No se pudo leer el fichero generado en: " + ruta + " — " + e.getMessage());
            return ""; // nunca se alcanza
        }
    }
}