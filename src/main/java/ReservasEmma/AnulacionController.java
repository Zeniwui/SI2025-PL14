package ReservasEmma;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AnulacionController {
	private AnulacionModel model;
	private AnulacionView view;
	private List<AnulacionModel.ReservaDetalleDTO> reservasActuales;

	public AnulacionController(AnulacionModel m, AnulacionView v) {
		this.model = m;
		this.view = v;
		initController();
	}

	private void initController() {
		view.getBtnBuscarNombre().addActionListener(e -> buscar(1));
		view.getBtnBuscarDni().addActionListener(e -> buscar(2));
		view.getBtnBuscarId().addActionListener(e -> buscar(3));
		view.getBtnAnular().addActionListener(e -> ejecutarAnulacion());
		view.getBtnLimpiar().addActionListener(e -> limpiarTabla());

		view.getTablaReservas().getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int fila = view.getTablaReservas().getSelectedRow();
				if (fila != -1) {
					Object id = view.getTablaReservas().getValueAt(fila, 0);
					view.getLblReservaSeleccionada().setText("Reserva seleccionada: " + id);
				}
			}
		});
	}
	
	private void limpiarTabla() {
	    DefaultTableModel modelo = view.getModeloTabla();
	    modelo.setRowCount(0);
	    view.getLblReservaSeleccionada().setText("Reserva seleccionada: Ninguna");
	    view.getTxtMotivo().setText("");
	    if (reservasActuales != null) {
	        reservasActuales.clear();
	    }
	    System.out.println("Tabla y datos temporales limpiados.");
	}

	private void buscar(int tipo) {
		view.getModeloTabla().setRowCount(0);
		try {
			if (tipo == 1) {
				reservasActuales = model.getReservasPorNombre(view.getTxtNombre().getText().trim());
			} else if (tipo == 2) {
				reservasActuales = model.getReservasPorDni(view.getTxtDni().getText().trim());
			} else {
				int id = Integer.parseInt(view.getTxtId().getText().trim());
				reservasActuales = model.getReservasPorId(id);
			}

			System.out.println("Búsqueda realizada. Registros encontrados: " + reservasActuales.size());

			if (reservasActuales.isEmpty()) {
				JOptionPane.showMessageDialog(view, "No se encontraron reservas futuras.");
			} else {
				llenarTabla();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
		}
	}

	private void llenarTabla() {
		DefaultTableModel modelo = view.getModeloTabla();
		for (AnulacionModel.ReservaDetalleDTO r : reservasActuales) {
			modelo.addRow(new Object[] { r.getIdReserva(), r.getInstalacion(), r.getFecha(), r.getHoraInicio(),
					r.getHoraFin(), r.getCosteReserva(), r.getIdSocio(), r.getDniSocio(), r.getNombreSocio() });
		}
	}

	private void ejecutarAnulacion() {
		int fila = view.getTablaReservas().getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(view, "Por favor, seleccione una reserva de la tabla.");
			return;
		}

		String motivo = view.getTxtMotivo().getText().trim();
		if (motivo.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Debe escribir el motivo de la cancelación.");
			return;
		}

		int idReserva = (int) view.getTablaReservas().getValueAt(fila, 0);
		String nombreSocio = (String) view.getTablaReservas().getValueAt(fila, 8);

		int confirm = JOptionPane.showConfirmDialog(view, "¿Confirmar anulación de la reserva #" + idReserva + "?",
				"Confirmación", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			model.anularReserva(idReserva);

			// Como no tenemos whatsapp de los socios porque son datos inventados se imprime
			// en consola el mensaje, pero sino se enviaria por correo electronico o
			// whatsapp
			System.out.println("NOTIFICACIÓN: Enviado mensaje a " + nombreSocio + ". Motivo: " + motivo);

			JOptionPane.showMessageDialog(view, "Reserva anulada. Se ha liberado el hueco y eliminado el cargo.");

			// Limpiar interfaz
			view.getTxtMotivo().setText("");
			buscarReciente();
		}
	}

	private void buscarReciente() {
		if (!view.getTxtId().getText().isEmpty())
			buscar(3);
		else if (!view.getTxtDni().getText().isEmpty())
			buscar(2);
		else
			buscar(1);
	}
}
