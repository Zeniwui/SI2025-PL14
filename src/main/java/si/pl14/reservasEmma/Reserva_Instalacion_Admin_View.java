package si.pl14.reservasEmma;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Reserva_Instalacion_Admin_View extends JFrame {

    private JComboBox<String> cbActividades;
    private JLabel lblInstalacionActual;
    private JTextArea txtHorariosActuales;
    private JButton btnCambiarInstalacion, btnCambiarHorario, btnVerConflictos;
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

        // --- SECCIÓN INSTALACIÓN ---
        JLabel lblInst = new JLabel("Instalación asignada:");
        lblInst.setBounds(300, 20, 150, 25);
        panelPrincipal.add(lblInst);

        lblInstalacionActual = new JLabel("---");
        lblInstalacionActual.setOpaque(true);
        lblInstalacionActual.setBackground(Color.WHITE);
        lblInstalacionActual.setBorder(new LineBorder(Color.GRAY));
        lblInstalacionActual.setBounds(440, 20, 120, 25);
        panelPrincipal.add(lblInstalacionActual);

        btnCambiarInstalacion = new JButton("CAMBIAR INSTALACIÓN");
        btnCambiarInstalacion.setBounds(570, 15, 180, 35);
        panelPrincipal.add(btnCambiarInstalacion);

        // --- SECCIÓN HORARIO ---
        JLabel lblHor = new JLabel("Horario asignado:");
        lblHor.setBounds(20, 70, 120, 25);
        panelPrincipal.add(lblHor);

        txtHorariosActuales = new JTextArea();
        txtHorariosActuales.setEditable(false);
        txtHorariosActuales.setBorder(new LineBorder(Color.LIGHT_GRAY));
        txtHorariosActuales.setBounds(140, 70, 250, 60);
        panelPrincipal.add(txtHorariosActuales);

        btnCambiarHorario = new JButton("CAMBIAR HORARIO");
        btnCambiarHorario.setBounds(400, 80, 160, 35);
        panelPrincipal.add(btnCambiarHorario);

        // --- BOTÓN VER CONFLICTOS ---
        btnVerConflictos = new JButton("VER CONFLICTOS");
        btnVerConflictos.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnVerConflictos.setBounds(20, 150, 180, 40);
        panelPrincipal.add(btnVerConflictos);

        // --- TABLA DE CONFLICTOS (EL RECUADRO GRANDE) ---
        String[] col = {"Fecha", "Franja Horaria", "Coincide con (CONFLICTO)"};
        modelConflictos = new DefaultTableModel(null, col);
        tableConflictos = new JTable(modelConflictos);
        tableConflictos.setRowHeight(25);
        
        JScrollPane scroll = new JScrollPane(tableConflictos);
        scroll.setBounds(20, 200, 840, 330);
        scroll.setBorder(new TitledBorder("Visor de Conflictos Detallado"));
        panelPrincipal.add(scroll);
    }

    public JComboBox<String> getCbActividades() { return cbActividades; }
    public JLabel getLblInstalacionActual() { return lblInstalacionActual; }
    public JTextArea getTxtHorariosActuales() { return txtHorariosActuales; }
    public JButton getBtnCambiarInstalacion() { return btnCambiarInstalacion; }
    public JButton getBtnCambiarHorario() { return btnCambiarHorario; }
    public JButton getBtnVerConflictos() { return btnVerConflictos; }
    public DefaultTableModel getModelConflictos() { return modelConflictos; }
}