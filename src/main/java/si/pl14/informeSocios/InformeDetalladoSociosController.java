package si.pl14.informeSocios;

import si.pl14.util.ApplicationException;
import si.pl14.util.SwingUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

/**
 * Controlador MVC para la HU "Generar Informe Detallado de Socios".
 *
 * Flujo:
 *  1. initController() registra listeners y hace visible la ventana.
 *  2. Botón "Confirmar" → valida fechas, consulta el Model y muestra resultados.
 *     Validaciones:
 *       · Ambos campos deben estar rellenos y con formato dd/MM/yyyy.
 *       · La fecha de inicio no puede ser posterior a hoy.
 *       · La fecha de fin no puede ser posterior a hoy.
 *       · La fecha de fin no puede ser anterior a la de inicio.
 *  3. ComboBox "Ordenar por" → reordena la lista en memoria y refresca la tabla.
 *  4. Botón "Guardar Informe" → exporta a CSV mediante la Vista.
 */
public class InformeDetalladoSociosController {

    private static final DateTimeFormatter FMT_ENTRADA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_ISO     = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final InformeDetalladoSociosModel model;
    private final InformeDetalladoSociosView  view;

    private List<InformeDetalladoSocioDTO> datosActuales;

    public InformeDetalladoSociosController(InformeDetalladoSociosModel model,
                                             InformeDetalladoSociosView view) {
        this.model = model;
        this.view  = view;
    }

    public void initController() {
        view.getBtnConfirmar()  .addActionListener(e -> SwingUtil.exceptionWrapper(this::accionConfirmar));
        view.getBtnGuardar()    .addActionListener(e -> SwingUtil.exceptionWrapper(this::accionGuardar));
        view.getCbFiltrar()     .addActionListener(e -> SwingUtil.exceptionWrapper(this::accionFiltrar));
        view.setVisible(true);
    }

    private void accionConfirmar() {
        String textoInicio = view.getTxtFechaDesde().getText().trim();
        String textoFin    = view.getTxtFechaHasta().getText().trim();

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

        // ── Validación: fechas no pueden ser futuras ──────────────────────
        LocalDate hoy = LocalDate.now();

        if (fechaInicio.isAfter(hoy))
            throw new ApplicationException(
                "La fecha de inicio no puede ser futura (" + textoInicio + ").\n" +
                "No es posible generar informes de periodos que aún no han transcurrido."
            );

        if (fechaFin.isAfter(hoy))
            throw new ApplicationException(
                "La fecha de fin no puede ser posterior a hoy (" + hoy.format(FMT_ENTRADA) + ").\n" +
                "No es posible informar de reservas o actividades que aún no han ocurrido."
            );
        // ──────────────────────────────────────────────────────────────────

        if (fechaFin.isBefore(fechaInicio))
            throw new ApplicationException("La fecha de fin no puede ser anterior a la de inicio.");

        datosActuales = model.getInforme(fechaInicio.format(FMT_ISO), fechaFin.format(FMT_ISO));
        aplicarOrden(datosActuales);
        view.mostrarResultados(datosActuales, textoInicio, textoFin);
    }

    private void accionFiltrar() {
        if (datosActuales == null || datosActuales.isEmpty()) return;
        aplicarOrden(datosActuales);
        view.refrescarTabla(datosActuales);
    }

    private void accionGuardar() {
        if (datosActuales == null || datosActuales.isEmpty())
            throw new ApplicationException("No hay datos para guardar. Genere el informe primero.");
        view.guardarInforme(datosActuales);
    }

    private void aplicarOrden(List<InformeDetalladoSocioDTO> datos) {
        String filtro = (String) view.getCbFiltrar().getSelectedItem();
        if (filtro == null) return;

        switch (filtro) {
            case "Deuda (mayor a menor)":
                datos.sort(Comparator.comparingDouble(InformeDetalladoSocioDTO::getDeuda).reversed());
                break;
            case "Reservas (mayor a menor)":
                datos.sort(Comparator.comparingInt(InformeDetalladoSocioDTO::getNumReservas).reversed());
                break;
            case "Actividades (mayor a menor)":
                datos.sort(Comparator.comparingInt(InformeDetalladoSocioDTO::getNumActividades).reversed());
                break;
            case "Nombre (A-Z)":
                datos.sort(Comparator.comparing(InformeDetalladoSocioDTO::getNombreSocio));
                break;
            default:
                break;
        }
    }
}