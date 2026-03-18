package si.pl14.reservasEmma;

import javax.swing.*;
import si.pl14.util.Database;
import si.pl14.util.DatabaseViewer;

import java.awt.EventQueue;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reserva_Instalacion_Admin_Controller {

	private Reserva_Instalacion_Admin_View view;
	private Reserva_Instalacion_Admin_Model model;

	public Reserva_Instalacion_Admin_Controller(Reserva_Instalacion_Admin_View view,
			Reserva_Instalacion_Admin_Model model) {
		this.view = view;
		this.model = model;
		initController();
	}

	private void initController() {
		for (String a : model.getActividades()) {
			view.getCbActividades().addItem(a);
		}

		view.getCbActividades().addActionListener(e -> cargarDatosActividad());
		view.getBtnCambiarInstalacion().addActionListener(e -> cambiarInstalacion());
		view.getBtnVerConflictos().addActionListener(e -> mostrarConflictos());
		view.getBtnCambiarHorario().addActionListener(e -> {
			String[] dias = { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO" };
			String nuevoDia = (String) JOptionPane.showInputDialog(view, "Nuevo día:", "Cambiar Horario",
					JOptionPane.QUESTION_MESSAGE, null, dias, dias[0]);
			String nuevaIni = JOptionPane.showInputDialog(view, "Nueva Hora Inicio (HH:mm):", "10:00");
			String nuevaFin = JOptionPane.showInputDialog(view, "Nueva Hora Fin (HH:mm):", "11:30");

			if (nuevoDia != null && nuevaIni != null && nuevaFin != null) {
				int idAct = getSelectedIdActividad();
				if (model.actualizarHorarioActividad(idAct, nuevoDia, nuevaIni, nuevaFin)) {
					JOptionPane.showMessageDialog(view,
							"Horario base actualizado. Pulsa 'Ver Conflictos' para verificar el nuevo escenario.");
					cargarDatosActividad();
				}
			}
		});

		// Carga inicial
		if (view.getCbActividades().getItemCount() > 0)
			cargarDatosActividad();
	}

	private void cargarDatosActividad() {
		int idAct = getSelectedIdActividad();
		String[] datos = model.getDatosActividad(idAct);

		if (datos[0] != null) {
			// Actualizar labels de la nueva vista
			view.getLblInstalacionActual().setText(datos[0]);

			// Cargar horarios en el TextArea
			List<String[]> horarios = model.getHorariosActividad(idAct);
			StringBuilder sb = new StringBuilder();
			for (String[] h : horarios) {
				sb.append(h[0]).append(": ").append(h[1]).append(" a ").append(h[2]).append("\n");
			}
			view.getTxtHorariosActuales().setText(sb.toString());

			// Limpiar tabla al cambiar de actividad para evitar confusión
			view.getModelConflictos().setRowCount(0);
		}
	}

	private void cambiarInstalacion() {
		List<String> opciones = model.getInstalaciones();
		String seleccion = (String) JOptionPane.showInputDialog(view, "Selecciona una nueva instalación:",
				"Cambiar Instalación", JOptionPane.QUESTION_MESSAGE, null, opciones.toArray(), opciones.get(0));

		if (seleccion != null) {
			int newIdInst = Integer.parseInt(seleccion.split(" - ")[0]);
			int idAct = getSelectedIdActividad();

			if (model.actualizarInstalacion(idAct, newIdInst)) {
				JOptionPane.showMessageDialog(view, "Instalación actualizada.");
				cargarDatosActividad(); // Refrescar vista
			} else {
				JOptionPane.showMessageDialog(view, "Error al actualizar la base de datos.");
			}
		}
	}

	private void mostrarConflictos() {
		view.getModelConflictos().setRowCount(0);
		int idAct = getSelectedIdActividad();
		String[] datos = model.getDatosActividad(idAct);
		if (datos[1] == null)
			return;
		List<String[]> conflictos = model.buscarConflictosDetallados(Integer.parseInt(datos[1]), datos[2], datos[3],
				idAct);
		if (conflictos.isEmpty()) {
			view.getModelConflictos().addRow(new String[] { "-", "-", "SIN CONFLICTOS: La pista está libre" });
			JOptionPane.showMessageDialog(view, "Instalación disponible para el horario seleccionado.",
					"Verificación Exitosa", JOptionPane.INFORMATION_MESSAGE);
		} else {
			// Llenar tabla con los problemas encontrados
			for (String[] c : conflictos) {
				view.getModelConflictos().addRow(c);
			}

			JOptionPane.showMessageDialog(view,
					"Se han detectado " + conflictos.size() + " colisiones de horario.\n"
							+ "Por favor, cambie la instalación o el horario para poder guardar.",
					"Conflicto de Reservas", JOptionPane.WARNING_MESSAGE);
		}
	}

	private int getSelectedIdActividad() {
		String seleccion = (String) view.getCbActividades().getSelectedItem();
		return (seleccion == null) ? -1 : Integer.parseInt(seleccion.split(" - ")[0]);
	}

	public static void main(String[] args) {
		Database db = new Database();
		db.createDatabase(false);
		db.loadDatabase();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		Reserva_Instalacion_Admin_View view = new Reserva_Instalacion_Admin_View();
		Reserva_Instalacion_Admin_Model model = new Reserva_Instalacion_Admin_Model();

		new Reserva_Instalacion_Admin_Controller(view, model);

		view.setVisible(true);
		view.setLocationRelativeTo(null);

		EventQueue.invokeLater(() -> {
			new DatabaseViewer().setVisible(true);
		});
	}
}
