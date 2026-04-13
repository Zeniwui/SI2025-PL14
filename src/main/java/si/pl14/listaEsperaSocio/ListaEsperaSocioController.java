package si.pl14.listaEsperaSocio;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import si.pl14.model.ActividadInscripcionDTO;
import si.pl14.util.SwingUtil;

public class ListaEsperaSocioController {
	
	private ListaEsperaSocioModel model;
	private ListaEsperaSocioView view;
	
	private List<ActividadInscripcionDTO> actividadesActuales;
	private final int ID_SOCIO_ACTUAL = 1;
	private String nombreSocioActual;
	
	public ListaEsperaSocioController(ListaEsperaSocioModel m, ListaEsperaSocioView v) {
		model = m;
		view = v;		
		
		this.initView();
	}
	
	public void initView() {
		this.nombreSocioActual = model.getNombreSocioById(ID_SOCIO_ACTUAL);
		
		this.cargarTablaActividades();
		
		view.getFrame().setVisible(true);
	}
	
	public void initController() {
		view.getTablaActividades().getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && view.getTablaActividades().getSelectedRow() != -1) {
				actualizarPanelResumen();
			}
		});
		
		view.getBtnInscribirse().addActionListener(e -> 
			SwingUtil.exceptionWrapper(() -> intentarInscripcion())
		);
        
        view.getBtnCancelar().addActionListener(e -> view.getFrame().dispose());
	}
	
	private void cargarTablaActividades() {
		actividadesActuales = model.getActividadesInscripcionActual();
		DefaultTableModel tm = view.getModeloTabla();
		tm.setRowCount(0); 
		
		for (ActividadInscripcionDTO act : actividadesActuales) {
			tm.addRow(new Object[]{
				act.getNombre(),
				act.getInstalacion(),
				act.getPlazasDisponibles(),
				act.getFinInscripcion(), 
				act.getFechaInicio(),
				act.getFechaFin(),
				act.getPrecioSocio()
			});
		}
	}
	
	private void actualizarPanelResumen() {
		int selectedRow = view.getTablaActividades().getSelectedRow();
		ActividadInscripcionDTO act = actividadesActuales.get(selectedRow);
		
		String desc = act.getDescripcion() != null ? act.getDescripcion() : "Sin descripción";
		String horario = "Consultar horarios";
		
		view.setResumen(
			act.getNombre(), desc, horario, 
			String.valueOf(act.getFechaInicio()), 
			String.valueOf(act.getFechaFin()), 
			String.valueOf(act.getPrecioSocio())
		);
		
		boolean aforoCompleto = act.getPlazasDisponibles() <= 0;
		view.setModoListaEspera(aforoCompleto);
	}
	
	private void intentarInscripcion() {
		int selectedRow = view.getTablaActividades().getSelectedRow();
		if (selectedRow == -1) return;
		
		ActividadInscripcionDTO actividadSeleccionada = actividadesActuales.get(selectedRow);

		if (actividadSeleccionada.getPlazasDisponibles() > 0) {
            // Flujo inscripción normal
			model.realizarInscripcion(ID_SOCIO_ACTUAL, actividadSeleccionada);
			
			String resguardo = model.generaResguardoInscripcion(
					nombreSocioActual, 
					actividadSeleccionada.getNombre(), 
					actividadSeleccionada.getPrecioSocio()
			);
			
			SwingUtil.showMessage(
					"¡Inscripción formalizada con éxito!\nEl coste se añadirá a tu próxima cuota mensual.\n\n" + resguardo, 
					"Inscripción Confirmada", 
					JOptionPane.INFORMATION_MESSAGE
			);
		} else {
            // Flujo lista de espera
			model.apuntarListaEspera(ID_SOCIO_ACTUAL, actividadSeleccionada);
			
			SwingUtil.showMessage(
					"El aforo para '" + actividadSeleccionada.getNombre() + "' está completo.\nHas sido añadido a la lista de espera con éxito.\nNo se te cobrará nada hasta que se te asigne una plaza definitiva.", 
					"Añadido a Lista de Espera", 
					JOptionPane.INFORMATION_MESSAGE
			);
		}
		
		cargarTablaActividades();
		view.limpiarResumen();
	}
	

}
