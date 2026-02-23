package si.pl14.actividades;

import java.util.ArrayList;
import java.util.List;

public class Lista_Actividades_Periodo_Controller {
	private Lista_Actividades_Periodo_Model model;
	private Lista_Actividades_Periodo_Admin_Vista view;

	public Lista_Actividades_Periodo_Controller(Lista_Actividades_Periodo_Model model,
			Lista_Actividades_Periodo_Admin_Vista view) {
		this.model = model;
		this.view = view;
	}

	public void initController() {
		view.cargarPeriodos(model.obtenerPeriodos());

		view.getBtnConsultar().addActionListener(e -> {
		    int id = view.getIdPeriodoSeleccionado();
		    if (id != -1) {
		        List<ActividadDTO> dtos = model.obtenerActividadesPorPeriodoDTO(id);
		        
		        // Convertimos la lista de DTOs a una lista de Object[] para la JTable
		        List<Object[]> filasTabla = new ArrayList<>();
		        for (ActividadDTO dto : dtos) {
		            filasTabla.add(dto.toArray());
		        }
		        
		        view.actualizarTabla(filasTabla);
		    }
		});

		// esto ya se enlazara con las demas historias
		view.getBtnVolver().addActionListener(e -> view.dispose());

		view.setVisible(true);
	}
}
