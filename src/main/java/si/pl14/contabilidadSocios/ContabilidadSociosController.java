package si.pl14.contabilidadSocios;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controlador MVC para la HU "Calcular contabilidad de socios en un mes".
 *
 * Coordina la Vista y el Modelo:
 *  1. Al pulsar "Calcular"  → pide al Model los datos y los envía a la Vista.
 *  2. Al pulsar "Guardar"   → pide al Model que escriba el fichero y notifica a la Vista.
 *
 * El directorio de salida por defecto es el directorio de trabajo del proceso (raíz del proyecto).
 */
public class ContabilidadSociosController {

    private final ContabilidadSociosModel model;
    private final ContabilidadSociosView  view;

    /** Lista resultado del último cálculo; se reutiliza al guardar. */
    private List<ContabilidadSocioDTO> ultimaLista = null;
    private int ultimoMes  = 0;
    private int ultimoAnio = 0;

    // ─────────────────────────────────────────────────────────────────────────

    public ContabilidadSociosController(ContabilidadSociosModel model,
                                        ContabilidadSociosView  view) {
        this.model = model;
        this.view  = view;
        initController();
    }

    /** Registra los listeners y hace visible la ventana. */
    public void initController() {
        view.getBtnCalcular().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCalcular();
            }
        });

        view.getBtnGuardar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onGuardar();
            }
        });

        view.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Acciones
    // ─────────────────────────────────────────────────────────────────────────

    private void onCalcular() {
        int mes  = view.getMesSeleccionado();
        int anio = view.getAnioSeleccionado();

        try {
            ultimaLista = model.calcularContabilidad(mes, anio);
            ultimoMes   = mes;
            ultimoAnio  = anio;
            view.mostrarResultados(ultimaLista, mes, anio);
        } catch (Exception ex) {
            view.mostrarError("Error al calcular la contabilidad:\n" + ex.getMessage());
        }
    }

    private void onGuardar() {
        if (ultimaLista == null || ultimaLista.isEmpty()) {
            view.mostrarError("No hay datos que guardar. Ejecute primero el cálculo.");
            return;
        }

        // Elegir directorio mediante JFileChooser
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar carpeta de destino");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int resultado = chooser.showSaveDialog(view);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        String directorio = chooser.getSelectedFile().getAbsolutePath();

        try {
            String ruta = model.guardarFichero(ultimaLista, ultimoMes, ultimoAnio, directorio);
            view.mostrarMensajeFichero(ruta);
        } catch (Exception ex) {
            view.mostrarError("Error al guardar el fichero:\n" + ex.getMessage());
        }
    }
}