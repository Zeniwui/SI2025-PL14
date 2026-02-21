package si.pl14.actividades;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.List;

public class Planificar_Actividad_Admin_View extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNombre = new JTextField();
	private JTextField txtAforo = new JTextField("20");
	private JTextField txtFechaInicio = new JTextField("2026-03-01");
	private JTextField txtFechaFin = new JTextField("2026-06-30");
	private JTextField txtCuotaSocio = new JTextField("15.50");
	private JTextField txtCuotaNoSocio = new JTextField("25.00");
	private JTextField txtHoraInicio = new JTextField("10:00");
	private JTextField txtHoraFin = new JTextField("11:30");
	private JLabel lblInfoFechas = new JLabel("Fechas: Seleccione un periodo");
	private JComboBox<String> cbDiaSemana = new JComboBox<>(
			new String[] { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" });
	private JButton btnCrearHorario = new JButton("AÑADIR HORARIO");
	private DefaultListModel<String> listModelHorarios = new DefaultListModel<>();
	private JList<String> listHorariosVista = new JList<>(listModelHorarios);
	private JTextArea txtrDescripcion = new JTextArea();
	private JButton btnCrear = new JButton("CREAR ACTIVIDAD");
	private JLabel lblDescripcion = new JLabel("Descripción:");
	private JScrollPane scrollDesc = new JScrollPane(txtrDescripcion);
	private JLabel lblFechas = new JLabel("Periodo de tiempo:");
	private JLabel lblPeriodo = new JLabel("Periodo Inscripción:");
	private JComboBox<String> cbPeriodoInscripcion = new JComboBox<>(
			new String[] { "Periodo de verano", "Periodo anual" });
	private JLabel lblNombre = new JLabel("Nombre de la Actividad:");
	private JPanel panelHorarios = new JPanel();
	private JLabel lblHorario = new JLabel("Planificar Horarios");
	private JLabel lblHIn = new JLabel("Desde:");
	private JLabel lblHOut = new JLabel("Hasta:");
	private JLabel lblInstalacion = new JLabel("Instalación:");
	private JScrollPane scrollHorarios = new JScrollPane(listHorariosVista);
	private JComboBox<String> cbInstalacion = new JComboBox<>(
			new String[] { "Pista Interior 1", "Pista Exterior", "Piscina" });
	private JLabel lblAforo = new JLabel("Aforo Máximo:");
	private JLabel lblTipo = new JLabel("Categoría:");
	private JComboBox<String> cbTipo = new JComboBox<>(new String[] { "Deporte", "Conferencia", "Competición" });
	private JPanel panelPrecios = new JPanel();
	private JLabel lblSocio = new JLabel("Socio:");
	private JLabel lblNoSocio = new JLabel("No Socio:");
	private JSeparator separator = new JSeparator();
	private JButton btnEliminarHorario = new JButton("ELIMINAR SELECCIONADO");

	public static void main(String[] args) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Planificar_Actividad_Admin_View vista = new Planificar_Actividad_Admin_View();
		Planificar_Actividad_Model modelo = new Planificar_Actividad_Model();
		Planificar_Actividad_Controller controlador = new Planificar_Actividad_Controller(modelo, vista);

		controlador.initController();
		vista.setVisible(true);
	}

	public Planificar_Actividad_Admin_View() {
		// Paleta de colores
		Color azul = new Color(40, 60, 85);
		Color fondo_gris = new Color(240, 242, 245);
		Color verde_oscuro = new Color(46, 82, 58);
		Color morado_seccion = new Color(242, 240, 250);
		Color morado_texto = new Color(90, 70, 160);
		Color morado_scroll = new Color(215, 210, 240);

		// Fuentes
		Font mainFont = new Font("Segoe UI", Font.PLAIN, 13);
		Font boldFont = new Font("Segoe UI", Font.BOLD, 13);
		Font titleFont = new Font("Segoe UI", Font.BOLD, 15);

		setTitle("Planificar Actividad - Administración");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 780, 580);

		contentPane = new JPanel();
		contentPane.setBackground(fondo_gris);
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// --- COLUMNA 1: DATOS BÁSICOS ---
		lblNombre.setFont(boldFont);
		lblNombre.setForeground(azul);
		lblNombre.setBounds(30, 20, 200, 20);
		contentPane.add(lblNombre);

		txtNombre.setFont(mainFont);
		txtNombre.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(2, 5, 2, 5)));
		txtNombre.setBounds(30, 45, 240, 30);
		contentPane.add(txtNombre);

		lblDescripcion.setFont(boldFont);
		lblDescripcion.setForeground(azul);
		lblDescripcion.setBounds(30, 90, 100, 20);
		contentPane.add(lblDescripcion);

		txtrDescripcion.setFont(mainFont);
		txtrDescripcion.setLineWrap(true);
		txtrDescripcion.setWrapStyleWord(true);

		scrollDesc.setBounds(30, 115, 240, 80);
		contentPane.add(scrollDesc);

		lblFechas.setFont(boldFont);
		lblFechas.setForeground(azul);
		lblFechas.setBounds(30, 210, 200, 20);
		contentPane.add(lblFechas);

		txtFechaInicio.setBounds(30, 235, 110, 30);
		txtFechaFin.setBounds(160, 235, 110, 30);
		contentPane.add(txtFechaInicio);
		contentPane.add(txtFechaFin);

		lblPeriodo.setFont(boldFont);
		lblPeriodo.setForeground(azul);
		lblPeriodo.setBounds(30, 280, 150, 20);
		contentPane.add(lblPeriodo);

		cbPeriodoInscripcion.setBounds(30, 305, 240, 30);
		contentPane.add(cbPeriodoInscripcion);

		lblInfoFechas.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		lblInfoFechas.setForeground(azul);
		lblInfoFechas.setBounds(30, 340, 450, 45);
		contentPane.add(lblInfoFechas);

		// --- COLUMNA 2: HORARIOS (BLOQUE DIFERENCIADO) ---
		panelHorarios.setBackground(morado_seccion);
		panelHorarios.setBorder(BorderFactory.createLineBorder(morado_texto, 1));
		panelHorarios.setBounds(290, 15, 230, 305);
		panelHorarios.setLayout(null);
		contentPane.add(panelHorarios);

		lblHorario.setFont(titleFont);
		lblHorario.setForeground(morado_texto);
		lblHorario.setBounds(15, 10, 180, 20);
		panelHorarios.add(lblHorario);

		cbDiaSemana.setBackground(Color.WHITE);
		cbDiaSemana.setBounds(15, 35, 200, 30);
		panelHorarios.add(cbDiaSemana);

		lblHIn.setFont(boldFont);
		lblHIn.setForeground(morado_texto);
		lblHIn.setBounds(15, 75, 80, 20);
		panelHorarios.add(lblHIn);

		txtHoraInicio.setBounds(15, 95, 90, 30);
		panelHorarios.add(txtHoraInicio);

		lblHOut.setFont(boldFont);
		lblHOut.setForeground(morado_texto);
		lblHOut.setBounds(125, 75, 80, 20);
		panelHorarios.add(lblHOut);

		txtHoraFin.setBounds(125, 95, 90, 30);
		panelHorarios.add(txtHoraFin);

		btnCrearHorario.setBackground(morado_texto);
		btnCrearHorario.setForeground(Color.WHITE);
		btnCrearHorario.setFont(boldFont);
		btnCrearHorario.setBounds(15, 135, 200, 35);
		panelHorarios.add(btnCrearHorario);

		listHorariosVista.setBackground(morado_scroll);
		listHorariosVista.setForeground(morado_texto);
		listHorariosVista.setSelectionBackground(morado_texto);
		listHorariosVista.setSelectionForeground(Color.WHITE); // cuando pinchas se pone blanco

		scrollHorarios.setBorder(BorderFactory.createLineBorder(morado_texto));
		scrollHorarios.getViewport().setBackground(morado_scroll);
		scrollHorarios.setBounds(15, 180, 200, 65);
		panelHorarios.add(scrollHorarios);
		
		btnEliminarHorario.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnEliminarHorario.setBackground(new Color(231, 76, 60));
		btnEliminarHorario.setForeground(Color.WHITE);
		btnEliminarHorario.setBounds(15, 255, 200, 30);
		panelHorarios.add(btnEliminarHorario);

		// fuera del cuadro morado
		lblInstalacion.setFont(boldFont);
		lblInstalacion.setForeground(azul);
		lblInstalacion.setBounds(290, 330, 120, 20);
		contentPane.add(lblInstalacion);

		cbInstalacion.setBackground(Color.WHITE);
		cbInstalacion.setBounds(290, 355, 230, 30);
		contentPane.add(cbInstalacion);

		// --- COLUMNA 3: AFORO Y COSTES ---
		lblAforo.setFont(boldFont);
		lblAforo.setForeground(azul);
		lblAforo.setBounds(550, 20, 120, 20);
		contentPane.add(lblAforo);

		txtAforo.setBounds(550, 45, 100, 30);
		contentPane.add(txtAforo);

		lblTipo.setFont(boldFont);
		lblTipo.setForeground(azul);
		lblTipo.setBounds(550, 90, 100, 20);
		contentPane.add(lblTipo);

		cbTipo.setBounds(550, 115, 150, 30);
		contentPane.add(cbTipo);

		panelPrecios.setBackground(new Color(228, 243, 233));
		panelPrecios.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(verde_oscuro),
				"Tarifas (€)", 0, 0, boldFont, verde_oscuro));
		panelPrecios.setBounds(540, 190, 180, 145);
		panelPrecios.setLayout(null);
		contentPane.add(panelPrecios);

		lblSocio.setForeground(verde_oscuro);
		lblSocio.setBounds(15, 25, 100, 20);
		panelPrecios.add(lblSocio);

		lblNoSocio.setForeground(verde_oscuro);
		lblNoSocio.setBounds(15, 80, 100, 20);
		panelPrecios.add(lblNoSocio);

		txtCuotaSocio.setBounds(15, 45, 140, 25);
		txtCuotaNoSocio.setBounds(15, 100, 140, 25);
		panelPrecios.add(txtCuotaSocio);
		panelPrecios.add(txtCuotaNoSocio);

		separator.setBounds(30, 390, 700, 2);
		contentPane.add(separator);

		btnCrear.setBackground(morado_texto);
		btnCrear.setForeground(Color.WHITE);
		btnCrear.setBounds(280, 400, 220, 55);
		contentPane.add(btnCrear);
	}

	// Métodos para rellenar los combos desde el controlador
	public void cargarPeriodos(List<Object[]> periodos) {
		cbPeriodoInscripcion.removeAllItems();
		for (Object[] p : periodos) {
			// Guardamos "ID - Nombre"
			cbPeriodoInscripcion.addItem(p[0] + " - " + p[1]);
		}
	}

	public void cargarInstalaciones(List<Object[]> instalaciones) {
		cbInstalacion.removeAllItems();
		for (Object[] i : instalaciones) {
			cbInstalacion.addItem(i[0] + " - " + i[1]);
		}
	}

	// Métodos para obtener el ID seleccionado (sacando el número del String "1 -
	// Piscina")
	public int getIdPeriodoSeleccionado() {
		String item = (String) cbPeriodoInscripcion.getSelectedItem();
		return (item == null) ? 0 : Integer.parseInt(item.split(" - ")[0]);
	}

	public int getIdInstalacionSeleccionada() {
		String item = (String) cbInstalacion.getSelectedItem();
		return (item == null) ? 0 : Integer.parseInt(item.split(" - ")[0]);
	}

	// --- GETTERS ---
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

	public String getDiaSemana() {
		return (String) cbDiaSemana.getSelectedItem();
	}

	public String getHoraInicio() {
		return txtHoraInicio.getText();
	}

	public String getHoraFin() {
		return txtHoraFin.getText();
	}

	public JButton getBtnCrear() {
		return btnCrear;
	}

	public JButton getBtnCrearHorario() {
		return btnCrearHorario;
	}

	public DefaultListModel<String> getListModelHorarios() {
		return listModelHorarios;
	}

	public void setTextoFechas(String texto) {
		lblInfoFechas.setText(texto);
	}

	public JComboBox<String> getCbPeriodoInscripcion() {
		return cbPeriodoInscripcion;
	}

	public JTextField getTxtFechaInicio() {
		return txtFechaInicio;
	}

	public JTextField getTxtFechaFin() {
		return txtFechaFin;
	}

	// Necesitamos un getter para el controlador
	public JButton getBtnEliminarHorario() {
		return btnEliminarHorario;
	}

	// Necesitamos saber qué índice hay seleccionado en la JList
	public int getIndiceHorarioSeleccionado() {
		return listHorariosVista.getSelectedIndex();
	}
}