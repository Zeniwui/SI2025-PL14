package si.pl14.contabilidadSocios;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista Swing para la HU "Calcular contabilidad de socios en un mes".
 *
 * Contiene:
 *  - Selectores de mes y año + botón Calcular
 *  - Tabla con resultados (nombre+DNI, coste reservas, coste actividades, deuda)
 *  - Fila de TOTALES al pie de la tabla
 *  - Botón "Guardar fichero" + etiqueta de estado
 */
public class ContabilidadSociosView extends JFrame {

    private static final long serialVersionUID = 1L;

    // Controles de selección
    private JComboBox<String>  cmbMes;
    private JComboBox<Integer> cmbAnio;
    private JButton            btnCalcular;
    private JLabel             lblResultado;

    // Tabla de resultados
    private JTable             tabla;
    private DefaultTableModel  modeloTabla;

    // Fila de totales
    private JLabel lblTotalSocios;
    private JLabel lblTotalReservas;
    private JLabel lblTotalActividades;
    private JLabel lblTotalDeuda;

    // Controles inferiores
    private JButton btnGuardar;
    private JLabel  lblEstado;

    // ─────────────────────────────────────────────────────────────────────────

    public ContabilidadSociosView() {
        super("Contabilidad de socios para un mes");
        construirUI();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Construcción de la UI
    // ─────────────────────────────────────────────────────────────────────────

    private void construirUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(860, 500);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        root.add(crearPanelSuperior(), BorderLayout.NORTH);
        root.add(crearPanelCentral(),  BorderLayout.CENTER);
        root.add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setBorder(BorderFactory.createEtchedBorder());

        p.add(new JLabel("Selecciona:"));

        String[] meses = {
            "Enero","Febrero","Marzo","Abril","Mayo","Junio",
            "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
        };
        cmbMes = new JComboBox<>(meses);
        cmbMes.setPreferredSize(new Dimension(130, 26));
        p.add(cmbMes);

        cmbAnio = new JComboBox<>();
        int hoy = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int a = hoy - 4; a <= hoy + 1; a++) cmbAnio.addItem(a);
        cmbAnio.setSelectedItem(hoy);
        cmbAnio.setPreferredSize(new Dimension(80, 26));
        p.add(cmbAnio);

        btnCalcular = new JButton("Calcular");
        btnCalcular.setFont(btnCalcular.getFont().deriveFont(Font.BOLD));
        p.add(btnCalcular);

        p.add(Box.createHorizontalStrut(16));
        p.add(new JLabel("Resultado:"));
        lblResultado = new JLabel("–");
        lblResultado.setFont(lblResultado.getFont().deriveFont(Font.BOLD, 12f));
        lblResultado.setForeground(new Color(0, 80, 160));
        p.add(lblResultado);

        return p;
    }

    private JPanel crearPanelCentral() {
        JPanel p = new JPanel(new BorderLayout(0, 4));

        // Tabla
        String[] cols = { "Socio (nombre · DNI)", "Coste Reservas (€)", "Coste Actividades (€)", "Deuda Total (€)" };
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD));

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 1; i <= 3; i++) tabla.getColumnModel().getColumn(i).setCellRenderer(right);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new TitledBorder("Contabilidad del mes"));
        p.add(scroll, BorderLayout.CENTER);

        // Fila de totales
        JPanel fila = new JPanel(new GridLayout(1, 4, 2, 0));
        fila.setBorder(new MatteBorder(2, 0, 0, 0, Color.DARK_GRAY));
        fila.setBackground(new Color(230, 230, 230));

        lblTotalSocios      = lblTotal("Total socios: –");
        lblTotalReservas    = lblTotal("Total: –");
        lblTotalActividades = lblTotal("Total: –");
        lblTotalDeuda       = lblTotal("Total: –");

        fila.add(lblTotalSocios);
        fila.add(lblTotalReservas);
        fila.add(lblTotalActividades);
        fila.add(lblTotalDeuda);

        p.add(fila, BorderLayout.SOUTH);
        return p;
    }

    private JLabel lblTotal(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 11f));
        l.setBorder(new EmptyBorder(4, 4, 4, 4));
        return l;
    }

    private JPanel crearPanelInferior() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBorder(new EmptyBorder(6, 0, 0, 0));

        btnGuardar = new JButton("Guardar fichero");
        btnGuardar.setEnabled(false);
        p.add(btnGuardar, BorderLayout.WEST);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.ITALIC));
        p.add(lblEstado, BorderLayout.CENTER);

        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos de actualización llamados desde el Controller
    // ─────────────────────────────────────────────────────────────────────────

    public void mostrarResultados(List<ContabilidadSocioDTO> lista, int mes, int anio) {
        modeloTabla.setRowCount(0);

        String[] nombres = {
            "Enero","Febrero","Marzo","Abril","Mayo","Junio",
            "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
        };
        lblResultado.setText(nombres[mes - 1] + " de " + anio);

        if (lista == null || lista.isEmpty()) {
            actualizarTotales(0, 0, 0, 0);
            setEstado("No hay reservas de socios en el mes seleccionado. No se generará fichero.", new Color(160, 80, 0));
            btnGuardar.setEnabled(false);
            return;
        }

        double sumRes = 0, sumAct = 0, sumTot = 0;
        for (ContabilidadSocioDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                dto.getNombre() + " " + dto.getApellidos() + " · " + dto.getDni(),
                String.format("%.2f", dto.getTotalReservas()),
                String.format("%.2f", dto.getTotalActividades()),
                String.format("%.2f", dto.getTotalDeuda())
            });
            sumRes += dto.getTotalReservas();
            sumAct += dto.getTotalActividades();
            sumTot += dto.getTotalDeuda();
        }

        actualizarTotales(lista.size(), sumRes, sumAct, sumTot);
        setEstado("Cálculo completado. Pulse 'Guardar fichero' para exportar.", new Color(30, 110, 30));
        btnGuardar.setEnabled(true);
    }

    private void actualizarTotales(int n, double res, double act, double tot) {
        lblTotalSocios     .setText("Total socios: " + n);
        lblTotalReservas   .setText(String.format("Total: %.2f €", res));
        lblTotalActividades.setText(String.format("Total: %.2f €", act));
        lblTotalDeuda      .setText(String.format("Total: %.2f €", tot));
    }

    public void mostrarMensajeFichero(String ruta) {
        if (ruta != null) {
            setEstado("Fichero guardado: " + ruta, new Color(30, 110, 30));
        } else {
            setEstado("No se generó fichero (sin datos).", new Color(160, 80, 0));
        }
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setEstado(String msg, Color color) {
        lblEstado.setText(msg);
        lblEstado.setForeground(color);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Getters para el Controller
    // ─────────────────────────────────────────────────────────────────────────

    public int     getMesSeleccionado()  { return cmbMes.getSelectedIndex() + 1;          }
    public int     getAnioSeleccionado() { return (Integer) cmbAnio.getSelectedItem();     }
    public JButton getBtnCalcular()      { return btnCalcular;                             }
    public JButton getBtnGuardar()       { return btnGuardar;                              }
}