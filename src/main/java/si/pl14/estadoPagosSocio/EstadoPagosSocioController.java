package si.pl14.estadoPagosSocio;

import si.pl14.util.ApplicationException;
import si.pl14.util.SwingUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Controlador del caso de uso "Visualizar Estado de Pagos del Socio".
 * Coordina la vista ({@link EstadoPagosSocioView}) y el modelo
 * ({@link EstadoPagosSocioModel}): valida la entrada del usuario,
 * solicita los datos al modelo y ordena a la vista que los muestre.
 */
public class EstadoPagosSocioController {

    private static final DateTimeFormatter FMT_ENTRADA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_ISO     = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final EstadoPagosSocioModel model;
    private final EstadoPagosSocioView  view;

    public EstadoPagosSocioController(EstadoPagosSocioModel model) {
        this.model = model;
        this.view  = new EstadoPagosSocioView();
    }

    /** Registra los listeners y abre la ventana. */
    public void initController() {
        view.getBtnConfirmar()  .addActionListener(e -> SwingUtil.exceptionWrapper(this::accionConfirmar));
        view.getBtnFechaInicio().addActionListener(e -> SwingUtil.exceptionWrapper(this::accionConfirmar));
        view.getBtnFechaFin()   .addActionListener(e -> SwingUtil.exceptionWrapper(this::accionConfirmar));
        SwingUtil.exceptionWrapper(this::accionConfirmar);
        view.setVisible(true);
    }

    // ── Acciones ──────────────────────────────────────────────────────────

    private void accionConfirmar() {
        String textoInicio = view.getBtnFechaInicio().getText().trim();
        String textoFin    = view.getBtnFechaFin()   .getText().trim();

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

        if (fechaFin.isBefore(fechaInicio))
            throw new ApplicationException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        if (fechaInicio.isBefore(LocalDate.now().minusYears(1)))
            throw new ApplicationException("No se pueden consultar pagos de hace mas de un año.");
        if (fechaFin.isAfter(LocalDate.now().plusMonths(1)))
            throw new ApplicationException("No se pueden consultar pagos de mas de 1 mes en el futuro.");
        if (fechaInicio.isEqual(fechaFin))
            throw new ApplicationException("La fecha de inicio y la fecha de fin no pueden ser iguales.");

        String isoInicio = fechaInicio.format(FMT_ISO);
        String isoFin    = fechaFin.format(FMT_ISO);

        List<Object[]> cargos         = model.getCargos(isoInicio, isoFin);
        double         totalPendiente = model.getTotalPendiente(isoInicio, isoFin);

        view.mostrarResultados(cargos, textoInicio, textoFin, totalPendiente);
    }
}