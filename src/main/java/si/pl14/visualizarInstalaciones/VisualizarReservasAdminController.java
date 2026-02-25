package si.pl14.visualizarInstalaciones;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.time.temporal.TemporalAdjusters;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import si.pl14.model.InstalacionEntity;
import si.pl14.model.ReservaDTO;
import si.pl14.util.SwingUtil;

public class VisualizarReservasAdminController {
	
	private VisualizarReservasAdminModel model;
	private VisualizarReservasAdminView view;
	
	private LocalDate fechaLunesActual;
	
	private final DateTimeFormatter formatterRango = DateTimeFormatter.ofPattern("dd MMMM", new Locale("es", "ES"));
    private final DateTimeFormatter formatterCabecera = DateTimeFormatter.ofPattern("EEEE dd", new Locale("es", "ES"));
    private final DateTimeFormatter formatterIsoDb = DateTimeFormatter.ISO_LOCAL_DATE;
    
    private final int HORA_APERTURA = 9;
    private final int DIAS_MAXIMOS_VISUALIZACION = 30;
	
	public VisualizarReservasAdminController(VisualizarReservasAdminModel m, VisualizarReservasAdminView v) {
		model = m;
		view = v;
		
		this.initView();
	}
	
	public void initView() {
		
		this.getInstalaciones();
		
		fechaLunesActual = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		this.actualizarVistaSemana();
		
		view.getFrame().setVisible(true);
	}
	
	public void initController() {
		view.getCbInstalaciones().addActionListener(e -> SwingUtil.exceptionWrapper(() -> cargarDatosTabla()));
		view.getBtnSemanaAnterior().addActionListener(e -> SwingUtil.exceptionWrapper(() -> cambiarSemana(-1)));
		view.getBtnSemanaSiguiente().addActionListener(e -> SwingUtil.exceptionWrapper(() -> cambiarSemana(1)));
	}
	
	private void getInstalaciones() {
		List<InstalacionEntity> instalaciones = model.getListaInstalaciones();
		
		DefaultComboBoxModel<Object> lmodel = new DefaultComboBoxModel<>();
		
		InstalacionEntity opcionDefecto = new InstalacionEntity();
	    opcionDefecto.setNombre("Seleccionar instalación...");
	    opcionDefecto.setIdInstalacion(-1);
	    
	    lmodel.addElement(opcionDefecto);
				
		for (InstalacionEntity i : instalaciones) {
	        lmodel.addElement(i);
	    }
		
		view.getCbInstalaciones().setModel(lmodel);
		
		view.getCbInstalaciones().setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public java.awt.Component getListCellRendererComponent(
	                javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	            
	            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	            
	            if (value instanceof InstalacionEntity) {
	                InstalacionEntity instalacion = (InstalacionEntity) value;
	                setText(instalacion.getNombre());
	            }
	            return this;
	        }
	    });
		
		view.getCbInstalaciones().setSelectedIndex(0);		
	}
	
	private void cambiarSemana(int semanas) {
		fechaLunesActual = fechaLunesActual.plusWeeks(semanas);
		actualizarVistaSemana();
	}
	
	private void actualizarVistaSemana() {
		LocalDate hoy = LocalDate.now();
		LocalDate fechaLimite = hoy.plusDays(DIAS_MAXIMOS_VISUALIZACION);
		
		LocalDate fechaDomingoActual = fechaLunesActual.plusDays(6);
		
		String rangoTexto = fechaLunesActual.format(formatterRango) + " - " + fechaDomingoActual.format(formatterRango);
        view.getLblRangoFechas().setText(rangoTexto);
        
        JTableHeader header = view.getTablaHorario().getTableHeader();
        boolean[] diasValidos = new boolean[7];
        
        for (int i = 0; i < 7; i++) {
            LocalDate fechaDia = fechaLunesActual.plusDays(i);
            String nombreColumna = capitalize(fechaDia.format(formatterCabecera));
            view.getTablaHorario().getColumnModel().getColumn(i + 1).setHeaderValue(nombreColumna);
            
            diasValidos[i] = !fechaDia.isBefore(hoy) && !fechaDia.isAfter(fechaLimite);
        }
        header.repaint();

        view.setDiasValidos(diasValidos);

        LocalDate lunesHoy = hoy.with(DayOfWeek.MONDAY);
        LocalDate lunesLimite = fechaLimite.with(DayOfWeek.MONDAY);
        
        view.getBtnSemanaAnterior().setEnabled(fechaLunesActual.isAfter(lunesHoy));
        
        view.getBtnSemanaSiguiente().setEnabled(fechaLunesActual.isBefore(lunesLimite));

        cargarDatosTabla();
	}
	
	private void cargarDatosTabla() {
		DefaultTableModel tableModel = view.getTableModel();
		
		InstalacionEntity instalacionSeleccionada = (InstalacionEntity) view.getCbInstalaciones().getSelectedItem();
		
		if (instalacionSeleccionada.getIdInstalacion() <= 0) {
			view.getPanelTabla().setVisible(false);
			return;
		} else {
			view.getPanelTabla().setVisible(true);
		}
		
		for (int fila = 0; fila < tableModel.getRowCount(); fila++) {
            for (int col = 1; col <= 7; col++) {
                tableModel.setValueAt("Libre", fila, col);
            }
        }
		
		LocalDate fechaDomingoActual = fechaLunesActual.plusDays(6);
		String fechaInicio = fechaLunesActual.format(formatterIsoDb);
        String fechaFin = fechaDomingoActual.format(formatterIsoDb);
        
        List<ReservaDTO> reservas = model.getReservasEnPeriodo(instalacionSeleccionada.getIdInstalacion(), fechaInicio, fechaFin);
        
        for (ReservaDTO reserva: reservas) {
    		LocalDate fechaReserva = LocalDate.parse(reserva.getFecha());
            
            int columna = fechaReserva.getDayOfWeek().getValue();
            
            int filaInicio = reserva.getHoraInicio() - HORA_APERTURA;
            int filaFin = reserva.getHoraFin() - HORA_APERTURA;
            
            String textoOcupado = "Ocupado";
            if (reserva.getNombreActividad() != null && !reserva.getNombreActividad().isEmpty()) {
                textoOcupado = reserva.getNombreActividad();
            } else if (reserva.getNombreSocio() != null && !reserva.getNombreSocio().isEmpty()) {
                textoOcupado = reserva.getNombreSocio();
            }

            for (int fila = filaInicio; fila < filaFin; fila++) {
                if (fila >= 0 && fila < tableModel.getRowCount()) {
                    tableModel.setValueAt(textoOcupado, fila, columna);
                }
            }
        }
	}
	
	private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
