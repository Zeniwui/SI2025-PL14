package si.pl14.disponibilidad;

import si.pl14.disponibilidad.DisponibilidadView.InstalacionItem;
import si.pl14.util.SwingUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la pantalla de disponibilidad de instalaciones.
 * Patron MVC del proyecto: instanciar con Model + View y llamar a initController().
 */
public class DisponibilidadController {

    private final DisponibilidadModel model;
    private final DisponibilidadView  view;

    private LocalDate fechaBase;
    private LocalDate fechaSeleccionada;

    public DisponibilidadController(DisponibilidadModel m, DisponibilidadView v) {
        this.model = m;
        this.view  = v;
        initView();
    }

    private void initView() {
        view.setInstalaciones(model.getInstalaciones());
    }

    public void initController() {
        view.getBtnComprobar().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::accionComprobar));

        view.getBtnCerrar().addActionListener(
            e -> view.getFrame().dispose());

        view.getFrame().setVisible(true);
    }

    private void accionComprobar() {
        InstalacionItem item = view.getInstalacionSeleccionada();
        if (item == null) {
            throw new si.pl14.util.ApplicationException("Por favor, seleccione una instalacion.");
        }
        fechaBase         = LocalDate.now().withDayOfMonth(1);
        fechaSeleccionada = null;
        view.ocultarHorario();
        mostrarCalendario();
    }

    private void mostrarCalendario() {
        LocalDate hoy    = LocalDate.now();
        LocalDate limite = hoy.plusDays(30);

        view.mostrarCalendario(fechaBase, hoy, limite, fechaSeleccionada,
            fecha -> SwingUtil.exceptionWrapper(() -> accionSeleccionarDia(fecha)));

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

    private void accionSeleccionarDia(LocalDate fecha) {
    	//Añadida la restriccion que se pedia en RedKanban de no poder ver dentro de más  de 30 días
    	LocalDate limite = LocalDate.now().plusDays(30);
    	/* Me he dado cuenta que es código inutil pero lo dejo por si acaso
    	 * if (fecha.isAfter(limite)) {
    	    throw new si.pl14.util.ApplicationException(
    	        "No se puede consultar la disponibilidad de las instalaciones con más de 30 días de antelación.");
    	}
    	*/
        this.fechaSeleccionada = fecha;
        mostrarCalendario();
        cargarHorario(fecha);
    }

    /**
     * Consulta el modelo y actualiza AMBAS pestanas:
     *  - "Disponibilidad" con todas las ocupaciones
     *  - "Mis Reservas"   con solo las reservas del socio actual
     */
    private void cargarHorario(LocalDate fecha) {
        InstalacionItem item = view.getInstalacionSeleccionada();
        if (item == null) return;

        String fechaIso = fecha.toString();   // "yyyy-MM-dd"

        // Pestana Disponibilidad
        Map<Integer, List<String>> ocupaciones =
            model.getOcupacionesPorHora(item.getId(), fechaIso);
        view.mostrarHorario(fecha, item.getNombre(), ocupaciones);

        // Pestana Mis Reservas
        List<Object[]> misReservas =
            model.getMisReservasDelDia(item.getId(), fechaIso);
        view.mostrarMisReservas(fecha, item.getNombre(), misReservas);
    }
}