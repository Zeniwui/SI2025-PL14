package si.pl14.contabilidadSocios;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista Swing para la HU "Calcular contabilidad de socios en un mes".
 * Muestra:
 *   - Selectores de mes y año
 *   - Botón "Calcular"
 *   - Tabla con resultados (nombre, DNI, total reservas, total actividades, deuda)
 *   - Fila de totales al pie
 *   - Botón "Guardar fichero"
 *   - Etiqueta de estado con la ruta del fichero generado
 */
public class ContabilidadSociosView extends JFrame {

    private static final long serialVersionUID = 1L;

    // ── Controles superiores ─────────────────────────────────────────────────
    private JComboBox<String> cmbMes;
    private JComboBox<Integer> cmbAnio;
    private JButton btnCalcular;

    // ── Tabla de resultados ──────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // ── Panel de totales ─────────────────────────────────────────────────────
    private JLabel lblTotalSocios;
    private JLabel lblTotalReservas;
    private JLabel lblTotalActividades;
    private JLabel lblTotalDeuda;

    // ── Controles inferiores ─────────────────────────────────────────────────
    private JButton btnGuardar;
    private JLabel  lblEstado;

    // ── Etiqueta de resultado actual ─────────────────────────────────────────
    private JLabel lblResultado;

    // ─────────────────────────────────────────────────────────────────────────
    public ContabilidadSociosView() {
        super("Contabilidad de socios para un mes");
        construirUI();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Construcción de la interfaz
    // ─────────────────────────────────────────────────────────────────────────

    private void construirUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 540);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel panelPrincipal = new JPanel(new BorderLayout(8, 8));
        panelPrincipal.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(panelPrincipal);

        panelPrincipal.add(crearPanelSuperior(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelCentral(),  BorderLayout.CENTER);
        panelPrincipal.add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    /** Panel con los selectores de mes/año y el botón calcular */
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.setBorder(BorderFactory.createEtchedBorder());

        panel.add(new JLabel("Selecciona:"));

        // Meses
        String[] meses = {
            "Enero","Febrero","Marzo","Abril","Mayo","Junio",
            "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
        };
        cmbMes = new JComboBox<>(meses);
        cmbMes.setPreferredSize(new Dimension(130, 26));
        panel.add(cmbMes);

        // Años: últimos 5 y próximo
        cmbAnio = new JComboBox<>();
        int anioActual = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int a = anioActual - 4; a <= anioActual + 1; a++) {
            cmbAnio.addItem(a);
        }
        cmbAnio.setSelectedItem(anioActual);
        cmbAnio.setPreferredSize(new Dimension(80, 26));
        panel.add(cmbAnio);

        btnCalcular = new JButton("Calcular");
        btnCalcular.setFont(btnCalcular.getFont().deriveFont(Font.BOLD));
        panel.add(btnCalcular);

        // Etiqueta de resultado seleccionado
        lblResultado = new JLabel("");
        lblResultado.setFont(lblResultado.getFont().deriveFont(Font.BOLD, 12f));
        lblResultado.setForeground(new Color(0, 90, 160));
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Resultado:"));
        panel.add(lblResultado);

        return panel;
    }

    /** Panel central con la tabla de resultados y los totales */
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));

        // ── Tabla ────────────────────────────────────────────────────────────
        String[] columnas = {
            "Socio (nombre, DNI)",
            "Total Coste Reservas",
            "Total Coste Actividades",
            "Deuda (Total)"
        };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD));

        // Alineación numérica en columnas 1, 2, 3
        javax.swing.table.DefaultTableCellRenderer renderDerecha =
            new javax.swing.table.DefaultTableCellRenderer();
        renderDerecha.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 1; i <= 3; i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderDerecha);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new TitledBorder("Contabilidad del mes"));
        panel.add(scroll, BorderLayout.CENTER);

        // ── Fila de totales ──────────────────────────────────────────────────
        JPanel panelTotales = new JPanel(new GridLayout(1, 4, 4, 0));
        panelTotales.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.DARK_GRAY));

        lblTotalSocios      = crearLblTotal("Total socios: -");
        lblTotalReservas    = crearLblTotal("Total: -");
        lblTotalActividades = crearLblTotal("Total: -");
        lblTotalDeuda       = crearLblTotal("Total: -");

        panelTotales.add(lblTotalSocios);
        panelTotales.add(lblTotalReservas);
        panelTotales.add(lblTotalActividades);
        panelTotales.add(lblTotalDeuda);

        panel.add(panelTotales, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel crearLblTotal(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 11f));
        lbl.setBorder(new EmptyBorder(4, 6, 4, 6));
        return lbl;
    }

    /** Panel inferior con el botón de guardar y la etiqueta de estado */
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(new EmptyBorder(6, 0, 0, 0));

        btnGuardar = new JButton("Guardar fichero");
        btnGuardar.setEnabled(false);
        panel.add(btnGuardar, BorderLayout.WEST);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.ITALIC));
        lblEstado.setForeground(new Color(40, 120, 40));
        panel.add(lblEstado, BorderLayout.CENTER);

        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos de actualización de la UI (llamados desde el Controller)
    // ─────────────────────────────────────────────────────────────────────────

    /** Rellena la tabla con la lista de DTOs y actualiza los totales */
    public void mostrarResultados(List<ContabilidadSocioDTO> lista, int mes, int anio) {
        // Limpiar tabla
        modeloTabla.setRowCount(0);

        // Nombre del mes en español
        String[] nombresMes = {
            "Enero","Febrero","Marzo","Abril","Mayo","Junio",
            "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
        };
        lblResultado.setText(nombresMes[mes - 1] + " de " + anio);

        if (lista == null || lista.isEmpty()) {
            actualizarTotales(0, 0, 0, 0);
            lblEstado.setText("No hay reservas de socios en el mes seleccionado. No se generará fichero.");
            lblEstado.setForeground(new Color(160, 80, 0));
            btnGuardar.setEnabled(false);
            return;
        }

        double sumRes = 0, sumAct = 0, sumTot = 0;
        for (ContabilidadSocioDTO dto : lista) {
            String socio = dto.getNombre() + " " + dto.getApellidos() + " · " + dto.getDni();
            modeloTabla.addRow(new Object[] {
                socio,
                String.format("%.2f €", dto.getTotalReservas()),
                String.format("%.2f €", dto.getTotalActividades()),
                String.format("%.2f €", dto.getTotalDeuda())
            });
            sumRes += dto.getTotalReservas();
            sumAct += dto.getTotalActividades();
            sumTot += dto.getTotalDeuda();
        }

        actualizarTotales(lista.size(), sumRes, sumAct, sumTot);
        lblEstado.setText("Cálculo completado. Pulse 'Guardar fichero' para exportar.");
        lblEstado.setForeground(new Color(40, 120, 40));
        btnGuardar.setEnabled(true);
    }

    private void actualizarTotales(int nSocios, double res, double act, double tot) {
        lblTotalSocios     .setText("Total socios: " + nSocios);
        lblTotalReservas   .setText(String.format("Total: %.2f €", res));
        lblTotalActividades.setText(String.format("Total: %.2f €", act));
        lblTotalDeuda      .setText(String.format("Total: %.2f €", tot));
    }

    public void mostrarMensajeFichero(String ruta) {
        if (ruta != null) {
            lblEstado.setText("Fichero guardado en: " + ruta);
            lblEstado.setForeground(new Color(40, 120, 40));
        } else {
            lblEstado.setText("No se generó fichero (sin datos).");
            lblEstado.setForeground(new Color(160, 80, 0));
        }
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Getters para el Controller
    // ─────────────────────────────────────────────────────────────────────────

    public int getMesSeleccionado()  { return cmbMes.getSelectedIndex() + 1; }

    public int getAnioSeleccionado() {
        return (Integer) cmbAnio.getSelectedItem();
    }

    public JButton getBtnCalcular() { return btnCalcular; }
    public JButton getBtnGuardar()  { return btnGuardar;  }
}