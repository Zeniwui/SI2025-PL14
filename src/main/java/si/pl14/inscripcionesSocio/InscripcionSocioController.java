package si.pl14.inscripcionesSocio;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import si.pl14.model.ActividadInscripcionDTO;
import si.pl14.util.SwingUtil;

public class InscripcionSocioController {
	
	private InscripcionSocioModel model;
	private InscripcionSocioView view;
	
	private List<ActividadInscripcionDTO> actividadesActuales;
	private final int ID_SOCIO_ACTUAL = 2;
	
	public InscripcionSocioController(InscripcionSocioModel m, InscripcionSocioView v) {
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
	}
	
	private void intentarInscripcion() {
		int selectedRow = view.getTablaActividades().getSelectedRow();
		if (selectedRow == -1) return;
		
		ActividadInscripcionDTO actividadSeleccionada = actividadesActuales.get(selectedRow);

		model.realizarInscripcion(ID_SOCIO_ACTUAL, actividadSeleccionada);
		
		SwingUtil.showMessage("¡Inscripción formalizada con éxito!\nEl coste (" + actividadSeleccionada.getPrecioSocio() + 
                              "€) se añadirá a tu próxima cuota mensual.\nSe ha generado el resguardo.", 
                              "Inscripción Confirmada", JOptionPane.INFORMATION_MESSAGE);
		
		cargarTablaActividades();
		view.limpiarResumen();
	}
	

}
