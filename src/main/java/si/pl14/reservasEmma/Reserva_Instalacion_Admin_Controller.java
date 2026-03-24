package si.pl14.reservasEmma;

import javax.swing.*;

import si.pl14.util.Database;
import si.pl14.util.DatabaseViewer;

import java.awt.EventQueue;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Reserva_Instalacion_Admin_Controller {

    private Reserva_Instalacion_Admin_View view;
    private Reserva_Instalacion_Admin_Model model;

    public Reserva_Instalacion_Admin_Controller(Reserva_Instalacion_Admin_View view, Reserva_Instalacion_Admin_Model model) {
        this.view = view;
        this.model = model;
        initController();
    }

    private void initController() {
        for (String a : model.getActividades()) view.getCbActividades().addItem(a);
        view.getCbActividades().addActionListener(e -> cargarDatosActividad());
        view.getBtnCambiarInstalacion().addActionListener(e -> cambiarInstalacion());
        view.getBtnVerConflictos().addActionListener(e -> mostrarConflictos());
        view.getBtnGestionarHorarios().addActionListener(e -> gestionarHorarios());
        view.getBtnCrearReservas().addActionListener(e -> crearReservas());
        if (view.getCbActividades().getItemCount() > 0) cargarDatosActividad();
    }

    private void cargarDatosActividad() {
        int idAct = getSelectedIdActividad();
        String[] d = model.getDatosActividad(idAct);
        if (d[0] != null) {
            view.getLblInstalacionActual().setText(d[0]);
            view.getTxtFiltroInicio().setText(d[2]);
            view.getTxtFiltroFin().setText(d[3]);
            List<String[]> h = model.getHorariosActividad(idAct);
            StringBuilder sb = new StringBuilder();
            for (String[] row : h) sb.append(row[0]).append(": ").append(row[1]).append("-").append(row[2]).append("\n");
            view.getTxtHorariosActuales().setText(sb.toString());
            view.getModelConflictos().setRowCount(0);
        }
    }

    private void gestionarHorarios() {
        String[] opciones = {"Añadir Nuevo Horario", "Eliminar Horario Existente"};
        int seleccion = JOptionPane.showOptionDialog(view, "Elija una acción:", "Gestión de Horarios", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);

        if (seleccion == 0) añadirNuevo();
        else if (seleccion == 1) eliminarExistente();
    }

    private void añadirNuevo() {
        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};
        String d = (String) JOptionPane.showInputDialog(view, "Día:", "Nuevo Horario", JOptionPane.QUESTION_MESSAGE, null, dias, dias[0]);
        String ini = JOptionPane.showInputDialog(view, "Hora Inicio (HH:mm):", "10:00");
        String fin = JOptionPane.showInputDialog(view, "Hora Fin (HH:mm):", "11:30");

        if (d != null && ini != null && fin != null) {
            if (validarHoras(ini, fin)) {
                model.insertarNuevoHorario(getSelectedIdActividad(), d, ini, fin);
                cargarDatosActividad();
            }
        }
    }

    private void eliminarExistente() {
        List<String[]> lista = model.getHorariosActividad(getSelectedIdActividad());
        if (lista.isEmpty()) return;
        String[] ops = new String[lista.size()];
        for(int i=0; i<lista.size(); i++) ops[i] = lista.get(i)[0] + " " + lista.get(i)[1] + "-" + lista.get(i)[2];
        
        String sel = (String) JOptionPane.showInputDialog(view, "Borrar:", "Eliminar", JOptionPane.QUESTION_MESSAGE, null, ops, ops[0]);
        if (sel != null) {
            String[] parts = sel.split(" ");
            String[] times = parts[1].split("-");
            model.eliminarHorario(getSelectedIdActividad(), parts[0], times[0], times[1]);
            cargarDatosActividad();
        }
    }

    private boolean validarHoras(String ini, String fin) {
        try {
            LocalTime t1 = LocalTime.parse(ini);
            LocalTime t2 = LocalTime.parse(fin);
            if (!t1.isBefore(t2)) {
                JOptionPane.showMessageDialog(view, "Error: El inicio debe ser antes que el fin.");
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Error: Formato HH:mm (ej 09:30)");
            return false;
        }
    }

    private void mostrarConflictos() {
        view.getModelConflictos().setRowCount(0);
        int idAct = getSelectedIdActividad();
        String[] d = model.getDatosActividad(idAct);
        List<String[]> c = model.buscarConflictosDetallados(Integer.parseInt(d[1]), view.getTxtFiltroInicio().getText(), view.getTxtFiltroFin().getText(), idAct);
        if (c.isEmpty()) view.getModelConflictos().addRow(new String[]{"-", "-", "LIBRE: Pista disponible"});
        else for (String[] row : c) view.getModelConflictos().addRow(row);
    }

    private void crearReservas() {
        int idAct = getSelectedIdActividad();
        String[] d = model.getDatosActividad(idAct);
        String fI = view.getTxtFiltroInicio().getText();
        String fF = view.getTxtFiltroFin().getText();
        
        if (!model.buscarConflictosDetallados(Integer.parseInt(d[1]), fI, fF, idAct).isEmpty()) {
            JOptionPane.showMessageDialog(view, "No se puede: existen conflictos."); return;
        }

        List<String[]> horarios = model.getHorariosActividad(idAct);
        List<String[]> reservas = new ArrayList<>();
        LocalDate start = LocalDate.parse(fI);
        LocalDate end = LocalDate.parse(fF);

        for (String[] h : horarios) {
            DayOfWeek dow = DayOfWeek.valueOf(traducir(h[0]));
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                if (date.getDayOfWeek() == dow) reservas.add(new String[]{date.toString(), h[1], h[2]});
            }
        }
        
        // --- SUGERENCIA APLICADA: Diálogo de confirmación ---
        if (reservas.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No hay reservas que generar en este rango de fechas con los horarios actuales.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(view, 
                "¿Estás seguro de que deseas generar " + reservas.size() + " reservas para esta actividad?", 
                "Confirmar Generación", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (model.insertarReservasGeneradas(Integer.parseInt(d[1]), idAct, reservas)) {
                JOptionPane.showMessageDialog(view, "Se han creado correctamente " + reservas.size() + " reservas.");
            } else {
                JOptionPane.showMessageDialog(view, "Ocurrió un error al intentar guardar las reservas en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String traducir(String d) {
        if(d.equals("MIERCOLES")) return "WEDNESDAY";
        if(d.equals("SABADO")) return "SATURDAY";
        if(d.equals("DOMINGO")) return "SUNDAY";
        if(d.equals("JUEVES")) return "THURSDAY";
        if(d.equals("MARTES")) return "TUESDAY";
        if(d.equals("VIERNES")) return "FRIDAY";
        return "MONDAY";
    }

    private int getSelectedIdActividad() {
        return Integer.parseInt(((String) view.getCbActividades().getSelectedItem()).split(" - ")[0]);
    }
    
    private void cambiarInstalacion() {
        // Obtener la lista de instalaciones del modelo
        List<String> opciones = model.getInstalaciones();
        
        if (opciones.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No hay instalaciones configuradas en el sistema.");
            return;
        }

        String seleccion = (String) JOptionPane.showInputDialog(
                view, 
                "Seleccione la nueva instalación para esta actividad:",
                "Cambiar Instalación", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                opciones.toArray(), 
                opciones.get(0)
        );

        // Si el usuario no canceló, procesar el cambio
        if (seleccion != null) {
            try {
                int newIdInst = Integer.parseInt(seleccion.split(" - ")[0]);
                int idAct = getSelectedIdActividad();

                if (model.actualizarInstalacion(idAct, newIdInst)) {
                    JOptionPane.showMessageDialog(view, "Instalación actualizada correctamente.");
                    
                    // Actualizamos el texto de la instalación en la vista
                    String nombreInst = seleccion.split(" - ")[1];
                    view.getLblInstalacionActual().setText(nombreInst);
                    
                    // Limpiamos la tabla de conflictos porque el escenario ha cambiado
                    view.getModelConflictos().setRowCount(0);
                } else {
                    JOptionPane.showMessageDialog(view, "Error al actualizar la instalación en la base de datos.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Error al procesar la selección: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

        Reserva_Instalacion_Admin_View view = new Reserva_Instalacion_Admin_View();
        Reserva_Instalacion_Admin_Model model = new Reserva_Instalacion_Admin_Model();

        new Reserva_Instalacion_Admin_Controller(view, model);

        view.setVisible(true);
        view.setLocationRelativeTo(null);

        EventQueue.invokeLater(() -> new DatabaseViewer().setVisible(true));
    }
}