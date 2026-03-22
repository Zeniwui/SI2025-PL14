package si.pl14.reservasEmma;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Reserva_Instalacion_Admin_View extends JFrame {

    private JComboBox<String> cbActividades;
    private JLabel lblInstalacionActual;
    private JTextArea txtHorariosActuales;
    private JButton btnCambiarInstalacion, btnGestionarHorarios, btnVerConflictos, btnCrearReservas;
    private JTextField txtFiltroInicio, txtFiltroFin;
    private JTable tableConflictos;
    private DefaultTableModel modelConflictos;

    public Reserva_Instalacion_Admin_View() {
        setTitle("Planificación de Reservas de Actividad");
        setBounds(100, 100, 900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panelPrincipal = new JPanel(null);
        getContentPane().add(panelPrincipal);

        // --- SECCIÓN SUPERIOR: SELECCIÓN ---
        JLabel lblAct = new JLabel("Actividad:");
        lblAct.setBounds(20, 20, 80, 25);
        panelPrincipal.add(lblAct);

        cbActividades = new JComboBox<>();
        cbActividades.setBounds(90, 20, 180, 25);
        panelPrincipal.add(cbActividades);

        JLabel lblInst = new JLabel("Instalación:");
        lblInst.setBounds(300, 20, 150, 25);
        panelPrincipal.add(lblInst);

        lblInstalacionActual = new JLabel("---");
        lblInstalacionActual.setOpaque(true);
        lblInstalacionActual.setBackground(Color.WHITE);
        lblInstalacionActual.setBorder(new LineBorder(Color.GRAY));
        lblInstalacionActual.setBounds(380, 20, 180, 25);
        panelPrincipal.add(lblInstalacionActual);

        btnCambiarInstalacion = new JButton("CAMBIAR PISTA");
        btnCambiarInstalacion.setBounds(570, 15, 180, 35);
        panelPrincipal.add(btnCambiarInstalacion);

        // --- SECCIÓN HORARIO ---
        JLabel lblHor = new JLabel("Horarios Semanales:");
        lblHor.setBounds(20, 70, 120, 25);
        panelPrincipal.add(lblHor);

        txtHorariosActuales = new JTextArea();
        txtHorariosActuales.setEditable(false);
        txtHorariosActuales.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtHorariosActuales.setBorder(new LineBorder(Color.LIGHT_GRAY));
        txtHorariosActuales.setBounds(140, 70, 250, 60);
        panelPrincipal.add(txtHorariosActuales);

        btnGestionarHorarios = new JButton("GESTIONAR HORARIOS");
        btnGestionarHorarios.setBounds(400, 80, 170, 35);
        panelPrincipal.add(btnGestionarHorarios);

        // --- FILTROS ---
        JLabel lblFiltro = new JLabel("Periodo de Reservas:");
        lblFiltro.setBounds(580, 60, 200, 20);
        panelPrincipal.add(lblFiltro);

        txtFiltroInicio = new JTextField();
        txtFiltroInicio.setBounds(580, 80, 90, 25);
        panelPrincipal.add(txtFiltroInicio);

        txtFiltroFin = new JTextField();
        txtFiltroFin.setBounds(680, 80, 90, 25);
        panelPrincipal.add(txtFiltroFin);

        // --- ACCIONES ---
        btnVerConflictos = new JButton("1. VER CONFLICTOS");
        btnVerConflictos.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnVerConflictos.setBounds(20, 150, 180, 40);
        panelPrincipal.add(btnVerConflictos);

        btnCrearReservas = new JButton("2. GENERAR RESERVAS");
        btnCrearReservas.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnCrearReservas.setBackground(new Color(210, 255, 210));
        btnCrearReservas.setBounds(210, 150, 220, 40);
        panelPrincipal.add(btnCrearReservas);

        // --- TABLA ---
        String[] col = {"Fecha / Tipo", "Horario Detalle", "Estado / Conflicto Detectado"};
        modelConflictos = new DefaultTableModel(null, col);
        tableConflictos = new JTable(modelConflictos);
        tableConflictos.setRowHeight(25);
        
        tableConflictos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = table.getValueAt(row, 2).toString();
                if (val.contains("CONFLICTO") || val.contains("SOCIO")) {
                    c.setBackground(new Color(255, 210, 210));
                    c.setForeground(new Color(180, 0, 0));
                } else if (val.contains("LIBRE")) {
                    c.setBackground(new Color(220, 255, 220));
                    c.setForeground(new Color(0, 100, 0));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tableConflictos);
        scroll.setBounds(20, 200, 840, 330);
        scroll.setBorder(new TitledBorder("Visor de Disponibilidad e Incompatibilidades"));
        panelPrincipal.add(scroll);
    }

    public JComboBox<String> getCbActividades() { return cbActividades; }
    public JLabel getLblInstalacionActual() { return lblInstalacionActual; }
    public JTextArea getTxtHorariosActuales() { return txtHorariosActuales; }
    public JButton getBtnCambiarInstalacion() { return btnCambiarInstalacion; }
    public JButton getBtnGestionarHorarios() { return btnGestionarHorarios; }
    public JButton getBtnVerConflictos() { return btnVerConflictos; }
    public JButton getBtnCrearReservas() { return btnCrearReservas; }
    public JTextField getTxtFiltroInicio() { return txtFiltroInicio; }
    public JTextField getTxtFiltroFin() { return txtFiltroFin; }
    public DefaultTableModel getModelConflictos() { return modelConflictos; }
}