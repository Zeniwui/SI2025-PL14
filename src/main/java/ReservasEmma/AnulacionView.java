package ReservasEmma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import java.awt.*;

public class AnulacionView extends JFrame {
    private JTable tablaReservas;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdSocio;
    private JButton btnBuscar;
    private JButton btnAnular;

    public AnulacionView() {
        setTitle("Administración: Anular Reservas de Socio");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 700, 500);
        getContentPane().setLayout(new MigLayout("fill, insets 20", "[grow][]", "[][grow][]"));

        // Cabecera: Búsqueda
        getContentPane().add(new JLabel("ID Socio:"), "split 3");
        txtIdSocio = new JTextField();
        getContentPane().add(txtIdSocio, "w 100!");
        btnBuscar = new JButton("Buscar Reservas");
        getContentPane().add(btnBuscar, "gapleft 10, wrap");

        // Tabla
        String[] columnas = {"ID", "Instalación", "Fecha", "Hora Inicio", "Hora Fin", "Coste"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaReservas = new JTable(modeloTabla);
        getContentPane().add(new JScrollPane(tablaReservas), "span 2, grow, wrap");

        // Botonera
        btnAnular = new JButton("Anular Reserva Seleccionada");
        btnAnular.setBackground(new Color(231, 76, 60));
        btnAnular.setForeground(Color.WHITE);
        btnAnular.setFont(new Font("Segoe UI", Font.BOLD, 13));
        getContentPane().add(btnAnular, "span 2, center, gaptop 10");
    }

    public String pedirMotivo() {
        return JOptionPane.showInputDialog(this, 
            "Especifique el motivo de la cancelación (Obligatorio):", 
            "Motivo de Anulación", 
            JOptionPane.QUESTION_MESSAGE);
    }

    // Getters
    public JButton getBtnAnular() { return btnAnular; }
    public JTable getTablaReservas() { return tablaReservas; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JTextField getTxtIdSocio() { return txtIdSocio; }
    public JButton getBtnBuscar() { return btnBuscar; }
}