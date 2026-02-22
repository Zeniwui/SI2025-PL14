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

import si.pl14.reservas.ReservaController;
// Importa aquí tus clases del MVC
import si.pl14.reservas.ReservaModel;
import si.pl14.reservas.ReservaView;
import si.pl14.reservas.VisualizarReservasAdminController;
import si.pl14.reservas.VisualizarReservasAdminView;

public class AdminView {

    private JFrame frame;

    public AdminView() {
        initialize();
        frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame("Panel de Administrador");
        frame.setBounds(150, 150, 400, 150);
        // Usamos DISPOSE_ON_CLOSE para que al cerrar esta ventana NO se cierre toda la app
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        frame.setLocationRelativeTo(null); // Centrar en pantalla
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitulo = new JLabel("Seleccione la historia de usuario a ejecutar:");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 12));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JComboBox<String> cbHistorias = new JComboBox<>();
        
        // =====================================================================
        // 1. AQUÍ DEBES AÑADIR LAS HISTORIAS DE USUARIO DEL ADMINISTRADOR
        // =====================================================================
        cbHistorias.addItem("Seleccionar historia de usuario");
        cbHistorias.addItem("HU1: Reservar una instalacion para un socio");
        cbHistorias.addItem("HU2: Reservar una instalacion para una actividad");
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
                // 2. AQUÍ INSTANCIAS EL MVC CORRESPONDIENTE A LA HISTORIA SELECCIONADA
                // =====================================================================
                switch (seleccion) {
                case 1:
                	break;
                case 2:
                	break;
                case 3:
    				VisualizarReservasAdminController controllerVisualizarReservas = new VisualizarReservasAdminController(new ReservaModel(), new VisualizarReservasAdminView());
    				controllerVisualizarReservas.initController();
                	break;
                case 4:
                	break;
                case 5:
                	break;
                case 6:
                	break;
                }
            }
        });
        
        panel.add(btnEjecutar, BorderLayout.SOUTH);
        frame.getContentPane().add(panel);
    }
}
