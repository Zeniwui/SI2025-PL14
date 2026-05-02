package si.pl14.actividadesEmma;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance;
import si.pl14.model.ActividadEntity;
import si.pl14.util.ApplicationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase bajo prueba: Planificar_Actividad_Model
 *
 * NOTA: uso @BeforeAll para que la BD se inicialice UNA SOLA VEZ, y el mismo
 * objeto model se reutiliza en todos ellos
 */
@DisplayName("Proceso de negocio: Planificar Actividad Admin")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // esto es necesario para que @BeforeAll no sea static
class Planificar_Actividad_ModelTest {

	// compartida por todos los tests
	private Planificar_Actividad_Model model;

	// Fechas calculadas una sola vez
	private static final String FECHA_INICIO_VALIDA = LocalDate.now().plusDays(1).toString();
	private static final String FECHA_FIN_VALIDA = LocalDate.now().plusDays(30).toString();


	/** Construye una ActividadEntity con todos los campos válidos. */
	private ActividadEntity actividadValida() {
		ActividadEntity a = new ActividadEntity();
		a.setNombre("Yoga Matinal");
		a.setDescripcion("Sesión de yoga para todos los niveles");
		a.setAforo(20);
		a.setPrecioSocio(10.0);
		a.setPrecioNoSocio(20.0);
		a.setFechaInicio(FECHA_INICIO_VALIDA);
		a.setFechaFin(FECHA_FIN_VALIDA);
		a.setIdInstalacion(1);
		a.setIdPeriodo(1);
		return a;
	}

	// -------------------------------------------------------------------------
	// Setup
	// -------------------------------------------------------------------------

	/**
	 * El constructor de Planificar_Actividad_Model ya inicializa la BD, así que no
	 * hay que repetirlo
	 */
	@BeforeAll
	void setUp() {
		model = new Planificar_Actividad_Model();
	}

	// =========================================================================
	// 1. NOMBRE
	// =========================================================================

	@Nested
	@DisplayName("1. Validación del nombre")
	class ValidacionNombre {

		@Test
		@DisplayName("1.1 Nombre válido --> no lanza excepción")
		void nombreValido_noLanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setNombre("Pilates Avanzado");
			assertDoesNotThrow(() -> model.validarTodo(a));
		}

		@Test
		@DisplayName("1.2 Nombre nulo --> lanza ApplicationException")
		void nombreNulo_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setNombre(null);
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("nombre"), "El mensaje debe indicar el problema con el nombre");
		}

		@Test
		@DisplayName("1.3 Nombre vacío (solo espacios) --> lanza ApplicationException")
		void nombreVacio_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setNombre("   ");
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("nombre"));
		}
	}

	// =========================================================================
	// 2. AFORO
	// =========================================================================

	@Nested
	@DisplayName("2. Validación del aforo")
	class ValidacionAforo {

		@Test
		@DisplayName("2.1 Aforo positivo (=1) --> frontera mínima válida")
		void aforoMinimo_valido() {
			ActividadEntity a = actividadValida();
			a.setAforo(1);
			assertDoesNotThrow(() -> model.validarTodo(a));
		}

		@Test
		@DisplayName("2.2 Aforo cero --> lanza ApplicationException")
		void aforoCero_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setAforo(0);
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("aforo"));
		}

		@Test
		@DisplayName("2.3 Aforo negativo --> lanza ApplicationException")
		void aforoNegativo_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setAforo(-5);
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("aforo"));
		}
	}

	// =========================================================================
	// 3. PRECIOS
	// =========================================================================

	@Nested
	@DisplayName("3. Validación de precios")
	class ValidacionPrecios {

		@Test
		@DisplayName("3.1 Precio socio = 0 (gratuito) --> válido")
		void precioSocioGratuito_valido() {
			ActividadEntity a = actividadValida();
			a.setPrecioSocio(0.0);
			a.setPrecioNoSocio(5.0);
			assertDoesNotThrow(() -> model.validarTodo(a));
		}

		@Test
		@DisplayName("3.2 Precio socio negativo --> lanza ApplicationException")
		void precioSocioNegativo_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setPrecioSocio(-1.0);
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("socio"));
		}

		@Test
		@DisplayName("3.3 Precio no-socio < precio socio --> lanza ApplicationException")
		void precioNoSocioMenorQueSocio_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setPrecioSocio(20.0);
			a.setPrecioNoSocio(10.0); // no socio < socio --> inválido
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("no socios"));
		}

		@Test
		@DisplayName("3.4 Precio no-socio igual a precio socio --> válido (frontera)")
		void precioNoSocioIgualASocio_valido() {
			ActividadEntity a = actividadValida();
			a.setPrecioSocio(15.0);
			a.setPrecioNoSocio(15.0); // igual --> válido según la regla >=
			assertDoesNotThrow(() -> model.validarTodo(a));
		}
	}

	// =========================================================================
	// 4. FECHAS
	// =========================================================================

	@Nested
	@DisplayName("4. Validación de fechas")
	class ValidacionFechas {

		@Test
		@DisplayName("4.1 Fecha inicio mañana, fin en un mes --> válido")
		void fechasValidas() {
			ActividadEntity a = actividadValida();
			assertDoesNotThrow(() -> model.validarTodo(a));
		}

		@Test
		@DisplayName("4.2 Fecha inicio en el pasado --> lanza ApplicationException")
		void fechaInicioEnPasado_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setFechaInicio(LocalDate.now().minusDays(1).toString());
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("inicio"));
		}

		@Test
		@DisplayName("4.3 Fecha inicio igual a hoy --> lanza ApplicationException")
		void fechaInicioHoy_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setFechaInicio(LocalDate.now().toString()); // hoy no está permitido
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("inicio"));
		}

		@Test
		@DisplayName("4.4 Fecha fin anterior a fecha inicio --> lanza ApplicationException")
		void fechaFinAnteriorAInicio_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setFechaInicio(LocalDate.now().plusDays(10).toString());
			a.setFechaFin(LocalDate.now().plusDays(5).toString()); // fin < inicio
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("fin"));
		}

		@Test
		@DisplayName("4.5 Fecha fin igual a fecha inicio --> lanza ApplicationException")
		void fechaFinIgualAInicio_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			String fecha = LocalDate.now().plusDays(5).toString();
			a.setFechaInicio(fecha);
			a.setFechaFin(fecha); // fin = inicio --> no es posterior
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("fin"));
		}

		@Test
		@DisplayName("4.6 Formato de fecha inválido --> lanza ApplicationException")
		void formatoFechaInvalido_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setFechaInicio("01-13-2026"); // formato incorrecto (DD-MM-YYYY)
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("Formato") || ex.getMessage().contains("formato"));
		}

		@Test
		@DisplayName("4.7 Fecha de inicio nula --> lanza ApplicationException")
		void fechaInicioNula_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setFechaInicio(null);
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("inicio"));
		}
	}

	// =========================================================================
	// 5. INSTALACIÓN Y PERIODO
	// =========================================================================

	@Nested
	@DisplayName("5. Validación de instalación y periodo")
	class ValidacionInstalacionPeriodo {

		@Test
		@DisplayName("5.1 ID instalación = 0 --> lanza ApplicationException")
		void idInstalacionCero_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setIdInstalacion(0);
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("instalaci"));
		}

		@Test
		@DisplayName("5.2 ID instalación negativo --> lanza ApplicationException")
		void idInstalacionNegativo_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setIdInstalacion(-1);
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("instalaci"));
		}

		@Test
		@DisplayName("5.3 ID periodo = 0 --> lanza ApplicationException")
		void idPeriodoCero_lanzaExcepcion() {
			ActividadEntity a = actividadValida();
			a.setIdPeriodo(0);
			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));
			assertTrue(ex.getMessage().contains("periodo"));
		}
	}

	// =========================================================================
	// 6. MÚLTIPLES ERRORES ACUMULADOS
	// =========================================================================

	@Nested
	@DisplayName("6. Acumulación de errores múltiples")
	class AcumulacionErrores {

		@Test
		@DisplayName("6.1 Varios campos inválidos --> todos los errores aparecen en el mensaje")
		void variosErrores_mensajeAcumulado() {
			ActividadEntity a = new ActividadEntity();
			a.setNombre(""); // error 1
			a.setAforo(-1); // error 2
			a.setPrecioSocio(-5.0); // error 3
			a.setPrecioNoSocio(-10.0); // error 4
			a.setFechaInicio(null); // error 5
			a.setFechaFin(null); // error 6
			a.setIdInstalacion(0); // error 7
			a.setIdPeriodo(0); // error 8

			ApplicationException ex = assertThrows(ApplicationException.class, () -> model.validarTodo(a));

			// El mensaje debe contener referencias a múltiples problemas
			String msg = ex.getMessage();
			assertTrue(msg.contains("nombre"), "Debe mencionar error de nombre");
			assertTrue(msg.contains("aforo"), "Debe mencionar error de aforo");
			assertTrue(msg.contains("socio"), "Debe mencionar error de precio socio");
			assertTrue(msg.contains("instalaci"), "Debe mencionar error de instalación");
			assertTrue(msg.contains("periodo"), "Debe mencionar error de periodo");
		}
	}
}
