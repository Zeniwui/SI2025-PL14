package si.pl14.util;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

import si.pl14.visualizareservas.VisualizarReservasSocioController;
import si.pl14.visualizareservas.VisualizarReservasSocioModel;
import si.pl14.visualizareservas.VisualizarReservasSocioView;


/**
 * Punto de entrada principal que incluye botones para la ejecucion de las pantallas 
 * de las aplicaciones de ejemplo
 * y acciones de inicializacion de la base de datos.
 * No sigue MVC pues es solamente temporal para que durante el desarrollo se tenga posibilidad
 * de realizar acciones de inicializacion
 */
public class SwingMain {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the application.
	 */
	public SwingMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Main");
		frame.setBounds(0, 0, 287, 185);
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);		
		
		JButton btnAdmin = new JButton("Entrar al panel de administrador");
		btnAdmin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AdminView();
			}
		});
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.getContentPane().add(btnAdmin);
		
		JButton btnSocio = new JButton("Entrar al panel de socio");
		btnSocio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SociosView();
			}
		});
		frame.getContentPane().add(btnSocio);
		
		JButton btnVisualizarReservas = new JButton("Socio: Visualizar mis reservas en un periodo");
		btnVisualizarReservas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VisualizarReservasSocioController controller = new VisualizarReservasSocioController(
					new VisualizarReservasSocioModel(),
					new VisualizarReservasSocioView()
				);
				controller.initController();
			}
		});
		frame.getContentPane().add(btnVisualizarReservas);
		
			
		JButton btnInicializarBaseDeDatos = new JButton("Inicializar Base de Datos en Blanco");
		btnInicializarBaseDeDatos.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
			public void actionPerformed(ActionEvent e) {
				Database db=new Database();
				db.createDatabase(false);
			}
		});
		frame.getContentPane().add(btnInicializarBaseDeDatos);
			
		JButton btnCargarDatosIniciales = new JButton("Cargar Datos Iniciales para Pruebas");
		btnCargarDatosIniciales.addActionListener(new ActionListener() { //NOSONAR codigo autogenerado
			public void actionPerformed(ActionEvent e) {
				Database db=new Database();
				db.createDatabase(false);
				db.loadDatabase();
			}
		});
		frame.getContentPane().add(btnCargarDatosIniciales);
		
		// --- BLOQUE PARA VISUALIZAR LOS DATOS DE LAS TABLAS ---
        
        JButton btnDebugBD = new JButton("Consultar Tablas (Debug)");
        btnDebugBD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Abre la ventana de visualización
                DatabaseViewer viewer = new DatabaseViewer();
                viewer.setVisible(true);
            }
        });
        frame.getContentPane().add(btnDebugBD);
        
        // ----------------------------------
	}

	public JFrame getFrame() { return this.frame; }
	
}