package si.pl14.ReservasEmma;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@SuppressWarnings("serial")
public class Generacion_Automatica_View extends JFrame {

    private JComboBox<String> cbActividadesPendientes;
    private JButton btnGenerarReservas;
    private JTable tableLog;
    private DefaultTableModel modelLog;

    public Generacion_Automatica_View() {
        setTitle("Generador Automático de Reservas (Ejecución)");
        setBounds(150, 150, 900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(null);
        getContentPane().add(panel);

        JLabel lblAct = new JLabel("Actividades PENDIENTES de generar reservas:");
        lblAct.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblAct.setBounds(20, 20, 350, 25);
        panel.add(lblAct);

        cbActividadesPendientes = new JComboBox<>();
        cbActividadesPendientes.setBounds(20, 50, 300, 30);
        panel.add(cbActividadesPendientes);

        btnGenerarReservas = new JButton("EJECUTAR RESERVAS AUTOMÁTICAS");
        btnGenerarReservas.setBackground(new Color(50, 150, 250));
        btnGenerarReservas.setForeground(Color.WHITE);
        btnGenerarReservas.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnGenerarReservas.setBounds(340, 50, 280, 30);
        panel.add(btnGenerarReservas);

        // --- TABLA DE RESULTADOS ---
        String[] columnas = {"Fecha", "Franja", "Resultado de la Operación"};
        modelLog = new DefaultTableModel(null, columnas);
        tableLog = new JTable(modelLog);
        tableLog.setRowHeight(25);

        // Colores según lo que haya pasado (Socio borrado, Actividad choca, Libre)
        tableLog.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String estado = table.getValueAt(row, 2).toString();
                if (estado.contains("ERROR") || estado.contains("OTRA ACTIVIDAD")) {
                    c.setBackground(new Color(255, 200, 200)); // Rojo: Conflicto insalvable
                    c.setForeground(new Color(150, 0, 0));
                } else if (estado.contains("SOCIO ANULADO")) {
                    c.setBackground(new Color(255, 240, 180)); // Amarillo: Se aplicó la prioridad
                    c.setForeground(new Color(150, 100, 0));
                } else if (estado.contains("OK")) {
                    c.setBackground(new Color(220, 255, 220)); // Verde: Creado limpio
                    c.setForeground(new Color(0, 100, 0));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tableLog);
        scroll.setBounds(20, 110, 840, 420);
        scroll.setBorder(new TitledBorder("Registro de Operaciones (Log)"));
        panel.add(scroll);
    }

    public JComboBox<String> getCbActividadesPendientes() { return cbActividadesPendientes; }
    public JButton getBtnGenerarReservas() { return btnGenerarReservas; }
    public DefaultTableModel getModelLog() { return modelLog; }
}