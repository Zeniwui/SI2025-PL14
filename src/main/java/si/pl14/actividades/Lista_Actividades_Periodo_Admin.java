package si.pl14.actividades;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.Color;

public class Lista_Actividades_Periodo_Admin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tableResultados;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Lista_Actividades_Periodo_Admin frame = new Lista_Actividades_Periodo_Admin();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public Lista_Actividades_Periodo_Admin() {
		setTitle("Listado de Actividades Ofertadas");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// --- FILTRO DE PERIODO ---
		JLabel lblSeleccionarPeriodo = new JLabel("Seleccionar Periodo:");
		lblSeleccionarPeriodo.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSeleccionarPeriodo.setBounds(20, 20, 150, 25);
		contentPane.add(lblSeleccionarPeriodo);

		String[] periodos = { "Septiembre", "Enero", "Junio" };
		JComboBox<String> cbPeriodo = new JComboBox<>(periodos);
		cbPeriodo.setBounds(170, 20, 150, 25);
		contentPane.add(cbPeriodo);

		JButton btnConsultar = new JButton("Consultar");
		btnConsultar.setBounds(330, 20, 100, 25);
		contentPane.add(btnConsultar);

		// --- TABLA DE RESULTADOS ---
		// Definimos las columnas según tu requisito: Tipo, Duración, Fechas, Plazas y Precio
		String[] columnas = { "Tipo Actividad", "Duración", "Fecha Inicio", "Fecha Fin", "Nº Plazas", "Precio (€)" };
		
		// Datos de ejemplo para visualizar cómo quedaría
		Object[][] datosEjemplo = {
			{"Yoga", "1h", "01/09/26", "30/06/26", "20", "25.00"},
			{"Tenis", "1.5h", "15/09/26", "15/06/26", "4", "40.00"}
		};

		DefaultTableModel model = new DefaultTableModel(datosEjemplo, columnas);
		tableResultados = new JTable(model);
		
		JScrollPane scrollPane = new JScrollPane(tableResultados);
		scrollPane.setBounds(20, 70, 740, 320);
		contentPane.add(scrollPane);

		// --- BOTÓN CERRAR O EXPORTAR ---
		JButton btnVolver = new JButton("Volver");
		btnVolver.setBounds(660, 410, 100, 30);
		contentPane.add(btnVolver);
		
		// Separador visual para mantener el orden
		JSeparator separator = new JSeparator();
		separator.setBounds(20, 56, 740, 2);
		contentPane.add(separator);
	}
}
