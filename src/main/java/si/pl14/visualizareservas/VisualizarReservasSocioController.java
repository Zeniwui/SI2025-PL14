package si.pl14.visualizareservas;

import si.pl14.util.ApplicationException;
import si.pl14.util.SwingUtil;
import si.pl14.visualizareservas.VisualizarReservasSocioView.InstalacionItem;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

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

    public void initController() {
        view.getBtnConfirmar().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::accionConfirmar));

        view.getTxtFechaInicio().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::accionConfirmar));
        view.getTxtFechaFin().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::accionConfirmar));

        view.getFrame().setVisible(true);
    }

    private void accionConfirmar() {
        String textoInicio = view.getTxtFechaInicio().getText().trim();
        String textoFin    = view.getTxtFechaFin().getText().trim();

        if (textoInicio.isEmpty())
            throw new ApplicationException("Debe indicar una fecha de inicio.");
        if (textoFin.isEmpty())
            throw new ApplicationException("Debe indicar una fecha de fin.");

        LocalDate fechaInicio;
        LocalDate fechaFin;

        try {
            fechaInicio = LocalDate.parse(textoInicio, FMT_ENTRADA);
        } catch (DateTimeParseException e) {
            throw new ApplicationException("La fecha de inicio no tiene el formato correcto (dd/MM/yyyy).");
        }
        try {
            fechaFin = LocalDate.parse(textoFin, FMT_ENTRADA);
        } catch (DateTimeParseException e) {
            throw new ApplicationException("La fecha de fin no tiene el formato correcto (dd/MM/yyyy).");
        }

        if (fechaInicio.isBefore(LocalDate.now().minusYears(1)))
            throw new ApplicationException("No se pueden consultar reservas de hace mas de un año.");
        
        if (fechaFin.isAfter(LocalDate.now().plusMonths(1)))
            throw new ApplicationException("No se pueden consultar reservas de mas de 1 mes en el futuro.");
        

        if (fechaFin.isBefore(fechaInicio))
            throw new ApplicationException("La fecha de fin no puede ser anterior a la fecha de inicio.");

        InstalacionItem item = view.getInstalacionSeleccionada();
        int    idInstalacion     = (item != null) ? item.getId()     : 0;
        String nombreInstalacion = (item != null) ? item.getNombre() : "Todas las instalaciones";

        List<ReservasSocioDTO> reservas = model.getReservas(
            idInstalacion,
            fechaInicio.format(FMT_ISO),
            fechaFin.format(FMT_ISO)
        );

        view.mostrarResultados(reservas, textoInicio, textoFin, nombreInstalacion);
    }
}