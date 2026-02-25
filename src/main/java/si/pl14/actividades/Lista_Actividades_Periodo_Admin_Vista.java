package si.pl14.actividades;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
	    Color pastel_blue = new Color(214, 230, 242);
	    Color pastel_purple = new Color(230, 230, 250);
	    Color soft_gray = new Color(245, 247, 250);
	    Color border_color = new Color(210, 215, 223);
	    Color dark_text = new Color(70, 80, 95);
	    Color white = Color.WHITE;

	    setTitle("Gestión de Actividades - Panel Administrativo");
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setSize(1300, 750);
	    setLocationRelativeTo(null);

	    contentPane = new JPanel();
	    contentPane.setBackground(soft_gray);
	    contentPane.setBorder(new EmptyBorder(25, 25, 25, 25));
	    setContentPane(contentPane);
	    contentPane.setLayout(new BorderLayout(0, 20));

	    JPanel northPanel = new JPanel(new BorderLayout());
	    northPanel.setOpaque(false);

	    JLabel lblTitulo = new JLabel("Actividades Ofertadas");
	    lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
	    lblTitulo.setForeground(dark_text);
	    northPanel.add(lblTitulo, BorderLayout.NORTH);

	    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
	    filterPanel.setOpaque(false);

	    JLabel lblSeleccionarPeriodo = new JLabel("Periodo lectivo:");
	    lblSeleccionarPeriodo.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
	    lblSeleccionarPeriodo.setForeground(dark_text);
	    filterPanel.add(lblSeleccionarPeriodo);

	    cbPeriodo.setPreferredSize(new Dimension(280, 35));
	    cbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	    cbPeriodo.setBackground(white);
	    filterPanel.add(cbPeriodo);

	    btnConsultar.setPreferredSize(new Dimension(140, 38));
	    btnConsultar.setFont(new Font("Segoe UI Bold", Font.BOLD, 13));
	    btnConsultar.setBackground(pastel_purple);
	    btnConsultar.setForeground(dark_text);
	    btnConsultar.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    btnConsultar.setFocusPainted(false);
	    btnConsultar.setBorder(new LineBorder(border_color, 1));
	    filterPanel.add(btnConsultar);

	    northPanel.add(filterPanel, BorderLayout.CENTER);
	    contentPane.add(northPanel, BorderLayout.NORTH);

	    String[] columnas = { "Nombre", "Tipo", "Instalación", "Duración", "Horarios", "F. Inicio", "F. Fin", "Plazas", "Socio", "No socio" };
	    modelTabla = new DefaultTableModel(null, columnas) {
	        @Override
	        public boolean isCellEditable(int r, int c) {
	            return false;
	        }
	    };

	    tableResultados = new JTable(modelTabla);
	    tableResultados.setRowHeight(40);
	    tableResultados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
	    tableResultados.setSelectionBackground(pastel_blue);
	    tableResultados.setSelectionForeground(dark_text);
	    tableResultados.setGridColor(soft_gray);
	    tableResultados.setShowVerticalLines(true);

	    tableResultados.getTableHeader().setPreferredSize(new Dimension(0, 40));
	    tableResultados.getTableHeader().setBackground(pastel_blue);
	    tableResultados.getTableHeader().setForeground(dark_text);
	    tableResultados.getTableHeader().setFont(new Font("Segoe UI Bold", Font.BOLD, 13));
	    tableResultados.getTableHeader().setReorderingAllowed(false);

	    tableResultados.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    int[] anchos = { 200, 100, 180, 90, 220, 100, 100, 70, 85, 85 };
	    for (int i = 0; i < columnas.length; i++) {
	        tableResultados.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
	    }

	    JScrollPane scrollPane = new JScrollPane(tableResultados);
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    scrollPane.getViewport().setBackground(white);
	    scrollPane.setBorder(new LineBorder(border_color, 1));
	    contentPane.add(scrollPane, BorderLayout.CENTER);

	    JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    southPanel.setOpaque(false);

	    btnVolver.setPreferredSize(new Dimension(110, 35));
	    btnVolver.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
	    btnVolver.setBackground(white);
	    btnVolver.setForeground(dark_text);
	    btnVolver.setFocusPainted(false);
	    btnVolver.setBorder(new LineBorder(border_color, 1));
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