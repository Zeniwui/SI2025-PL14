package si.pl14.reservas;

import java.awt.Color;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JOptionPane;

import si.pl14.model.InstalacionEntity;
import si.pl14.model.ReservaEntity;
import si.pl14.util.SwingUtil;
import si.pl14.util.Util;

public class ReservaController {
	private ReservaModel model;
	private ReservaView view;
	
	private final int ID_SOCIO_ACTUAL = 1;
	private final int HORAS_MAXIMAS_SEGUIDAS = 2;
	
	public ReservaController(ReservaModel m, ReservaView v) {
		model = m;
		view = v;
		
		this.initView();
	}
	
	public void initView() {
		this.getInstalaciones();
		view.getFrame().setVisible(true);
	}
	
	public void initController() {
		view.getBtnReservar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> realizarReserva()));
		
		
	}
	
	private void getInstalaciones() {
		List<InstalacionEntity> instalaciones = model.getListaInstalaciones();
		
		DefaultComboBoxModel<Object> lmodel = new DefaultComboBoxModel<>();
		
		/* Por si queremos que la primera selección del comboBox sea "seleccionar instalacion"
		InstalacionEntity opcionDefecto = new InstalacionEntity();
	    opcionDefecto.setNombre("Seleccionar instalación...");
	    opcionDefecto.setIdInstalacion(-1);
	    
	    lmodel.addElement(opcionDefecto);
		*/
		
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
		
		// view.getCbInstalaciones().setSelectedIndex(0);		
	}
	
	private void realizarReserva() {
		Date fechaDate = view.getCalendarFecha().getDate();
		
		String fechaSeleccionada = Util.dateToIsoString(fechaDate);
		
		InstalacionEntity instalacionSeleccionada = (InstalacionEntity) view.getCbInstalaciones().getSelectedItem();
		
		if (instalacionSeleccionada == null) {
	        view.setTextoInformacion("Seleccione una instalación válida");
	        return;
	    }
		
		int idInstalacionSeleccionada = instalacionSeleccionada.getIdInstalacion();
		int horaInicioSeleccionada = view.getHoraInicio();
		int horaFinSeleccionada = view.getHoraFin();
		String metodoPago;
		
		if (view.esPagoInmediato()) {
			metodoPago = "Pago inmediato";
		} else {
			metodoPago = "Cuota mensual";
		}
		
		if (!model.estaAlCorriente(ID_SOCIO_ACTUAL)) {
			view.setTextoInformacion("SOCIO CON PAGOS PENDIENTES. NO PUEDE RESERVAR");
		} else if (horaFinSeleccionada <= horaInicioSeleccionada)  {
			view.setTextoInformacion("LA HORA DE INICIO DEBE SER ANTERIOR A LA HORA DE FIN");
		} else if ((horaFinSeleccionada - horaInicioSeleccionada) > HORAS_MAXIMAS_SEGUIDAS) {
			view.setTextoInformacion("NO SE PERMITE RESERVAR MAS DE " + HORAS_MAXIMAS_SEGUIDAS + " HORAS SEGUIDAS");
		} else if (!model.estaLibreAEsasHoras(fechaSeleccionada, horaInicioSeleccionada, horaFinSeleccionada, idInstalacionSeleccionada)) {
			view.setTextoInformacion("LA INSTALACIÓN NO ESTÁ DISPONIBLE EN ESE HORARIO");
		} else {
			
			// Si aprueba todas las validaciones, creamos la reserva y la insertamos en la base de datos
			
			float costeReserva = model.getPrecioReserva(idInstalacionSeleccionada, horaInicioSeleccionada, horaFinSeleccionada);
			view.setTextoInformacion("");
			
			ReservaEntity reserva = new ReservaEntity();
			reserva.setIdInstalacion(idInstalacionSeleccionada);
			reserva.setFecha(fechaSeleccionada);
			reserva.setHoraInicio(horaInicioSeleccionada);
			reserva.setHoraFin(horaFinSeleccionada);
			reserva.setIdSocio(ID_SOCIO_ACTUAL);
			reserva.setCosteReserva(costeReserva);
			reserva.setMetodoPago(metodoPago);
			
			if ("Pago inmediato".equals(metodoPago)) {
			    reserva.setEstadoPago("Pagado");
			} else {
			    reserva.setEstadoPago("Pendiente");
			}
			
			model.realizarReserva(reserva);
			
			String textoResguardo = model.generarResguardo(reserva);
			view.setTextoResumen(textoResguardo);
			
			SwingUtil.showMessage("Reserva realizada con éxito.\n\n" + textoResguardo, 
	                "Reserva Confirmada", JOptionPane.INFORMATION_MESSAGE);
		}

	}
}
