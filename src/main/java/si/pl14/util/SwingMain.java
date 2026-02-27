package si.pl14.util;

import java.awt.EventQueue;
import javax.swing.JFrame;

import si.pl14.disponibilidad.DisponibilidadController;
import si.pl14.disponibilidad.DisponibilidadModel;
import si.pl14.disponibilidad.DisponibilidadView;
import si.pl14.ejemplos.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SwingMain {

    private JFrame frame;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() { //NOSONAR codigo autogenerado
            public void run() {
                try {
                    SwingMain window = new SwingMain();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace(); //NOSONAR codigo autogenerado
                }
            }
        });
    }

    public SwingMain() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Main");
        frame.setBounds(0, 0, 320, 230);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JButton btnEjecutarTkrun = new JButton("Ejecutar giis.demo.tkrun");
        btnEjecutarTkrun.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
            public void actionPerformed(ActionEvent e) {
                CarrerasController controller = new CarrerasController(new CarrerasModel(), new CarrerasView());
                controller.initController();
            }
        });
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().add(btnEjecutarTkrun);

        // ── Disponibilidad de Instalación ─────────────────────────────────────
        JButton btnDisponibilidad = new JButton("Disponibilidad de Instalación");
        btnDisponibilidad.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
            public void actionPerformed(ActionEvent e) {
                DisponibilidadController ctrl =
                    new DisponibilidadController(
                        new DisponibilidadModel(),
                        new DisponibilidadView()
                    );
                ctrl.initController();
            }
        });
        frame.getContentPane().add(btnDisponibilidad);
        // ─────────────────────────────────────────────────────────────────────

        JButton btnInicializarBaseDeDatos = new JButton("Inicializar Base de Datos en Blanco");
        btnInicializarBaseDeDatos.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
            public void actionPerformed(ActionEvent e) {
                Database db = new Database();
                db.createDatabase(false);
            }
        });
        frame.getContentPane().add(btnInicializarBaseDeDatos);

        JButton btnCargarDatosIniciales = new JButton("Cargar Datos Iniciales para Pruebas");
        btnCargarDatosIniciales.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
            public void actionPerformed(ActionEvent e) {
                Database db = new Database();
                db.createDatabase(false);
                db.loadDatabase();
            }
        });
        frame.getContentPane().add(btnCargarDatosIniciales);
    }

    public JFrame getFrame() { return this.frame; }
}