package si.pl14.actividadesEmma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

            System.out.println("--- NOTIFICACIONES ENVIADAS ---");
            String motivo = "Actividad '" + nombreAct + "' cancelada por falta de aforo.";
            
            for (String email : emails) {
                System.out.println("Aviso enviado a: " + email + " -> Motivo: " + motivo);
                guardarNotificacionTxt(email, motivo);
            }

            JOptionPane.showMessageDialog(view, "Actividad cancelada con éxito.\nSe ha devuelto el dinero a los inscritos y liberado la instalación.");
            cargarActividades(); 
            view.getLblSeleccionada().setText("Actividad seleccionada: Ninguna");
        }
    }

    private void guardarNotificacionTxt(String email, String motivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("notificaciones_actividades.txt", true))) {
            String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String mensaje = "[" + fechaHora + "] AVISO A: " + email + " | MOTIVO: " + motivo;
            
            writer.write(mensaje);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo txt: " + e.getMessage());
        }
    }
}
