package si.pl14.contabilidadSocios;

import si.pl14.util.ApplicationException;

import javax.swing.JFileChooser;
import java.util.List;

/**
 * Controlador MVC para la HU "Calcular contabilidad de socios en un mes".
 *
 * Flujo:
 *  1. initController() registra los listeners y hace visible la ventana.
 *  2. Botón "Calcular"  → llama al Model y pasa resultados a la Vista.
 *     Si el Model lanza ApplicationException por fecha futura o demasiado antigua,
 *     el Controller resalta los selectores en rojo y muestra un mensaje preventivo.
 *  3. Botón "Guardar"   → abre JFileChooser de directorio y delega en el Model.
 */
public class ContabilidadSociosController {

    private final ContabilidadSociosModel model;
    private final ContabilidadSociosView  view;

    private List<ContabilidadSocioDTO> ultimaLista = null;
    private int ultimoMes  = 0;
    private int ultimoAnio = 0;

    public ContabilidadSociosController(ContabilidadSociosModel model,
                                        ContabilidadSociosView  view) {
        this.model = model;
        this.view  = view;
    }

    /** Registra listeners y muestra la ventana. */
    public void initController() {
        view.getBtnCalcular().addActionListener(e -> onCalcular());
        view.getBtnGuardar() .addActionListener(e -> onGuardar());
        view.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void onCalcular() {
        int mes  = view.getMesSeleccionado();
        int anio = view.getAnioSeleccionado();

        try {
            // Resetear posibles marcas de error anteriores antes de recalcular
            view.resetearFecha();

            ultimaLista = model.calcularContabilidad(mes, anio);
            ultimoMes   = mes;
            ultimoAnio  = anio;
            view.mostrarResultados(ultimaLista, mes, anio);

        } catch (ApplicationException ex) {
            // Error de validación de rango de fechas: resaltar selectores en rojo
            // y mostrar mensaje claro al usuario sin stacktrace técnico.
            view.marcarFechaInvalida();
            view.mostrarError(
                "Fecha no válida\n\n" + ex.getMessage() +
                "\n\nPor favor, seleccione un mes comprendido entre\n" +
                "el mes actual y hace un año."
            );

        } catch (Exception ex) {
            // Error inesperado: resetear marcas y mostrar mensaje genérico.
            view.resetearFecha();
            view.mostrarError("Error al calcular la contabilidad:\n" + ex.getMessage());
        }
    }

    private void onGuardar() {
        if (ultimaLista == null || ultimaLista.isEmpty()) {
            view.mostrarError("No hay datos que guardar. Ejecute primero el cálculo.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar carpeta de destino");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) return;

        String dir = chooser.getSelectedFile().getAbsolutePath();
        try {
            String ruta = model.guardarFichero(ultimaLista, ultimoMes, ultimoAnio, dir);
            view.mostrarMensajeFichero(ruta);
        } catch (Exception ex) {
            view.mostrarError("Error al guardar el fichero:\n" + ex.getMessage());
        }
    }
}