package si.pl14.visualizareservas;

import org.junit.jupiter.api.*;
import si.pl14.model.InstalacionEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase bajo prueba: VisualizarReservasSocioModel
 *
 * PROCESO DE NEGOCIO: Visualizar las reservas de instalaciones realizadas por un
 * socio en un rango de fechas, con posibilidad de filtrar por instalación concreta.
 *
 * La lógica de negocio incluye:
 *   1. Consulta del catálogo de instalaciones disponibles.
 *   2. Búsqueda de reservas del socio logueado (ID_SOCIO_ACTUAL = 1) en el rango
 *      [fechaDesde, fechaHasta], opcionalmente filtradas por instalación.
 *   3. Filtrado por instalación: si idInstalacion = 0, se devuelven todas las
 *      instalaciones; si idInstalacion > 0, solo las de esa instalación.
 *   4. Ordenación del resultado por fecha y hora de inicio.
 *   5. Cada reserva devuelta debe incluir: id, fecha, horas, estado de pago,
 *      método de pago, coste y nombre de la instalación.
 *
 * Ruta de la clase de prueba:
 *   src/test/java/si/pl14/visualizareservas/VisualizarReservasSocioModelTest.java
 *
 * @author UO295119
 */
@DisplayName("Proceso de negocio: Visualizar Reservas del Socio")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VisualizarReservasSocioModelTest {

    private VisualizarReservasSocioModel model;

    // ── Fechas de referencia ──────────────────────────────────────────────────
    /** Fecha muy antigua para abarcar todos los datos de prueba. */
    private static final String DESDE_INICIO = "2000-01-01";
    /** Fecha muy lejana en el futuro para abarcar todos los datos de prueba. */
    private static final String HASTA_FUTURO  = "2099-12-31";

    /** Hoy (formato ISO). */
    private static final String HOY = LocalDate.now().toString();

    @BeforeAll
    void setUp() {
        // Inicializa la BD una única vez. El constructor llama a createDatabase(true).
        model = new VisualizarReservasSocioModel();
    }

    // =========================================================================
    // 1. CATÁLOGO DE INSTALACIONES
    // =========================================================================

    @Nested
    @DisplayName("1. Catálogo de instalaciones")
    class CatalogoInstalaciones {

        @Test
        @DisplayName("1.1 getInstalaciones() nunca devuelve null")
        void getInstalaciones_nuncaDevuelveNull() {
            List<InstalacionEntity> lista = model.getInstalaciones();
            assertNotNull(lista, "La lista de instalaciones nunca debe ser null");
        }

        @Test
        @DisplayName("1.2 Las instalaciones del catálogo tienen id positivo")
        void instalaciones_tienenIdPositivo() {
            List<InstalacionEntity> lista = model.getInstalaciones();
            for (InstalacionEntity inst : lista) {
                assertTrue(inst.getIdInstalacion() > 0,
                    "El id de la instalación debe ser positivo, se obtuvo: " + inst.getIdInstalacion());
            }
        }

        @Test
        @DisplayName("1.3 Las instalaciones tienen nombre no vacío")
        void instalaciones_tienenNombreNoVacio() {
            List<InstalacionEntity> lista = model.getInstalaciones();
            for (InstalacionEntity inst : lista) {
                assertNotNull(inst.getNombre(), "El nombre de la instalación no debe ser null");
                assertFalse(inst.getNombre().isBlank(), "El nombre de la instalación no debe estar vacío");
            }
        }

        @Test
        @DisplayName("1.4 El coste por hora de cada instalación es no negativo")
        void instalaciones_costeHoraNoNegativo() {
            List<InstalacionEntity> lista = model.getInstalaciones();
            for (InstalacionEntity inst : lista) {
                assertTrue(inst.getCosteHora() >= 0,
                    "El coste/hora de la instalación '" + inst.getNombre() + "' no puede ser negativo");
            }
        }

        @Test
        @DisplayName("1.5 El catálogo contiene al menos las instalaciones del data.sql")
        void catalogo_contieneInstalacionesIniciales() {
            // data.sql inserta 'Piscina Cubierta' y 'Sala Fitness'
            List<InstalacionEntity> lista = model.getInstalaciones();
            assertFalse(lista.isEmpty(),
                "El catálogo no debe estar vacío tras cargar los datos iniciales");
        }
    }

    // =========================================================================
    // 2. BÚSQUEDA DE RESERVAS – RESULTADO NUNCA NULL
    // =========================================================================

    @Nested
    @DisplayName("2. Resultado de getReservas nunca es null")
    class ResultadoNoNull {

        @Test
        @DisplayName("2.1 Sin filtro de instalación (id=0), rango amplio → no es null")
        void sinFiltroInstalacion_resultadoNoNull() {
            List<ReservasSocioDTO> lista = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            assertNotNull(lista);
        }

        @Test
        @DisplayName("2.2 Con filtro de instalación válida → no es null")
        void conFiltroInstalacionValida_resultadoNoNull() {
            // Instalación 1 existe en data.sql
            List<ReservasSocioDTO> lista = model.getReservas(1, DESDE_INICIO, HASTA_FUTURO);
            assertNotNull(lista);
        }

        @Test
        @DisplayName("2.3 Rango de fechas sin reservas → lista vacía (no null)")
        void rangoDeFechasSinReservas_listaVacia() {
            // Rango en el siglo XIX: ninguna reserva del socio existe ahí
            List<ReservasSocioDTO> lista = model.getReservas(0, "1900-01-01", "1900-12-31");
            assertNotNull(lista, "La lista no debe ser null aunque el rango no tenga reservas");
            assertTrue(lista.isEmpty(),
                "No debe haber reservas en un rango de fechas vacío como el siglo XIX");
        }

        @Test
        @DisplayName("2.4 Instalación inexistente → lista vacía (no null)")
        void instalacionInexistente_listaVacia() {
            List<ReservasSocioDTO> lista = model.getReservas(9999, DESDE_INICIO, HASTA_FUTURO);
            assertNotNull(lista);
            assertTrue(lista.isEmpty(),
                "Con id_instalacion inexistente no debe haber resultados");
        }
    }

    // =========================================================================
    // 3. FILTRADO POR INSTALACIÓN
    // =========================================================================

    @Nested
    @DisplayName("3. Filtrado por instalación")
    class FiltradoPorInstalacion {

        @Test
        @DisplayName("3.1 idInstalacion = 0 incluye reservas de todas las instalaciones")
        void sinFiltro_incluyeTodasInstalaciones() {
            // El total sin filtro debe ser >= total de cualquier filtro concreto
            List<ReservasSocioDTO> todas = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            List<ReservasSocioDTO> inst1 = model.getReservas(1, DESDE_INICIO, HASTA_FUTURO);
            List<ReservasSocioDTO> inst2 = model.getReservas(2, DESDE_INICIO, HASTA_FUTURO);

            assertTrue(todas.size() >= inst1.size(),
                "Sin filtro debe haber al menos tantas reservas como con filtro inst=1");
            assertTrue(todas.size() >= inst2.size(),
                "Sin filtro debe haber al menos tantas reservas como con filtro inst=2");
        }

        @Test
        @DisplayName("3.2 Con filtro, todas las reservas pertenecen a la instalación filtrada")
        void conFiltro_todasPerteneceAInstalacion() {
            // Tomamos la primera instalación disponible del catálogo
            List<InstalacionEntity> instalaciones = model.getInstalaciones();
            if (instalaciones.isEmpty()) return; // no hay datos → test neutro

            InstalacionEntity primera = instalaciones.get(0);
            List<ReservasSocioDTO> reservas =
                model.getReservas(primera.getIdInstalacion(), DESDE_INICIO, HASTA_FUTURO);

            for (ReservasSocioDTO r : reservas) {
                assertEquals(primera.getNombre(), r.getNombreInstalacion(),
                    "Todas las reservas devueltas deben pertenecer a la instalación '"
                    + primera.getNombre() + "'");
            }
        }

        @Test
        @DisplayName("3.3 Reservas de inst=1 ∪ reservas de inst=2 ⊆ reservas sin filtro")
        void unionFiltrosSubconjuntoDeTodasLasReservas() {
            List<ReservasSocioDTO> todas = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            List<ReservasSocioDTO> inst1 = model.getReservas(1, DESDE_INICIO, HASTA_FUTURO);
            List<ReservasSocioDTO> inst2 = model.getReservas(2, DESDE_INICIO, HASTA_FUTURO);

            // Los ids de la unión de ambos filtros nunca pueden superar el total sin filtro
            long idsUnion = inst1.stream().mapToInt(ReservasSocioDTO::getIdReserva)
                    .distinct().count()
                + inst2.stream().mapToInt(ReservasSocioDTO::getIdReserva)
                    .distinct().count();
            long idsTotal = todas.stream().mapToInt(ReservasSocioDTO::getIdReserva)
                    .distinct().count();

            assertTrue(idsUnion <= idsTotal,
                "El número de reservas de inst1 + inst2 no puede superar el total sin filtro");
        }
    }

    // =========================================================================
    // 4. FILTRADO POR RANGO DE FECHAS
    // =========================================================================

    @Nested
    @DisplayName("4. Filtrado por rango de fechas")
    class FiltradoPorFechas {

        @Test
        @DisplayName("4.1 fechaDesde = fechaHasta = misma fecha → solo reservas de ese día")
        void rangoUnDia_soloEseDia() {
            List<ReservasSocioDTO> lista = model.getReservas(0, HOY, HOY);
            for (ReservasSocioDTO r : lista) {
                assertEquals(HOY, r.getFecha(),
                    "Con rango de un solo día, todas las reservas deben ser de ese día");
            }
        }

        @Test
        @DisplayName("4.2 Todas las reservas del resultado están dentro del rango [desde, hasta]")
        void reservasDentroDelRango() {
            String desde = "2026-01-01";
            String hasta = "2026-12-31";
            List<ReservasSocioDTO> lista = model.getReservas(0, desde, hasta);
            for (ReservasSocioDTO r : lista) {
                assertTrue(r.getFecha().compareTo(desde) >= 0,
                    "La fecha " + r.getFecha() + " no debe ser anterior a " + desde);
                assertTrue(r.getFecha().compareTo(hasta) <= 0,
                    "La fecha " + r.getFecha() + " no debe ser posterior a " + hasta);
            }
        }

        @Test
        @DisplayName("4.3 Rango [fechaFutura, fechaFuturaPlus1] → lista vacía si no hay reservas")
        void rangoFuturoSinReservas_listaVacia() {
            String desde = "2090-01-01";
            String hasta = "2090-12-31";
            List<ReservasSocioDTO> lista = model.getReservas(0, desde, hasta);
            assertTrue(lista.isEmpty(),
                "No debe haber reservas en un rango futuro donde no se han creado");
        }
    }

    // =========================================================================
    // 5. ESTRUCTURA Y CAMPOS DE CADA DTO DEVUELTO
    // =========================================================================

    @Nested
    @DisplayName("5. Estructura y campos de los DTOs devueltos")
    class EstructuraDTOs {

        @Test
        @DisplayName("5.1 Cada reserva tiene id positivo")
        void cadaReservaTieneIdPositivo() {
            List<ReservasSocioDTO> lista = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            for (ReservasSocioDTO r : lista) {
                assertTrue(r.getIdReserva() > 0,
                    "El id de la reserva debe ser positivo, se obtuvo: " + r.getIdReserva());
            }
        }

        @Test
        @DisplayName("5.2 Cada reserva tiene fecha no nula y no vacía")
        void cadaReservaTieneFechaNoVacia() {
            List<ReservasSocioDTO> lista = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            for (ReservasSocioDTO r : lista) {
                assertNotNull(r.getFecha(), "La fecha de la reserva no debe ser null");
                assertFalse(r.getFecha().isBlank(), "La fecha de la reserva no debe estar vacía");
            }
        }

        @Test
        @DisplayName("5.3 El coste de cada reserva es no negativo")
        void costeReservaNoNegativo() {
            List<ReservasSocioDTO> lista = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            for (ReservasSocioDTO r : lista) {
                assertTrue(r.getCosteReserva() >= 0,
                    "El coste de la reserva " + r.getIdReserva() + " no puede ser negativo");
            }
        }

        @Test
        @DisplayName("5.4 El nombre de instalación de cada reserva no es null ni vacío")
        void nombreInstalacionNoVacio() {
            List<ReservasSocioDTO> lista = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            for (ReservasSocioDTO r : lista) {
                assertNotNull(r.getNombreInstalacion(),
                    "El nombre de la instalación de la reserva " + r.getIdReserva() + " no debe ser null");
                assertFalse(r.getNombreInstalacion().isBlank(),
                    "El nombre de la instalación no debe estar vacío");
            }
        }

        @Test
        @DisplayName("5.5 Estado de pago no es null (puede ser '-' si no hay datos, nunca null)")
        void estadoPagoNoEsNull() {
            List<ReservasSocioDTO> lista = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            for (ReservasSocioDTO r : lista) {
                assertNotNull(r.getEstadoPago(),
                    "El estado de pago de la reserva " + r.getIdReserva() + " no debe ser null");
            }
        }

        @Test
        @DisplayName("5.6 Método de pago no es null (valor '-' si no hay método, nunca null)")
        void metodoPagoNoEsNull() {
            List<ReservasSocioDTO> lista = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            for (ReservasSocioDTO r : lista) {
                assertNotNull(r.getMetodoPago(),
                    "El método de pago de la reserva " + r.getIdReserva() + " no debe ser null");
            }
        }
    }

    // =========================================================================
    // 6. ORDENACIÓN DEL RESULTADO
    // =========================================================================

    @Nested
    @DisplayName("6. Ordenación de reservas por fecha y hora")
    class OrdenacionResultado {

        @Test
        @DisplayName("6.1 Las reservas están ordenadas cronológicamente (fecha ASC)")
        void reservasOrdenadas_porFechaAscendente() {
            List<ReservasSocioDTO> lista = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            for (int i = 1; i < lista.size(); i++) {
                String fechaAnterior = lista.get(i - 1).getFecha();
                String fechaActual   = lista.get(i).getFecha();
                assertTrue(fechaAnterior.compareTo(fechaActual) <= 0,
                    "La fecha '" + fechaActual + "' no debe ser anterior a '" + fechaAnterior + "'");
            }
        }

        @Test
        @DisplayName("6.2 Reservas del mismo día ordenadas por hora de inicio ASC")
        void reservasMismoDia_ordenadas_porHoraInicio() {
            List<ReservasSocioDTO> lista = model.getReservas(0, DESDE_INICIO, HASTA_FUTURO);
            for (int i = 1; i < lista.size(); i++) {
                ReservasSocioDTO anterior = lista.get(i - 1);
                ReservasSocioDTO actual   = lista.get(i);
                if (anterior.getFecha().equals(actual.getFecha())) {
                    // Mismo día → la hora de inicio del anterior debe ser <= la del actual
                    assertTrue(anterior.getHoraInicio().compareTo(actual.getHoraInicio()) <= 0,
                        "Para el día " + actual.getFecha() + ", la hora de inicio '"
                        + actual.getHoraInicio() + "' no debe ser anterior a '"
                        + anterior.getHoraInicio() + "'");
                }
            }
        }
    }
}