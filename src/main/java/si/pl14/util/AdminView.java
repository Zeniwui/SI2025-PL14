package si.pl14.util;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import si.pl14.actividadesEmma.Lista_Actividades_Periodo_Admin_Vista;
import si.pl14.actividadesEmma.Lista_Actividades_Periodo_Controller;
import si.pl14.actividadesEmma.Lista_Actividades_Periodo_Model;
import si.pl14.actividadesEmma.Planificar_Actividad_Admin_View;
import si.pl14.actividadesEmma.Planificar_Actividad_Controller;
import si.pl14.actividadesEmma.Planificar_Actividad_Model;
import si.pl14.periodosinscripcion.PeriodosInscripcionController;
import si.pl14.periodosinscripcion.PeriodosInscripcionModel;
import si.pl14.periodosinscripcion.PeriodosInscripcionView;
import si.pl14.reservasAdmin.ReservaControllerAdmin;
import si.pl14.reservasAdmin.ReservaModelAdmin;
import si.pl14.reservasAdmin.ReservaViewAdmin;
import si.pl14.reservasEmma.Reserva_Instalacion_Admin_Controller;
import si.pl14.reservasEmma.Reserva_Instalacion_Admin_Model;
import si.pl14.reservasEmma.Reserva_Instalacion_Admin_View;
import si.pl14.visualizarInstalaciones.VisualizarReservasAdminController;
import si.pl14.visualizarInstalaciones.VisualizarReservasAdminModel;
import si.pl14.visualizarInstalaciones.VisualizarReservasAdminView;

public class AdminView {

    private JFrame frame;

    public AdminView() {
        initialize();
        frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame("Panel de Administrador");
        frame.setBounds(150, 150, 400, 150);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        frame.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitulo = new JLabel("Seleccione la historia de usuario a ejecutar:");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 12));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JComboBox<String> cbHistorias = new JComboBox<>();
        
        // =====================================================================
        // 1. AQUÍ DEBEMOS AÑADIR LAS HISTORIAS DE USUARIO DEL ADMINISTRADOR
        // =====================================================================
        cbHistorias.addItem("Seleccionar historia de usuario");
        cbHistorias.addItem("HU1: Reservar una instalacion para un socio");
        cbHistorias.addItem("HU2: Reservar una instalacion para una actividad en un periodo determinado"); // HU 33741
        cbHistorias.addItem("HU3: Visualizar reservas de instalaciones");
        cbHistorias.addItem("HU4: Planificar una actividad");
        cbHistorias.addItem("HU5: Crear un periodo de inscripcion");
        cbHistorias.addItem("HU6: Obtener lsita de actividades ofertadas en un periodo");
        
        panel.add(cbHistorias, BorderLayout.CENTER);

        JButton btnEjecutar = new JButton("Ejecutar Historia de Usuario");
        btnEjecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int seleccion = cbHistorias.getSelectedIndex();
                
                // =====================================================================
                // 2. AQUÍ INSTANCIAMOS AL MVC CORRESPONDIENTE A LA HISTORIA SELECCIONADA
                // =====================================================================
                switch (seleccion) {
                case 1:
                	ReservaControllerAdmin controllerReservaAdmin = new ReservaControllerAdmin(new ReservaModelAdmin(), new ReservaViewAdmin());
                	controllerReservaAdmin.initController();
                	break;
                case 2:
                	Reserva_Instalacion_Admin_View viewRI = new Reserva_Instalacion_Admin_View();
                    Reserva_Instalacion_Admin_Model modelRI = new Reserva_Instalacion_Admin_Model();
                    new Reserva_Instalacion_Admin_Controller(viewRI, modelRI);
                    viewRI.setVisible(true);
                    viewRI.setLocationRelativeTo(null);
                	break;
                case 3:
    				VisualizarReservasAdminController controllerVisualizarReservas = new VisualizarReservasAdminController(new VisualizarReservasAdminModel(), new VisualizarReservasAdminView());
    				controllerVisualizarReservas.initController();
                	break;
                case 4:
                	Planificar_Actividad_Admin_View vistaplanificar = new Planificar_Actividad_Admin_View();
            		Planificar_Actividad_Model modeloplanificar = new Planificar_Actividad_Model();
            		Planificar_Actividad_Controller controladorplanificar = new Planificar_Actividad_Controller(modeloplanificar, vistaplanificar);
            		controladorplanificar.initController();
            		//vistaplanificar.setVisible(true);
                	break;
                case 5:
                	PeriodosInscripcionController controllerPeriodosInscripcion = new PeriodosInscripcionController(new PeriodosInscripcionModel(), new PeriodosInscripcionView());
                	controllerPeriodosInscripcion.initController();
                	break;
                case 6:
                	Lista_Actividades_Periodo_Admin_Vista vistalista = new Lista_Actividades_Periodo_Admin_Vista();
    				Lista_Actividades_Periodo_Model modelolista = new Lista_Actividades_Periodo_Model();
    				Lista_Actividades_Periodo_Controller controladorlista = new Lista_Actividades_Periodo_Controller(modelolista,
    						vistalista);
    				controladorlista.initController();
                	break;
                }
            }
        });
        
        panel.add(btnEjecutar, BorderLayout.SOUTH);
        frame.getContentPane().add(panel);
    }
}
