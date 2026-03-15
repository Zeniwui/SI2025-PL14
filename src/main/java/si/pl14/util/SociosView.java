
package si.pl14.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Importa aquí tus clases del MVC
import si.pl14.reservas.ReservaModel;
import si.pl14.reservas.ReservaController;
import si.pl14.reservas.ReservaView;

public class SociosView {

    private JFrame frame;

    public SociosView() {
        initialize();
        frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame("Panel de Socio");
        frame.setBounds(200, 200, 400, 150);
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
        // 1. AQUÍ SE AÑADEN LAS HISTORIAS DE USUARIO DEL SOCIO
        // =====================================================================
        cbHistorias.addItem("Seleccionar historia de usuario");
        cbHistorias.addItem("HU1: Reservar instalacion para fecha determinada");
        cbHistorias.addItem("HU2: Ver disponibilidad de las instalaciones");
        
        panel.add(cbHistorias, BorderLayout.CENTER);

        JButton btnEjecutar = new JButton("Ejecutar Historia de Usuario");
        btnEjecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int seleccion = cbHistorias.getSelectedIndex();
                
                // =====================================================================
                // 2. AQUÍ SE INSTANCIA AL MVC CORRESPONDIENTE A LA HISTORIA SELECCIONADA
                // =====================================================================
                switch (seleccion) {
                case 1:
                	ReservaController controllerReserva = new ReservaController(new ReservaModel(), new ReservaView());
                	controllerReserva.initController();
                	break;
                case 2:
                	break;
                }
            }
        });
        
        panel.add(btnEjecutar, BorderLayout.SOUTH);
        frame.getContentPane().add(panel);
    }
}