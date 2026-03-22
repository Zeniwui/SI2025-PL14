package si.pl14.reservas;

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
import si.pl14.util.SwingUtil;
import si.pl14.util.Util;

public class ReservaController {
	private ReservaModel model;
	private ReservaView view;
	
	private final int ID_SOCIO_ACTUAL = 1;
	private final int HORAS_MAXIMAS_SEGUIDAS = 2;
	private final int HORAS_MAXIMAS_DIA = 3;
	private final int HORAS_MAXIMAS_MES = 8;
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
	
	private boolean superaHorasSeguidas(List<ReservaEntity> reservasDia, int nuevaHoraInicio, int nuevaHoraFin) {
		boolean[] horasActivas = new boolean[24];
		
		// 1. Marcamos las horas que el socio ya tiene reservadas ese día
		for (ReservaEntity r : reservasDia) {
			for (int i = r.getHoraInicio(); i < r.getHoraFin(); i++) {
				horasActivas[i] = true;
			}
		}
		
		// 2. Marcamos las horas que el socio quiere reservar ahora
		for (int i = nuevaHoraInicio; i < nuevaHoraFin; i++) {
			horasActivas[i] = true;
		}
		
		// 3. Contamos cuál es el bloque máximo de horas consecutivas
		int maxConsecutivas = 0;
		int actualesConsecutivas = 0;
		
		for (int i = 0; i < 24; i++) {
			if (horasActivas[i]) {
				actualesConsecutivas++;
				if (actualesConsecutivas > maxConsecutivas) {
					maxConsecutivas = actualesConsecutivas;
				}
			} else {
				actualesConsecutivas = 0;
			}
		}
		
		return maxConsecutivas > HORAS_MAXIMAS_SEGUIDAS;
	}
	
	private void realizarReserva() {
		Date fechaDate = view.getCalendarFecha().getDate();
		
		LocalDate fechaSeleccionadaLocal = fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate hoy = LocalDate.now();
        
        int horaActual = LocalTime.now().getHour();
        
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
		
		// Extraemos el mes actual (YYYY-MM) a partir de la fecha seleccionada
		String mesAnio = fechaSeleccionada.substring(0, 7); 
		
		// Obtenemos el historial de reservas de este socio para hoy y este mes
		List<ReservaEntity> reservasSocioDia = model.getReservasSocioEnDia(ID_SOCIO_ACTUAL, fechaSeleccionada);
		List<ReservaEntity> reservasSocioMes = model.getReservasSocioEnMes(ID_SOCIO_ACTUAL, mesAnio);
		
		// Calculamos las horas totales
		int horasAReservar = horaFinSeleccionada - horaInicioSeleccionada;
		
		// Calculamos las horas totales ya reservadas en el día
		int horasYaReservadasDia = 0;
		for (ReservaEntity r : reservasSocioDia) {
			horasYaReservadasDia += (r.getHoraFin() - r.getHoraInicio());
		}

		// Calculamos las horas totales ya reservadas en el mes
		int horasYaReservadasMes = 0;
		for (ReservaEntity r : reservasSocioMes) {
			horasYaReservadasMes += (r.getHoraFin() - r.getHoraInicio());
		}
		
		if (idInstalacionSeleccionada <= 0) {									//Comprobamos que este seleccionada una instalacion
			view.setTextoInformacion("SELECCIONA UNA INSTALACIÓN");
		} else if (horaInicioSeleccionada == -1 || horaFinSeleccionada == -1) {	//Comprobamos que haya seleccionada una hora de inicio y una hora de fin
		    view.setTextoInformacion("Por favor, selecciona una hora de inicio y de fin.");
		} else if(!model.estaAlCorriente(ID_SOCIO_ACTUAL)) {					//Comprobamos que el socio esté al corriente con los pagos
			view.setTextoInformacion("SOCIO CON PAGOS PENDIENTES. NO PUEDE RESERVAR");
		} else if (diasAntelacion < 0) {										//Comprobamos que la fecha no sea un día anterior a hoy
			view.setTextoInformacion("NO SE PUEDE RESERVAR EN FECHAS PASADAS");
		} else if (diasAntelacion == 0 && horaInicioSeleccionada <= horaActual) {  //Comprobamos que la hora que quiere reservar para hoy no haya pasado ya
			view.setTextoInformacion("NO PUEDES RESERVAR EN UNA HORA QUE YA HA PASADO");
		} else if (diasAntelacion > DIAS_MAXIMOS_ANTELACION) {					//Comprobamos que los dias de antelación no superen el numero descrito como parametro
			view.setTextoInformacion("SÓLO SE PUEDE RESERVAR CON " + DIAS_MAXIMOS_ANTELACION + " DÍAS DE ANTELACIÓN");
		} else if (horaFinSeleccionada <= horaInicioSeleccionada)  {			//Comprobamos que la hora de inicio sea menor a la hora de fin
			view.setTextoInformacion("LA HORA DE INICIO DEBE SER ANTERIOR A LA HORA DE FIN");
		} else if ((horaFinSeleccionada - horaInicioSeleccionada) > HORAS_MAXIMAS_SEGUIDAS) {	//Comprobamos que la reserva no dure mas de unas horas determinadas
			view.setTextoInformacion("NO SE PERMITE RESERVAR MAS DE " + HORAS_MAXIMAS_SEGUIDAS + " HORAS SEGUIDAS");
		} else if (superaHorasSeguidas(reservasSocioDia, horaInicioSeleccionada, horaFinSeleccionada)) {	//Comprobamos que no se pueda reservar mas de unas horas seguidas aunque sean diferentes reservas
			view.setTextoInformacion("NO SE PERMITE RESERVAR MÁS DE " + HORAS_MAXIMAS_SEGUIDAS + " HORAS SEGUIDAS");
		} else if ((horasYaReservadasDia + horasAReservar) > HORAS_MAXIMAS_DIA) {							//Comprobamos que no se puedan reservar mas de un numero de horas diarias
			view.setTextoInformacion("LÍMITE DIARIO: NO SE PUEDEN RESERVAR MÁS DE " + HORAS_MAXIMAS_DIA + " HORAS AL DÍA");
		} else if ((horasYaReservadasMes + horasAReservar) > HORAS_MAXIMAS_MES) {							//Comprobamos que no se puedan reservar mas de un numero de horas al mes
			view.setTextoInformacion("LÍMITE MENSUAL: NO SE PUEDEN RESERVAR MÁS DE " + HORAS_MAXIMAS_MES + " HORAS AL MES");
		} else if (!model.estaLibreAEsasHoras(fechaSeleccionada, horaInicioSeleccionada, horaFinSeleccionada, idInstalacionSeleccionada)) {		// Comprobamos que la instalacion este libre a esas horas ese dia
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
			
			String textoResguardo = model.generarResguardo(reserva, instalacionSeleccionada.getNombre());
			view.setTextoResumen(textoResguardo);
			
			SwingUtil.showMessage("Reserva realizada con éxito.\n\n" + textoResguardo, 
	                "Reserva Confirmada", JOptionPane.INFORMATION_MESSAGE);
		}

	}
}