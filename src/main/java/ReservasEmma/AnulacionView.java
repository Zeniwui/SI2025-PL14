package ReservasEmma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import java.awt.*;

public class AnulacionView extends JFrame {
    private JTextField txtNombre, txtDni, txtId;
    private JButton btnBuscarNombre, btnBuscarDni, btnBuscarId, btnAnular, btnLimpiar;
    private JTable tablaReservas;
    private DefaultTableModel modeloTabla;
    private JLabel lblReservaSeleccionada;
    private JTextArea txtMotivo;

    public AnulacionView() {
        setTitle("Administración: Anular Reservas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 950, 600);
        getContentPane().setLayout(new MigLayout("fill, insets 20", "[80!][200!][180!][grow]", "[][][][grow][][][]"));

        // Fila 0: Nombre
        getContentPane().add(new JLabel("Nombre:"), "cell 0 0, alignx right");
        txtNombre = new JTextField();
        getContentPane().add(txtNombre, "cell 1 0, growx");
        btnBuscarNombre = new JButton("Buscar por nombre");
        getContentPane().add(btnBuscarNombre, "cell 2 0, growx");

        // Fila 1: DNI
        getContentPane().add(new JLabel("DNI:"), "cell 0 1, alignx right");
        txtDni = new JTextField();
        getContentPane().add(txtDni, "cell 1 1, growx");
        btnBuscarDni = new JButton("Buscar por DNI");
        getContentPane().add(btnBuscarDni, "cell 2 1, growx");

        // Fila 2: ID
        getContentPane().add(new JLabel("ID Socio:"), "cell 0 2, alignx right");
        txtId = new JTextField();
        getContentPane().add(txtId, "cell 1 2, growx");
        btnBuscarId = new JButton("Buscar por ID");
        getContentPane().add(btnBuscarId, "cell 2 2, growx");
        
        // A la derecha: botón de limpiar
        btnLimpiar = new JButton("LIMPIAR RESULTADOS");
        getContentPane().add(btnLimpiar, "cell 3 2, growx");

        // Fila 3: Tabla de Reservas
        String[] columnas = {"ID Reserva", "Instalación", "Fecha", "H. Inicio", "H. Fin", "Coste", "ID Socio", "DNI Socio", "Nombre Socio"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaReservas = new JTable(modeloTabla);
        tablaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getContentPane().add(new JScrollPane(tablaReservas), "cell 0 3 4 1, grow, gaptop 15");

        // Fila 4: Label Reserva Seleccionada
        lblReservaSeleccionada = new JLabel("Reserva seleccionada: Ninguna");
        lblReservaSeleccionada.setFont(new Font("Segoe UI", Font.BOLD, 14));
        getContentPane().add(lblReservaSeleccionada, "cell 0 4 4 1, gaptop 10, gapbottom 10");

        // Fila 5: Label Motivo
        getContentPane().add(new JLabel("Motivo cancelación:"), "cell 0 5 2 1, aligny bottom");

        // Fila 6: Área de Texto (Motivo) y Botón Cancelar
        txtMotivo = new JTextArea();
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        getContentPane().add(scrollMotivo, "cell 0 6 2 1, grow, h 70!");

        btnAnular = new JButton("CANCELAR RESERVA SELECCIONADA");
        btnAnular.setBackground(new Color(231, 76, 60));
        btnAnular.setForeground(Color.BLACK);
        btnAnular.setFont(new Font("Segoe UI", Font.BOLD, 13));
        getContentPane().add(btnAnular, "cell 2 6 2 1, grow, gapleft 10");
    }

    // Getters
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtDni() { return txtDni; }
    public JTextField getTxtId() { return txtId; }
    public JButton getBtnBuscarNombre() { return btnBuscarNombre; }
    public JButton getBtnBuscarDni() { return btnBuscarDni; }
    public JButton getBtnBuscarId() { return btnBuscarId; }
    public JButton getBtnAnular() { return btnAnular; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JTable getTablaReservas() { return tablaReservas; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JLabel getLblReservaSeleccionada() { return lblReservaSeleccionada; }
    public JTextArea getTxtMotivo() { return txtMotivo; }
}