package si.pl14.actividadesEmma;

import java.util.List;
import javax.swing.JOptionPane;

public class CancelacionActividadController {
    private CancelacionActividadModel model;
    private CancelacionActividadView view;
    private List<ActividadResumenDTO> listaActual;

    public CancelacionActividadController(CancelacionActividadModel m, CancelacionActividadView v) {
        this.model = m;
        this.view = v;
        initController();
    }

    private void initController() {
        view.getBtnMostrar().addActionListener(e -> cargarActividades());
        
        view.getBtnCancelar().addActionListener(e -> procesarCancelacion());

        view.getTablaActividades().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = view.getTablaActividades().getSelectedRow();
                if (fila != -1) {
                    String nombre = view.getModelo().getValueAt(fila, 0).toString();
                    view.getLblSeleccionada().setText("Actividad seleccionada: " + nombre);
                }
            }
        });
    }

    private void cargarActividades() {
        try {
            int min = Integer.parseInt(view.getTxtMinimo().getText());
            listaActual = model.getActividadesBajoMinimo(min);
            
            view.getModelo().setRowCount(0);
            for (ActividadResumenDTO a : listaActual) {
                view.getModelo().addRow(new Object[]{
                    a.getNombre(), a.getInscritos(), a.getPlazas(), a.getInstalacion(), a.getIdActividad()
                });
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Introduce un número válido.");
        }
    }

    private void procesarCancelacion() {
        int fila = view.getTablaActividades().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Selecciona una actividad de la lista.");
            return;
        }

        int idAct = (int) view.getModelo().getValueAt(fila, 4);
        String nombreAct = view.getModelo().getValueAt(fila, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(view, 
            "¿Confirmar cancelación de '" + nombreAct + "'?\nSe liberarán las instalaciones y se tramitarán devoluciones.",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            List<String> emails = model.getEmailsInscritos(idAct);
            model.cancelarActividadCompleta(idAct);

            // simulado porque no podemos mandar emails de verdad
            System.out.println("--- NOTIFICACIONES ENVIADAS ---");
            for (String email : emails) {
                System.out.println("Aviso enviado a: " + email + " -> Motivo: Actividad " + nombreAct + " cancelada por falta de aforo.");
            }

            JOptionPane.showMessageDialog(view, "Actividad cancelada con éxito.");
            cargarActividades(); // Refrescar
            view.getLblSeleccionada().setText("Actividad seleccionada: Ninguna");
        }
    }
}
