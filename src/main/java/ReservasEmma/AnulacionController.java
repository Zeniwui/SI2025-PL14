package ReservasEmma;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import si.pl14.model.ReservaEntity;
import si.pl14.util.SwingUtil;


public class AnulacionController {
    private AnulacionModel model;
    private AnulacionView view;
    private List<ReservaEntity> reservasActuales; // IMPORTANTE: Para saber qué objeto anular

    public AnulacionController(AnulacionModel m, AnulacionView v) {
        this.model = m;
        this.view = v;
        this.initController();
    }

    public void initController() {
        // Evento de búsqueda
        view.getBtnBuscar().addActionListener(e -> refrescarTabla());
        
        // Evento de anulación
        view.getBtnAnular().addActionListener(e -> 
            SwingUtil.exceptionWrapper(() -> anularSeleccionada())
        );
    }

    private void refrescarTabla() {
        String idSocioText = view.getTxtIdSocio().getText();
        if (idSocioText.isEmpty()) {
        	SwingUtil.showMessage("Introduzca un ID de socio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idSocio = Integer.parseInt(idSocioText);
        reservasActuales = model.getReservasFuturasSocio(idSocio);
        
        DefaultTableModel tm = view.getModeloTabla();
        tm.setRowCount(0);

        for (ReservaEntity r : reservasActuales) {
            tm.addRow(new Object[]{
                r.getIdReserva(),
                r.getIdInstalacion(),
                r.getFecha(),
                r.getHoraInicio() + ":00",
                r.getHoraFin() + ":00",
                r.getCosteReserva() + " €"
            });
        }
    }

    private void anularSeleccionada() {
        int fila = view.getTablaReservas().getSelectedRow();
        if (fila == -1) {
            SwingUtil.showMessage("Seleccione una reserva de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ReservaEntity reserva = reservasActuales.get(fila);
        
        String motivo = view.pedirMotivo();
        if (motivo == null || motivo.trim().isEmpty()) {
            SwingUtil.showMessage("Operación cancelada: El motivo es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (confirmarAnulacion(reserva)) {
            model.anularReserva(reserva.getIdReserva(), motivo);
            SwingUtil.showMessage("Reserva anulada correctamente. Se ha liberado el hueco y cancelado el cargo.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTabla();
        }
    }

    private boolean confirmarAnulacion(ReservaEntity r) {
        return JOptionPane.showConfirmDialog(view, 
            "¿Está seguro de que desea anular la reserva #" + r.getIdReserva() + "?",
            "Confirmar Anulación", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}