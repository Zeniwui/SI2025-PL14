package si.pl14.actividades;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
//import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Font;

// View
public class Planificar_Actividad_Admin_View extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane = new JPanel();
	private JTextField txtNombre = new JTextField();
	private JTextField txtAforo = new JTextField();
	private JTextField txtFechaInicio = new JTextField();
	private JTextField txtFechaFin = new JTextField();
	private JTextField txtCuotaSocio = new JTextField();
	private JTextField txtCuotaNoSocio = new JTextField();
	private JTable tableHorario = new JTable();
	private JTextArea txtrDescripcion = new JTextArea();
	private JButton btnCrear = new JButton("CREAR"); //fuera del main para ver si ahora funciona

	public static void main(String[] args) {
		/*
		 * EventQueue.invokeLater(() -> { try { Planificar_Actividad_Admin_View frame =
		 * new Planificar_Actividad_Admin_View(); frame.setVisible(true); } catch
		 * (Exception e) { e.printStackTrace(); } });
		 */

		// Cambio el main a ver si ahora funciona

		// MVC
		Planificar_Actividad_Admin_View vista = new Planificar_Actividad_Admin_View();
		Planificar_Actividad_Model modelo = new Planificar_Actividad_Model();
		Planificar_Actividad_Controller controlador = new Planificar_Actividad_Controller(modelo, vista);

		// inicio controlador con el metofo
		controlador.initController();

		// hago visible aunque sea redundante
		vista.setVisible(true);
	}

	public Planificar_Actividad_Admin_View() {
		setTitle("Planificar Actividad - Admin");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null); // Layout absoluto para replicar el dibujo

		// --- SECCIÓN IZQUIERDA: NOMBRE Y DESCRIPCIÓN ---
		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(20, 20, 80, 14);
		contentPane.add(lblNombre);

		txtNombre = new JTextField();
		txtNombre.setBounds(20, 40, 250, 25);
		contentPane.add(txtNombre);

		JLabel lblDescripcion = new JLabel("Descripción:");
		lblDescripcion.setBounds(20, 75, 100, 14);
		contentPane.add(lblDescripcion);

		JTextArea txtrDescripcion = new JTextArea();
		txtrDescripcion.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		txtrDescripcion.setBounds(20, 95, 250, 100);
		contentPane.add(txtrDescripcion);

		// --- SECCIÓN DERECHA: HORARIO (GRID) ---
		JLabel lblHorario = new JLabel("Horario:");
		lblHorario.setBounds(300, 20, 80, 14);
		contentPane.add(lblHorario);

		String[] columnas = { "L", "M", "X", "J", "V", "S", "D" };
		Object[][] datos = new Object[5][7]; // Filas de horas
		tableHorario = new JTable(new DefaultTableModel(datos, columnas));
		tableHorario.setRowHeight(25);
		JScrollPane scrollPaneTable = new JScrollPane(tableHorario);
		scrollPaneTable.setBounds(300, 40, 350, 155);
		contentPane.add(scrollPaneTable);

		// --- AFORO ---
		JLabel lblAforo = new JLabel("Aforo:");
		lblAforo.setBounds(300, 210, 60, 14);
		contentPane.add(lblAforo);

		txtAforo = new JTextField();
		txtAforo.setText("15");
		txtAforo.setBounds(350, 205, 50, 25);
		contentPane.add(txtAforo);

		// --- PERIODO DE TIEMPO ---
		JLabel lblPeriodo = new JLabel("Periodo tiempo:");
		lblPeriodo.setBounds(20, 250, 120, 14);
		contentPane.add(lblPeriodo);

		txtFechaInicio = new JTextField("AAAA-MM-DD");
		txtFechaInicio.setBounds(20, 270, 120, 25);
		contentPane.add(txtFechaInicio);

		txtFechaFin = new JTextField("AAAA-MM-DD");
		txtFechaFin.setBounds(20, 300, 120, 25);
		contentPane.add(txtFechaFin);

		JComboBox<String> cbPeriodoInscripcion = new JComboBox<>(new String[] { "Periodo de verano", "Periodo anual" });
		cbPeriodoInscripcion.setBounds(20, 330, 170, 25);
		contentPane.add(cbPeriodoInscripcion);

		// --- TIPO ACTIVIDAD E INSTALACIÓN ---
		JLabel lblTipo = new JLabel("Tipo Actividad:");
		lblTipo.setBounds(220, 250, 120, 14);
		contentPane.add(lblTipo);

		JComboBox<String> cbTipo = new JComboBox<>(new String[] { "Deporte", "Conferencia", "Competición" });
		cbTipo.setBounds(220, 270, 150, 25);
		contentPane.add(cbTipo);

		JLabel lblInstalacion = new JLabel("Instalación:");
		lblInstalacion.setBounds(220, 310, 120, 14);
		contentPane.add(lblInstalacion);

		JComboBox<String> cbInstalacion = new JComboBox<>(
				new String[] { "Pista Interior 1", "Pista Exterior", "Piscina" });
		cbInstalacion.setBounds(220, 330, 150, 25);
		contentPane.add(cbInstalacion);

		// --- CUOTAS MENSUALES ---
		JLabel lblCuotaSocio = new JLabel("Cuota mensual SOCIO:");
		lblCuotaSocio.setBounds(400, 250, 150, 14);
		contentPane.add(lblCuotaSocio);

		txtCuotaSocio = new JTextField("10.00");
		txtCuotaSocio.setBounds(400, 270, 60, 25);
		contentPane.add(txtCuotaSocio);
		contentPane.add(new JLabel("€/mes")).setBounds(470, 270, 50, 25);

		JLabel lblCuotaNoSocio = new JLabel("Cuota NO SOCIO:");
		lblCuotaNoSocio.setBounds(400, 310, 150, 14);
		contentPane.add(lblCuotaNoSocio);

		txtCuotaNoSocio = new JTextField("10.00");
		txtCuotaNoSocio.setBounds(400, 330, 60, 25);
		contentPane.add(txtCuotaNoSocio);
		contentPane.add(new JLabel("€/mes")).setBounds(470, 330, 50, 25);

		// --- BOTÓN CREAR ---
		//JButton btnCrear = new JButton("CREAR");
		btnCrear.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnCrear.setBackground(new Color(200, 200, 200));
		btnCrear.setBounds(250, 450, 150, 40);
		contentPane.add(btnCrear);
	}

	// Métodos
	public String getNombre() {
		return txtNombre.getText();
	}

	public String getDescripcion() {
		return txtrDescripcion.getText();
	}

	public String getAforo() {
		return txtAforo.getText();
	}

	public String getFechaInicio() {
		return txtFechaInicio.getText();
	}

	public String getFechaFin() {
		return txtFechaFin.getText();
	}

	public String getPrecioSocio() {
		return txtCuotaSocio.getText();
	}

	public String getPrecioNoSocio() {
		return txtCuotaNoSocio.getText();
	}

	public JButton getBtnCrear() {
		return btnCrear;
	}
}
