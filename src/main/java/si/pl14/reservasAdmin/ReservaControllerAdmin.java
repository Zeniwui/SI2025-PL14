package si.pl14.reservasAdmin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JOptionPane;

import si.pl14.model.InstalacionEntity;
import si.pl14.model.ReservaEntity;
import si.pl14.model.SocioDTO;
import si.pl14.util.SwingUtil;
import si.pl14.util.Util;

public class ReservaControllerAdmin {
	private ReservaModelAdmin model;
	private ReservaViewAdmin view;
	
	private Integer idSocioActual = null;
	private String nombreSocioActual = null;
	
	public ReservaControllerAdmin(ReservaModelAdmin m, ReservaViewAdmin v) {
		model = m;
		view = v;
		
		this.initView();
	}
	
	public void initView() {
		this.getInstalaciones();
		
		Date hoy = new Date();
	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(hoy);
	    cal.add(Calendar.DAY_OF_MONTH, model.getDiasMaximosAntelacion()); 
	    Date fechaMaxima = cal.getTime();
	    
	    view.getCalendarFecha().setMinSelectableDate(hoy);
	    view.getCalendarFecha().setMaxSelectableDate(fechaMaxima);
	    
	    int horaApertura = model.getHoraApertura();
	    int horaCierre = model.getHoraCierre();
	    view.setHorariosDisponibles(horaApertura, horaCierre);
		
		view.getFrame().setVisible(true);
	}
	
	public void initController() {
		view.getBtnReservar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> realizarReserva()));
		
		view.getCbInstalaciones().addActionListener(e -> SwingUtil.exceptionWrapper(() -> calcularYMostrarPrecio()));
		view.getSpinHoraInicio().addChangeListener(e -> SwingUtil.exceptionWrapper(() -> calcularYMostrarPrecio()));
		view.getSpinHoraFin().addChangeListener(e -> SwingUtil.exceptionWrapper(() -> calcularYMostrarPrecio()));
		
		view.getBtnBuscarSocio().addActionListener(e -> SwingUtil.exceptionWrapper(() -> buscarSocio()));
		
		view.getBtnCancelar().addActionListener(e -> view.getFrame().dispose());
	}
	
	private void getInstalaciones() {
		List<InstalacionEntity> instalaciones = model.getListaInstalaciones();
		
		DefaultComboBoxModel<Object> lmodel = new DefaultComboBoxModel<>();
		
		// Por si queremos que la primera selección del comboBox sea "seleccionar instalacion"
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
	                if (instalacion.getIdInstalacion() == -1) {
	                    setText(instalacion.getNombre());
	                } else {
	                	setText(String.format("%s (%.2f €/h)", instalacion.getNombre(), instalacion.getCosteHora()));	                }
	            }
	            return this;
	        }
	    });
		
		view.getCbInstalaciones().setSelectedIndex(0);		
	}
	
	private void calcularYMostrarPrecio() {
	    InstalacionEntity instalacion = (InstalacionEntity) view.getCbInstalaciones().getSelectedItem();
	    int horaInicio = view.getHoraInicio();
	    int horaFin = view.getHoraFin();

	    if (instalacion != null && instalacion.getIdInstalacion() > 0 
	            && horaInicio != -1 && horaFin != -1 && horaFin > horaInicio) {
	        
	        float precio = model.getPrecioReserva(instalacion.getIdInstalacion(), horaInicio, horaFin);
	        view.getLblPrecioTotal().setText(String.format("Precio Total: %.2f €", precio));
	    } else {
	        view.getLblPrecioTotal().setText("Precio Total: -- €");
	    }
	}
	
	
	private void buscarSocio() {
		String dni = view.getDniBusqueda();
		
		if (dni == null || dni.isEmpty()) {
			view.setDatosSocio("Por favor, introduce un DNI para buscar.", true);
			this.idSocioActual = null;
			return;
		}
		
		SocioDTO socio = model.getSocioByDni(dni);
		
		if (socio == null) {
			view.setDatosSocio("No existe ningún socio con el DNI: " + dni, true);
			this.idSocioActual = null;
		} else {
			String nombreCompleto = socio.getNombre() + " " + socio.getApellidos();
			String info = "Socio: " + nombreCompleto + " - Pagos: " + socio.getEstadoPagos();
			
			view.setDatosSocio(info, false);
			
			this.idSocioActual = socio.getIdSocio();
			this.nombreSocioActual = nombreCompleto;
			
			view.setTextoInformacion("");
		}
	}
	
	private boolean sonDatosInterfazValidos() {
		if (this.idSocioActual == null) {
			view.setTextoInformacion("DEBES BUSCAR Y SELECCIONAR UN SOCIO PRIMERO");
			return false; 
		}
		
		InstalacionEntity instalacionSeleccionada = (InstalacionEntity) view.getCbInstalaciones().getSelectedItem();
		if (instalacionSeleccionada == null || instalacionSeleccionada.getIdInstalacion() <= 0) {
	        view.setTextoInformacion("SELECCIONA UNA INSTALACIÓN");
	        return false;
	    }
		
		int horaInicioSeleccionada = view.getHoraInicio();
		int horaFinSeleccionada = view.getHoraFin();
		if (horaInicioSeleccionada == -1 || horaFinSeleccionada == -1) {
		    view.setTextoInformacion("Por favor, selecciona una hora de inicio y de fin.");
		    return false;
		}
		
		if (horaFinSeleccionada <= horaInicioSeleccionada)  {
			view.setTextoInformacion("LA HORA DE INICIO DEBE SER ANTERIOR A LA HORA DE FIN");
			return false;
		}

		return true; 
	}
	
	private void realizarReserva() {
		
		if (!sonDatosInterfazValidos()) {
			return;
		}
		
		Date fechaDate = view.getCalendarFecha().getDate();
		InstalacionEntity instalacionSeleccionada = (InstalacionEntity) view.getCbInstalaciones().getSelectedItem();
		
		if (instalacionSeleccionada == null) {
	        view.setTextoInformacion("Seleccione una instalación válida");
	        return;
	    }
		
		int idInstalacionSeleccionada = instalacionSeleccionada.getIdInstalacion();
		int horaInicioSeleccionada = view.getHoraInicio();
		int horaFinSeleccionada = view.getHoraFin();
		
		String errorValidacion = model.validarReglas(idSocioActual, fechaDate, horaInicioSeleccionada, horaFinSeleccionada, idInstalacionSeleccionada);
		
		if (errorValidacion != null) {
		    view.setTextoInformacion(errorValidacion);
		    return;
		}
		
		view.setTextoInformacion("");
		
		String metodoPago = view.esPagoInmediato() ? "Pago inmediato" : "Cuota mensual";
		String fechaSeleccionada = Util.dateToIsoString(fechaDate);
		float costeReserva = model.getPrecioReserva(idInstalacionSeleccionada, horaInicioSeleccionada, horaFinSeleccionada);
		
		ReservaEntity reserva = new ReservaEntity();
		reserva.setIdInstalacion(idInstalacionSeleccionada);
		reserva.setFecha(fechaSeleccionada);
		reserva.setHoraInicio(horaInicioSeleccionada);
		reserva.setHoraFin(horaFinSeleccionada);
		reserva.setIdSocio(idSocioActual);
		reserva.setCosteReserva(costeReserva);
		reserva.setMetodoPago(metodoPago);
		
		if ("Pago inmediato".equals(metodoPago)) {
		    reserva.setEstadoPago("Pagado");
		} else {
		    reserva.setEstadoPago("Pendiente");
		}
		
		String mensajeConfirmacion = String.format(
	            "¿Estás seguro de que deseas reservar la instalación %s para el socio %s?\nEl coste de %.2f € se gestionará como: %s.",
	            instalacionSeleccionada.getNombre(), nombreSocioActual, costeReserva, metodoPago
	    );
		
		int confirmacion = JOptionPane.showConfirmDialog(
	            view.getFrame(),
	            mensajeConfirmacion,
	            "Confirmar Reserva",
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.QUESTION_MESSAGE
	    );
		
	    if (confirmacion == JOptionPane.YES_OPTION) {
	        model.realizarReserva(reserva, instalacionSeleccionada.getNombre());
	        
	        String textoResguardo = model.generarResguardo(reserva, instalacionSeleccionada.getNombre(), nombreSocioActual);
	        view.setTextoResumen(textoResguardo);
	        
	        SwingUtil.showMessage("Reserva realizada con éxito.\n\n" + textoResguardo, 
	                "Reserva Confirmada", JOptionPane.INFORMATION_MESSAGE);
	    }
	}
}
