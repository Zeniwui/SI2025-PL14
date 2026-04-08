package si.pl14.inscripcionesNoSocio;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import si.pl14.model.ActividadInscripcionDTO;
import si.pl14.util.SwingUtil;

public class InscripcionNoSocioController {
	
	private InscripcionNoSocioModel model;
	private InscripcionNoSocioView view;
	private List<ActividadInscripcionDTO> actividadesActuales;
	
	private int paginaActual = 1;
	private final int FILAS_POR_PAGINA = 5;
	
	public InscripcionNoSocioController(InscripcionNoSocioModel m, InscripcionNoSocioView v) {
		model = m;
		view = v;		
		this.initView();
	}
	
	public void initView() {
		this.cargarTablaActividades();
		view.getFrame().setVisible(true);
	}
	
	public void initController() {
		view.getTablaActividades().getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && view.getTablaActividades().getSelectedRow() != -1) {
				actualizarPanelResumen();
			}
		});
		
		view.getBtnAnterior().addActionListener(e -> {
			if (paginaActual > 1) {
				paginaActual--;
				cargarTablaActividades();
			}
		});
		
		view.getBtnSiguiente().addActionListener(e -> {
			int totalPaginas = (int) Math.ceil((double) actividadesActuales.size() / FILAS_POR_PAGINA);
			if (totalPaginas == 0) totalPaginas = 1;
			
			if (paginaActual < totalPaginas) {
				paginaActual++;
				cargarTablaActividades();
			}
		});
		
		view.getBtnInscribirse().addActionListener(e -> 
			SwingUtil.exceptionWrapper(() -> intentarInscripcion())
		);
        
        view.getBtnCancelar().addActionListener(e -> view.getFrame().dispose());
	}
	
	private void cargarTablaActividades() {
		// Obtenemos todas las actividades de la base de datos
		actividadesActuales = model.getActividadesInscripcionActual();
		
		// Calculamos el total de páginas
		int totalActividades = actividadesActuales.size();
		int totalPaginas = (int) Math.ceil((double) totalActividades / FILAS_POR_PAGINA);
		if (totalPaginas == 0) totalPaginas = 1; // Si no hay actividades, mostramos "Página 1 de 1"
		
		// Ajuste de seguridad por si al inscribirnos nos quedamos sin actividades en la última página
		if (paginaActual > totalPaginas) {
			paginaActual = totalPaginas;
		}

		// Actualizamos la etiqueta de la vista y activamos/desactivamos botones
		view.getLblPagina().setText("Página " + paginaActual + " de " + totalPaginas);
		view.getBtnAnterior().setEnabled(paginaActual > 1);
		view.getBtnSiguiente().setEnabled(paginaActual < totalPaginas);

		DefaultTableModel tm = view.getModeloTabla();
		tm.setRowCount(0); 
		
		// Calculamos qué subconjunto de la lista debemos mostrar en la página actual
		int inicio = (paginaActual - 1) * FILAS_POR_PAGINA;
		int fin = Math.min(inicio + FILAS_POR_PAGINA, totalActividades);
		
		for (int i = inicio; i < fin; i++) {
			ActividadInscripcionDTO act = actividadesActuales.get(i);
			tm.addRow(new Object[]{
				act.getNombre(),
				act.getInstalacion(),
				act.getPlazasDisponibles(),
				act.getFinInscripcion(), 
				act.getFechaInicio(),
				act.getFechaFin(),
				act.getPrecioNoSocio()
			});
		}
	}
	
	private void actualizarPanelResumen() {
		int selectedRow = view.getTablaActividades().getSelectedRow();
		// Hay que ajustar el índice seleccionado sumándole el desplazamiento de la paginación
		int indiceReal = ((paginaActual - 1) * FILAS_POR_PAGINA) + selectedRow;
		
		ActividadInscripcionDTO act = actividadesActuales.get(indiceReal);
		
		String desc = act.getDescripcion() != null ? act.getDescripcion() : "Sin descripción";
		String horario = "Consultar horarios";
		
		view.setResumen(
			act.getNombre(), desc, horario, 
			String.valueOf(act.getFechaInicio()), 
			String.valueOf(act.getFechaFin()), 
			String.valueOf(act.getPrecioNoSocio())
		);
	}
	
	private void intentarInscripcion() {
		int selectedRow = view.getTablaActividades().getSelectedRow();
		if (selectedRow == -1) return;
		
		// Recogemos y validamos datos
		String dni = view.getTxtDni().getText().trim();
		String nombre = view.getTxtNombre().getText().trim();
		String apellidos = view.getTxtApellidos().getText().trim();
		String email = view.getTxtEmail().getText().trim();
		String telefonoStr = view.getTxtTelefono().getText().trim();
		
		if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || telefonoStr.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(view.getFrame(), "Por favor, rellena todos los datos del usuario.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int telefono;
		try {
			telefono = Integer.parseInt(telefonoStr);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(view.getFrame(), "El teléfono debe ser numérico.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Obtenemos la actividad seleccionada teniendo en cuenta la paginación
		int indiceReal = ((paginaActual - 1) * FILAS_POR_PAGINA) + selectedRow;
		ActividadInscripcionDTO actividadSeleccionada = actividadesActuales.get(indiceReal);

		// Ejecutamos en el modelo
		model.realizarInscripcion(dni, nombre, apellidos, telefono, email, actividadSeleccionada);
		
		// Resguardo
		String resguardo = model.generaResguardoInscripcion(
				nombre + " " + apellidos, 
				actividadSeleccionada.getNombre(), 
				actividadSeleccionada.getPrecioNoSocio()
		);
		
		SwingUtil.showMessage(
				"¡Inscripción y pago formalizados con éxito!\n\n" + resguardo, 
                "Inscripción Confirmada", 
                JOptionPane.INFORMATION_MESSAGE
        );
		
		// Refrescar
		cargarTablaActividades();
		view.limpiarResumen();
		limpiarFormulario();
	}
	
	private void limpiarFormulario() {
		view.getTxtDni().setText("");
		view.getTxtNombre().setText("");
		view.getTxtApellidos().setText("");
		view.getTxtTelefono().setText("");
		view.getTxtEmail().setText("");
	}
}