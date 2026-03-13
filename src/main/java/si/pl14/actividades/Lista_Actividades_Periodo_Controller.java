package si.pl14.actividades;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Lista_Actividades_Periodo_Controller {
	private Lista_Actividades_Periodo_Model model;
	private Lista_Actividades_Periodo_Admin_Vista view;

	public Lista_Actividades_Periodo_Controller(Lista_Actividades_Periodo_Model model,
			Lista_Actividades_Periodo_Admin_Vista view) {
		this.model = model;
		this.view = view;
	}

	public void initController() {
    // Eliminar view.cargarPeriodos(...) ya que no se usa

    view.getBtnConsultar().addActionListener(e -> {
        String fIni = view.getFechaInicioFiltro();
        String fFin = view.getFechaFinFiltro();

        if (!fIni.isEmpty() && !fFin.isEmpty()) {
            List<ActividadDTO> dtos = model.obtenerActividadesPorRangoFechas(fIni, fFin);
            
            List<Object[]> filasTabla = new ArrayList<>();
            for (ActividadDTO dto : dtos) {
                filasTabla.add(dto.toArray());
            }
            view.actualizarTabla(filasTabla);
        } else {
            JOptionPane.showMessageDialog(view, "Por favor, introduce ambas fechas (AAAA-MM-DD)");
        }
    });

    view.getBtnVolver().addActionListener(e -> view.dispose());
    view.setVisible(true);
}
}
