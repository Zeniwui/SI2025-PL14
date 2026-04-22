package si.pl14.ReservasEmma;

import javax.swing.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import si.pl14.util.Database;
import si.pl14.util.DatabaseViewer;

public class Generacion_Automatica_Controller {

    private Generacion_Automatica_View view;
    private Generacion_Automatica_Model model;

    public Generacion_Automatica_Controller(Generacion_Automatica_View view, Generacion_Automatica_Model model) {
        this.view = view;
        this.model = model;
        initController();
    }

    private void initController() {
        cargarActividadesPendientes();
        view.getBtnGenerarReservas().addActionListener(e -> procesarGeneracion());
    }

    private void cargarActividadesPendientes() {
        view.getCbActividadesPendientes().removeAllItems();
        List<String> pendientes = model.getActividadesPendientes();
        if (pendientes.isEmpty()) {
            view.getCbActividadesPendientes().addItem("No hay actividades pendientes.");
            view.getBtnGenerarReservas().setEnabled(false);
        } else {
            for (String act : pendientes) view.getCbActividadesPendientes().addItem(act);
            view.getBtnGenerarReservas().setEnabled(true);
        }
    }

    private void procesarGeneracion() {
        String sel = (String) view.getCbActividadesPendientes().getSelectedItem();
        if (sel == null || sel.contains("No hay")) return;

        int idActividad = Integer.parseInt(sel.split(" - ")[0]);
        String[] datos = model.getDatosActividad(idActividad);
        int idInst = Integer.parseInt(datos[0]);
        LocalDate inicio = LocalDate.parse(datos[1]);
        LocalDate fin = LocalDate.parse(datos[2]);

        List<String[]> horarios = model.getHorariosActividad(idActividad);
        
        view.getModelLog().setRowCount(0); // Limpiar log anterior
        int reservasCreadas = 0;
        int sociosAnulados = 0;
        int conflictosIgnorados = 0;

        // Recorrer día a día
        for (LocalDate date = inicio; !date.isAfter(fin); date = date.plusDays(1)) {
            for (String[] h : horarios) {
                DayOfWeek diaHorario = traducirDia(h[0]);
                
                if (date.getDayOfWeek() == diaHorario) {
                    String fechaStr = date.toString();
                    
                    String hIni = h[1];
                    String hFin = h[2];
                    String franja = h[0].toUpperCase() + " (" + hIni + " - " + hFin + ")";

                    List<String[]> ocupantes = model.comprobarOcupacion(idInst, fechaStr, hIni, hFin);
                    boolean chocaConActividad = false;

                    // PRIMERA PASADA: Comprobar si hay una actividad (y abortar el slot si la hay)
                    for (String[] ocu : ocupantes) {
                        if (ocu[0].equals("ACTIVIDAD")) {
                            chocaConActividad = true;
                            view.getModelLog().addRow(new String[]{fechaStr, franja, "ERROR: OTRA ACTIVIDAD YA RESERVADA (ID: " + ocu[1] + ") - Se omite."});
                            conflictosIgnorados++;
                            break; 
                        }
                    }

                    // SEGUNDA PASADA: Si NO choca con actividad, procedemos a anular socios e insertar
                    if (!chocaConActividad) {
                        for (String[] ocu : ocupantes) {
                            if (ocu[0].equals("SOCIO")) {
                                int idReservaSocio = Integer.parseInt(ocu[1]);
                                String resultadoAnulacion = model.anularReservaSocio(idReservaSocio);
                                
                                if (resultadoAnulacion != null) {
                                    view.getModelLog().addRow(new String[]{
                                        fechaStr, franja, 
                                        "INFO: SOCIO ANULADO (ID: " + idReservaSocio + "). " + resultadoAnulacion
                                    });
                                    sociosAnulados++;
                                }
                            }
                        }

                        // Insertar reserva de actividad
                        if (model.insertarReserva(idInst, fechaStr, hIni, hFin, idActividad)) {
                            view.getModelLog().addRow(new String[]{fechaStr, franja, "OK: Reserva generada con éxito."});
                            reservasCreadas++;
                        } else {
                            view.getModelLog().addRow(new String[]{fechaStr, franja, "ERROR: Fallo de base de datos al insertar."});
                        }
                    }
                }
            }
        }

        String mensajeResumen = String.format(
        	    "<html>Proceso finalizado: <font color='blue'>%d creadas</font> | " +
        	    "<font color='orange'>%d socios anulados</font> | " +
        	    "<font color='red'>%d conflictos</font></html>",
        	    reservasCreadas, sociosAnulados, conflictosIgnorados
        	);

        	view.getLblResumen().setText(mensajeResumen);
        
        cargarActividadesPendientes();
    }

    private DayOfWeek traducirDia(String dia) {
        switch (dia.toUpperCase()) {
            case "LUNES": return DayOfWeek.MONDAY;
            case "MARTES": return DayOfWeek.TUESDAY;
            case "MIERCOLES": return DayOfWeek.WEDNESDAY;
            case "JUEVES": return DayOfWeek.THURSDAY;
            case "VIERNES": return DayOfWeek.FRIDAY;
            case "SABADO": return DayOfWeek.SATURDAY;
            case "DOMINGO": return DayOfWeek.SUNDAY;
            default: 
                // SUGERENCIA APLICADA: Lanzar excepción si el día no es válido en BD
                throw new IllegalArgumentException("Día de la semana desconocido en la BD: " + dia);
        }
    }

    public static void main(String[] args) {
        Database db = new Database();
        DatabaseViewer dbv = new DatabaseViewer();
        db.createDatabase(false);
        db.loadDatabase();

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

        Generacion_Automatica_View view = new Generacion_Automatica_View();
        Generacion_Automatica_Model model = new Generacion_Automatica_Model();
        new Generacion_Automatica_Controller(view, model);

        view.setVisible(true);
        dbv.setVisible(true);
    }
}