package si.pl14.actividades;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
//import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Font;

public class Planificar_Actividad_Admin extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNombre;
    private JTextField txtAforo;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtCuotaSocio;
    private JTextField txtCuotaNoSocio;
    private JTable tableHorario;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Planificar_Actividad_Admin frame = new Planificar_Actividad_Admin();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Planificar_Actividad_Admin() {
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

        String[] columnas = {"L", "M", "X", "J", "V", "S", "D"};
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

        txtFechaInicio = new JTextField("10/06/24");
        txtFechaInicio.setBounds(20, 270, 80, 25);
        contentPane.add(txtFechaInicio);

        txtFechaFin = new JTextField("30/08/24");
        txtFechaFin.setBounds(110, 270, 80, 25);
        contentPane.add(txtFechaFin);

        JComboBox<String> cbPeriodoInscripcion = new JComboBox<>(new String[]{"Periodo de verano", "Periodo anual"});
        cbPeriodoInscripcion.setBounds(20, 310, 170, 25);
        contentPane.add(cbPeriodoInscripcion);

        // --- TIPO ACTIVIDAD E INSTALACIÓN ---
        JLabel lblTipo = new JLabel("Tipo Actividad:");
        lblTipo.setBounds(220, 250, 120, 14);
        contentPane.add(lblTipo);

        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Deporte", "Conferencia", "Competición"});
        cbTipo.setBounds(220, 270, 150, 25);
        contentPane.add(cbTipo);

        JLabel lblInstalacion = new JLabel("Instalación:");
        lblInstalacion.setBounds(220, 310, 120, 14);
        contentPane.add(lblInstalacion);

        JComboBox<String> cbInstalacion = new JComboBox<>(new String[]{"Pista Interior 1", "Pista Exterior", "Piscina"});
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
        JButton btnCrear = new JButton("CREAR");
        btnCrear.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnCrear.setBackground(new Color(200, 200, 200));
        btnCrear.setBounds(250, 450, 150, 40);
        contentPane.add(btnCrear);
    }
}
