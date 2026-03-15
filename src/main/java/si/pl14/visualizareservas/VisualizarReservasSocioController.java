package si.pl14.visualizareservas;

import si.pl14.util.ApplicationException;
import si.pl14.util.SwingUtil;
import si.pl14.visualizareservas.VisualizarReservasSocioView.InstalacionItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Controlador para la historia de usuario
 * "Como socio quiero visualizar mis reservas durante un periodo de tiempo".
 *
 * Patron MVC del proyecto: instanciar con Model + View y llamar a initController().
 *
 * Criterios de aceptacion cubiertos:
 *   1. Se debe indicar una fecha de inicio y una fecha de fin.
 *   2. Se muestra una lista con todas las reservas del socio en ese periodo,
 *      con informacion de cada reserva.
 *   3. Las reservas aparecen ordenadas por fecha y hora.
 *   4. Se puede filtrar ademas por instalacion (opcional).
 */
public class VisualizarReservasSocioController {

    private static final DateTimeFormatter FMT_ENTRADA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_ISO     = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final VisualizarReservasSocioModel model;
    private final VisualizarReservasSocioView  view;

    public VisualizarReservasSocioController(VisualizarReservasSocioModel model,
                                              VisualizarReservasSocioView  view) {
        this.model = model;
        this.view  = view;
        view.setInstalaciones(model.getInstalaciones());
    }

    /** Registra los listeners y muestra la ventana. Llamar tras instanciar. */
    public void initController() {
        view.getBtnConfirmar().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::accionConfirmar));

        view.getBtnCerrar().addActionListener(
            e -> view.getFrame().dispose());

        // Tambien confirmar con Enter desde cualquier campo de fecha
        view.getTxtFechaInicio().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::accionConfirmar));
        view.getTxtFechaFin().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::accionConfirmar));

        view.getFrame().setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Accion principal
    // ─────────────────────────────────────────────────────────────────────────

    private void accionConfirmar() {
        // 1. Leer y validar entradas
        String textoInicio = view.getTxtFechaInicio().getText().trim();
        String textoFin    = view.getTxtFechaFin().getText().trim();

        if (textoInicio.isEmpty()) {
            throw new ApplicationException("Debe indicar una fecha de inicio.");
        }
        if (textoFin.isEmpty()) {
            throw new ApplicationException("Debe indicar una fecha de fin.");
        }

        LocalDate fechaInicio;
        LocalDate fechaFin;
        try {
            fechaInicio = LocalDate.parse(textoInicio, FMT_ENTRADA);
        } catch (DateTimeParseException e) {
            throw new ApplicationException(
                "La fecha de inicio no tiene el formato correcto (dd/MM/yyyy).");
        }
        try {
            fechaFin = LocalDate.parse(textoFin, FMT_ENTRADA);
        } catch (DateTimeParseException e) {
            throw new ApplicationException(
                "La fecha de fin no tiene el formato correcto (dd/MM/yyyy).");
        }

        if (fechaFin.isBefore(fechaInicio)) {
            throw new ApplicationException(
                "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        // 2. Instalacion seleccionada (0 = todas)
        InstalacionItem item = view.getInstalacionSeleccionada();
        int    idInstalacion     = (item != null) ? item.getId()     : 0;
        String nombreInstalacion = (item != null) ? item.getNombre() : "Todas las instalaciones";

        // 3. Consultar modelo
        List<Object[]> reservas = model.getReservas(
            idInstalacion,
            fechaInicio.format(FMT_ISO),
            fechaFin.format(FMT_ISO)
        );

        // 4. Actualizar vista
        view.mostrarResultados(reservas, textoInicio, textoFin, nombreInstalacion);
    }
}