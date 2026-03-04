package si.pl14.reservas;

import java.time.LocalDate;
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

public class ReservaController {
	private ReservaModel model;
	private ReservaView view;
	
	private Integer idSocioActual = null;
	private final int HORAS_MAXIMAS_SEGUIDAS = 2;
	private final int DIAS_MAXIMOS_ANTELACION = 30;
	
	public ReservaController(ReservaModel m, ReservaView v) {
		model = m;
		view = v;
		
		this.initView();
	}
	
	public void initView() {
		this.getInstalaciones();
		
		Date hoy = new Date();
	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(hoy);
	    cal.add(Calendar.DAY_OF_MONTH, DIAS_MAXIMOS_ANTELACION); 
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
	                    setText(instalacion.getNombre() + " (" + instalacion.getCosteHora() + " €/h)");
	                }
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
			
			// Lo mandamos a la vista (false = no es un error, saldrá en color normal)
			view.setDatosSocio(info, false);
			
			// ¡MUY IMPORTANTE! Guardamos el ID del socio encontrado para usarlo en la reserva
			this.idSocioActual = socio.getIdSocio();
			
			// Limpiamos cualquier mensaje de error anterior de la reserva
			view.setTextoInformacion("");
		}
	}
	
	private void realizarReserva() {
		Date fechaDate = view.getCalendarFecha().getDate();
		
		LocalDate fechaSeleccionadaLocal = fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate hoy = LocalDate.now();
        
        // Calculamos la diferencia en días para verificar que no se pueda reservar con mayor antelación que la descrita
        long diasAntelacion = ChronoUnit.DAYS.between(hoy, fechaSeleccionadaLocal);
		
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
		if (this.idSocioActual == null) {
			view.setTextoInformacion("DEBES BUSCAR Y SELECCIONAR UN SOCIO PRIMERO");
		} else if (idInstalacionSeleccionada <= 0) {
			view.setTextoInformacion("SELECCIONA UNA INSTALACIÓN");
		} else if (horaInicioSeleccionada == -1 || horaFinSeleccionada == -1) {
		    view.setTextoInformacion("Por favor, selecciona una hora de inicio y de fin.");
		} else if(!model.estaAlCorriente(idSocioActual)) {
			view.setTextoInformacion("SOCIO CON PAGOS PENDIENTES. NO PUEDE RESERVAR");
		} else if (diasAntelacion < 0) {
			view.setTextoInformacion("NO SE PUEDE RESERVAR EN FECHAS PASADAS");
		} else if (diasAntelacion > DIAS_MAXIMOS_ANTELACION) {
			view.setTextoInformacion("SÓLO SE PUEDE RESERVAR CON " + DIAS_MAXIMOS_ANTELACION + " DÍAS DE ANTELACIÓN");
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
			reserva.setIdSocio(idSocioActual);
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
