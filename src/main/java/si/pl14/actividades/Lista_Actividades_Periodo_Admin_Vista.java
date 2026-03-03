package si.pl14.actividades;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import si.pl14.util.DatabaseViewer;

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
		
		EventQueue.invokeLater(() -> {
			try {
				// MVC
				Lista_Actividades_Periodo_Admin_Vista vista = new Lista_Actividades_Periodo_Admin_Vista();
				Lista_Actividades_Periodo_Model modelo = new Lista_Actividades_Periodo_Model();
				Lista_Actividades_Periodo_Controller controlador = new Lista_Actividades_Periodo_Controller(modelo,
						vista);
				DatabaseViewer frame = new DatabaseViewer();
				frame.setVisible(true);

				// iniciar controlador
				controlador.initController();

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public Lista_Actividades_Periodo_Admin_Vista() {
		// --- COLORES ---
		Color bg_window = new Color(241, 245, 249); 
		Color bg_card = Color.WHITE;
		Color primary_indigo = new Color(79, 70, 229); 
		Color text_dark = new Color(15, 23, 42); 
		Color text_muted = new Color(100, 116, 139); 
		Color border_ui = new Color(226, 232, 240); 
		Color table_header_bg = new Color(30, 41, 59);

		setTitle("Administración - Gestión de Actividades");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1350, 800);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBackground(bg_window);
		contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));
		contentPane.setLayout(new BorderLayout(0, 25));
		setContentPane(contentPane);

		// --- PANEL SUPERIOR ---
		JPanel northPanel = new JPanel(new BorderLayout(0, 15));
		northPanel.setOpaque(false);

		JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 5));
		titleGroup.setOpaque(false);
		JLabel lblTitulo = new JLabel("Actividades Ofertadas");
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
		lblTitulo.setForeground(text_dark);
		JLabel lblSub = new JLabel("Consulta y gestiona las actividades programadas para cada periodo lectivo.");
		lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblSub.setForeground(text_muted);
		titleGroup.add(lblTitulo);
		titleGroup.add(lblSub);
		northPanel.add(titleGroup, BorderLayout.NORTH);

		// Tarjeta de Filtros
		JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
		filterCard.setBackground(bg_card);
		filterCard.setBorder(BorderFactory.createCompoundBorder(
				new LineBorder(border_ui, 1, true), 
				new EmptyBorder(10, 15, 10, 15)));

		JLabel lblPeriodoLabel = new JLabel("Periodo lectivo:");
		lblPeriodoLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		lblPeriodoLabel.setForeground(text_dark);
		filterCard.add(lblPeriodoLabel);

		cbPeriodo.setPreferredSize(new Dimension(320, 40));
		cbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		filterCard.add(cbPeriodo);

		// --- BOTÓN CONSULTAR ---
		btnConsultar.setPreferredSize(new Dimension(160, 42));
		btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnConsultar.setBackground(primary_indigo);
		btnConsultar.setForeground(Color.WHITE);
		btnConsultar.setFocusPainted(false);
		filterCard.add(Box.createRigidArea(new Dimension(10, 0)));
		filterCard.add(btnConsultar);

		northPanel.add(filterCard, BorderLayout.CENTER);
		contentPane.add(northPanel, BorderLayout.NORTH);

		// --- PANEL CENTRAL (ES LA TABLA) ---
		String[] columnas = { "Nombre", "Tipo", "Instalación", "Duración", "Horarios", "F. Inicio", "F. Fin", "Plazas", "Socio", "General" };
		modelTabla = new DefaultTableModel(null, columnas) {
			@Override public boolean isCellEditable(int r, int c) { return false; }
		};

		tableResultados = new JTable(modelTabla);
		tableResultados.setRowHeight(48);
		tableResultados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tableResultados.setSelectionBackground(new Color(238, 242, 255));
		tableResultados.setSelectionForeground(primary_indigo);
		tableResultados.setGridColor(new Color(241, 245, 249));
		tableResultados.setShowVerticalLines(false);

		tableResultados.getTableHeader().setPreferredSize(new Dimension(0, 45));
		tableResultados.getTableHeader().setBackground(table_header_bg);
		tableResultados.getTableHeader().setForeground(Color.WHITE);
		tableResultados.getTableHeader().setFont(new Font("Segoe UI Bold", Font.BOLD, 13));
		tableResultados.getTableHeader().setOpaque(false);
		
		tableResultados.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int[] anchos = { 200, 100, 180, 90, 220, 100, 100, 70, 90, 90 };
		for (int i = 0; i < anchos.length; i++) {
			tableResultados.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
		}

		JScrollPane scrollPane = new JScrollPane(tableResultados);
		scrollPane.getViewport().setBackground(bg_card);
		scrollPane.setBorder(new LineBorder(border_ui, 1));
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// --- PANEL INFERIOR ---
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		southPanel.setOpaque(false);

		btnVolver.setPreferredSize(new Dimension(130, 40));
		btnVolver.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		btnVolver.setBackground(Color.WHITE);
		btnVolver.setForeground(text_muted);
		btnVolver.setFocusPainted(false);
		btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnVolver.setBorder(BorderFactory.createCompoundBorder(
				new LineBorder(border_ui, 1), 
				new EmptyBorder(0, 15, 0, 15)));

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