package si.pl14.disponibilidad;

import si.pl14.disponibilidad.DisponibilidadView.InstalacionItem;
import si.pl14.util.SwingUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la pantalla de disponibilidad de instalaciones.
 * Patrón MVC del proyecto: instanciar con Model + View y llamar a initController().
 *
 * <pre>
 *   DisponibilidadController ctrl =
 *       new DisponibilidadController(new DisponibilidadModel(), new DisponibilidadView());
 *   ctrl.initController();
 * </pre>
 */
public class DisponibilidadController {

    private final DisponibilidadModel model;
    private final DisponibilidadView  view;

    /** Mes actualmente visible en el calendario (primer día del mes). */
    private LocalDate fechaBase;
    /** Día seleccionado por el usuario (null si ninguno). */
    private LocalDate fechaSeleccionada;

    // ─────────────────────────────────────────────────────────────────────────
    public DisponibilidadController(DisponibilidadModel m, DisponibilidadView v) {
        this.model = m;
        this.view  = v;
        initView();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Inicialización
    // ─────────────────────────────────────────────────────────────────────────

    /** Carga datos iniciales en la vista antes de hacerla visible. */
    private void initView() {
        view.setInstalaciones(model.getInstalaciones());
    }

    /**
     * Instala los manejadores de eventos en los componentes de la vista.
     * Llamar justo después del constructor.
     */
    public void initController() {
        // Botón Comprobar
        view.getBtnComprobar().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::accionComprobar));

        // Botón Cerrar
        view.getBtnCerrar().addActionListener(
            e -> view.getFrame().dispose());

        // Los botones de navegación de mes se reconstruyen cada vez que se dibuja
        // el calendario, así que los handlers se instalan en mostrarCalendario().

        // Hace visible la ventana
        view.getFrame().setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Acciones del controlador
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Acción del botón "Comprobar": valida la selección y muestra el calendario
     * desde el mes actual.
     */
    private void accionComprobar() {
        InstalacionItem item = view.getInstalacionSeleccionada();
        if (item == null) {
            throw new si.pl14.util.ApplicationException("Por favor, seleccione una instalación.");
        }
        fechaBase         = LocalDate.now().withDayOfMonth(1);
        fechaSeleccionada = null;
        view.ocultarHorario();
        mostrarCalendario();
    }

    /**
     * Construye y muestra el calendario del mes indicado por {@code fechaBase}.
     * Instala también los handlers de navegación de mes (se recronstruyen
     * en cada llamada junto con los botones).
     */
    private void mostrarCalendario() {
        LocalDate hoy    = LocalDate.now();
        LocalDate limite = hoy.plusDays(30);

        view.mostrarCalendario(fechaBase, hoy, limite, fechaSeleccionada,
            // Callback que se ejecuta cuando el usuario pulsa un día
            fecha -> SwingUtil.exceptionWrapper(() -> accionSeleccionarDia(fecha))
        );

        // Instala handlers de los botones de navegación de mes
        // (se recrean en cada mostrarCalendario, por eso se registran aquí)
        view.getBtnAnteriorMes().addActionListener(
            e -> SwingUtil.exceptionWrapper(() -> {
                fechaBase = fechaBase.minusMonths(1);
                mostrarCalendario();
            }));
        view.getBtnSiguienteMes().addActionListener(
            e -> SwingUtil.exceptionWrapper(() -> {
                fechaBase = fechaBase.plusMonths(1);
                mostrarCalendario();
            }));
    }

    /**
     * Acción al pulsar un día en el calendario: guarda la selección,
     * repinta el calendario (para resaltar la fecha) y carga el horario.
     */
    private void accionSeleccionarDia(LocalDate fecha) {
        this.fechaSeleccionada = fecha;
        mostrarCalendario();   // repinta con la nueva selección
        cargarHorario(fecha);
    }

    /**
     * Consulta el modelo y actualiza el horario en la vista.
     */
    private void cargarHorario(LocalDate fecha) {
        InstalacionItem item = view.getInstalacionSeleccionada();
        if (item == null) return;

        // SQLite guarda la fecha como texto "yyyy-MM-dd" según squema-pl14.sql
        String fechaIso = fecha.toString();   // LocalDate.toString() ya produce "yyyy-MM-dd"

        Map<Integer, List<String>> ocupaciones =
            model.getOcupacionesPorHora(item.getId(), fechaIso);

        view.mostrarHorario(fecha, item.getNombre(), ocupaciones);
    }
}