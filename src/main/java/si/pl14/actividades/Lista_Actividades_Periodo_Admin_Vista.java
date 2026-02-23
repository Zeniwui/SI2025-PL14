package si.pl14.actividades;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class Lista_Actividades_Periodo_Admin_Vista extends JFrame {
	private JPanel contentPane;
	private JTable tableResultados;
	private DefaultTableModel modelTabla;
	private JComboBox<String> cbPeriodo = new JComboBox<>();
	private JButton btnConsultar = new JButton("Consultar");
	private JButton btnVolver = new JButton("Volver");

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				// MVC
				Lista_Actividades_Periodo_Admin_Vista vista = new Lista_Actividades_Periodo_Admin_Vista();
				Lista_Actividades_Periodo_Model modelo = new Lista_Actividades_Periodo_Model();
				Lista_Actividades_Periodo_Controller controlador = new Lista_Actividades_Periodo_Controller(modelo,
						vista);

				// iniciar controlador
				controlador.initController();

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	
	public Lista_Actividades_Periodo_Admin_Vista() {
		// Colores
		Color azul_soft = new Color(132, 156, 189);     
		Color indigo_medio = new Color(139, 141, 214);   
		Color fondo_app = new Color(248, 250, 252);     
		Color gris_borde = new Color(226, 232, 240);    
		Color blanco_puro = Color.WHITE;
		Color texto_principal = new Color(30, 41, 59);  

		setTitle("Panel de Administración - Gestión de Actividades");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1300, 750);
		setLocationRelativeTo(null); // Centrar en pantalla

		contentPane = new JPanel();
		contentPane.setBackground(fondo_app);
		contentPane.setBorder(new EmptyBorder(30, 30, 30, 30));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 20));

		// --- PANEL SUPERIOR ---
		JPanel northPanel = new JPanel();
		northPanel.setOpaque(false);
		northPanel.setLayout(new BorderLayout());

		JLabel lblTitulo = new JLabel("Actividades Ofertadas");
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
		lblTitulo.setForeground(indigo_medio);
		northPanel.add(lblTitulo, BorderLayout.NORTH);

		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
		filterPanel.setOpaque(false);

		JLabel lblSeleccionarPeriodo = new JLabel("Periodo lectivo:");
		lblSeleccionarPeriodo.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
		lblSeleccionarPeriodo.setForeground(new Color(100, 100, 100));
		filterPanel.add(lblSeleccionarPeriodo);

		cbPeriodo.setPreferredSize(new Dimension(300, 35));
		cbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		cbPeriodo.setBackground(blanco_puro);
		filterPanel.add(cbPeriodo);

		// Botón Consultar
		btnConsultar.setPreferredSize(new Dimension(150, 40));
		btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnConsultar.setBackground(indigo_medio);
		btnConsultar.setForeground(blanco_puro);
		btnConsultar.setBorder(BorderFactory.createLineBorder(new Color(79, 70, 229), 1));
		btnConsultar.setOpaque(true);
		btnConsultar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnConsultar.setFocusPainted(false);
		filterPanel.add(btnConsultar);

		northPanel.add(filterPanel, BorderLayout.CENTER);
		contentPane.add(northPanel, BorderLayout.NORTH);

		// --- PANEL CENTRAL ---
		String[] columnas = { "Nombre", "Tipo", "Duración", "Horarios", "F. Inicio", "F. Fin", "Plazas", "Precio Socio",
				"Precio No Socio" };
		modelTabla = new DefaultTableModel(null, columnas) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tableResultados = new JTable(modelTabla);
		tableResultados.setRowHeight(45);
		tableResultados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tableResultados.setSelectionBackground(new Color(238, 242, 255));
		tableResultados.setSelectionForeground(texto_principal);
		tableResultados.setShowVerticalLines(false);
		tableResultados.setGridColor(new Color(230, 230, 230));

		// Header
		tableResultados.getTableHeader().setPreferredSize(new Dimension(0, 45));
		tableResultados.getTableHeader().setBackground(azul_soft);
		tableResultados.getTableHeader().setForeground(blanco_puro);
		tableResultados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		tableResultados.getTableHeader().setReorderingAllowed(false);

		tableResultados.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		int[] anchos = { 220, 100, 90, 250, 100, 100, 70, 90, 90 };
		for (int i = 0; i < anchos.length; i++) {
			tableResultados.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
		}

		JScrollPane scrollPane = new JScrollPane(tableResultados);
		scrollPane.getViewport().setBackground(blanco_puro);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// --- PANEL INFERIOR ---
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		southPanel.setOpaque(false);

		btnVolver.setPreferredSize(new Dimension(120, 40));
		btnVolver.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		btnVolver.setBackground(blanco_puro);
		btnVolver.setForeground(azul_soft);
		btnVolver.setBorder(BorderFactory.createLineBorder(gris_borde, 2));
		btnVolver.setFocusPainted(false);
		btnVolver.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
		southPanel.add(btnVolver);

		contentPane.add(southPanel, BorderLayout.SOUTH);
	}

	// Métodos para el Controlador
	public void cargarPeriodos(List<Object[]> periodos) {
		cbPeriodo.removeAllItems();
		for (Object[] p : periodos)
			cbPeriodo.addItem(p[0] + " - " + p[1]);
	}

	public int getIdPeriodoSeleccionado() {
		String item = (String) cbPeriodo.getSelectedItem();
		return (item == null) ? -1 : Integer.parseInt(item.split(" - ")[0]);
	}

	public void actualizarTabla(List<Object[]> datos) {
		modelTabla.setRowCount(0);
		for (Object[] fila : datos)
			modelTabla.addRow(fila);
	}

	public JButton getBtnConsultar() {
		return btnConsultar;
	}

	public JButton getBtnVolver() {
		return btnVolver;
	}
}