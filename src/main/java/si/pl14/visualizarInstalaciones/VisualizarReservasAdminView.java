package si.pl14.visualizarInstalaciones;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import net.miginfocom.swing.MigLayout;
import java.awt.*;

public class VisualizarReservasAdminView {

    private JFrame frame;
    private JComboBox<Object> cbInstalaciones;
    private JButton btnSemanaAnterior;
    private JButton btnSemanaSiguiente;
    private JLabel lblRangoFechas;
    private JTable tablaHorario;
    private DefaultTableModel tableModel;
    private JPanel panelTabla;
    
    private boolean[] diasValidos = new boolean[]{true, true, true, true, true, true, true};

    private final Color COLOR_FONDO = new Color(240, 242, 245); 
    private final Color COLOR_PANEL = Color.WHITE;
    private final Color COLOR_SECUNDARIO = new Color(71, 85, 105); 
    
    private final Color COLOR_LIBRE_BG = new Color(200, 230, 201);
    private final Color COLOR_LIBRE_FG = new Color(46, 125, 50);  
    private final Color COLOR_OCUPADO_BG = new Color(255, 205, 210); 
    private final Color COLOR_OCUPADO_FG = new Color(198, 40, 40);  
    
    private final Color COLOR_GRIS_LIBRE_BG = new Color(235, 235, 235);
    private final Color COLOR_GRIS_LIBRE_FG = new Color(160, 160, 160);
    private final Color COLOR_GRIS_OCUPADO_BG = new Color(210, 210, 210);
    private final Color COLOR_GRIS_OCUPADO_FG = new Color(110, 110, 110);

    public VisualizarReservasAdminView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Visualizar Reservas Instalaciones - Admin");
        frame.setBounds(100, 100, 1000, 600);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(COLOR_FONDO);
        
        frame.getContentPane().setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));

        // --- 1. PANEL SUPERIOR (Controles) ---
        JPanel panelControles = new JPanel(new MigLayout("fillx, insets 15", "[][200px][grow][][][]", "[]"));
        panelControles.setBackground(COLOR_PANEL);
        panelControles.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        
        JLabel lblInstalacion = new JLabel("Instalación:");
        lblInstalacion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInstalacion.setForeground(COLOR_SECUNDARIO);
        panelControles.add(lblInstalacion, "cell 0 0");

        cbInstalaciones = new JComboBox<>();
        cbInstalaciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbInstalaciones.setBackground(Color.WHITE);
        panelControles.add(cbInstalaciones, "cell 1 0, growx");

        panelControles.add(new JLabel(""), "cell 2 0, growx");

        btnSemanaAnterior = new JButton("◄ Semana anterior");
        styleButton(btnSemanaAnterior);
        panelControles.add(btnSemanaAnterior, "cell 3 0");

        lblRangoFechas = new JLabel("23 Febrero - 01 Marzo"); 
        lblRangoFechas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRangoFechas.setHorizontalAlignment(SwingConstants.CENTER);
        panelControles.add(lblRangoFechas, "cell 4 0, width 180!");

        btnSemanaSiguiente = new JButton("Semana siguiente ►");
        styleButton(btnSemanaSiguiente);
        panelControles.add(btnSemanaSiguiente, "cell 5 0");

        frame.getContentPane().add(panelControles, "cell 0 0, growx, gapbottom 10");

        // --- 2. PANEL CENTRAL (Cuadrícula del Horario) ---
        panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(COLOR_PANEL);
        panelTabla.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        

        panelTabla.setVisible(false);

        String[] columnNames = {"Hora", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tablaHorario = new JTable(tableModel);
        
        tablaHorario.setRowHeight(60); 
        
        tablaHorario.setGridColor(new Color(230, 230, 230));
        tablaHorario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHorario.setCellSelectionEnabled(true);

        JTableHeader header = tablaHorario.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(COLOR_SECUNDARIO);
        header.setPreferredSize(new Dimension(100, 35));

        CalendarioCellRenderer renderer = new CalendarioCellRenderer();
        for (int i = 0; i < tablaHorario.getColumnCount(); i++) {
            tablaHorario.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        tablaHorario.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaHorario.getColumnModel().getColumn(0).setMaxWidth(60);

        for (int i = 9; i <= 21; i++) {
            tableModel.addRow(new Object[]{i + ":00", "Libre", "Libre", "Libre", "Libre", "Libre", "Libre", "Libre"});
        }

        JScrollPane scrollPane = new JScrollPane(tablaHorario);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        frame.getContentPane().add(panelTabla, "cell 0 1, grow");
    }

    private class CalendarioCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            
            String texto = (value != null) ? value.toString() : "";

            if (column == 0) {
                c.setBackground(new Color(245, 245, 245));
                c.setForeground(COLOR_SECUNDARIO);
                ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                boolean esDiaValido = diasValidos[column - 1]; 
                
                if (texto.isEmpty() || texto.equalsIgnoreCase("Libre")) {
                    if (esDiaValido) {
                        c.setBackground(COLOR_LIBRE_BG);
                        c.setForeground(COLOR_LIBRE_FG);
                        ((JLabel) c).setText("Libre");
                    } else {
                        c.setBackground(COLOR_GRIS_LIBRE_BG);
                        c.setForeground(COLOR_GRIS_LIBRE_FG);
                        ((JLabel) c).setText("");
                    }
                } else {
                    if (esDiaValido) {
                        c.setBackground(COLOR_OCUPADO_BG);
                        c.setForeground(COLOR_OCUPADO_FG);
                    } else {
                        c.setBackground(COLOR_GRIS_OCUPADO_BG);
                        c.setForeground(COLOR_GRIS_OCUPADO_FG);
                    }
                    ((JLabel) c).setText(texto);
                }
                
                if (isSelected) {
                    c.setBackground(c.getBackground().darker());
                }
            }
            return c;
        }
    }
    
    public void setDiasValidos(boolean[] validos) {
        this.diasValidos = validos;
        tablaHorario.repaint();
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(COLOR_SECUNDARIO);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        btn.setPreferredSize(new Dimension(140, 30));
    }

    public JFrame getFrame() { return frame; }
    public JComboBox<Object> getCbInstalaciones() { return cbInstalaciones; }
    public JButton getBtnSemanaAnterior() { return btnSemanaAnterior; }
    public JButton getBtnSemanaSiguiente() { return btnSemanaSiguiente; }
    public JLabel getLblRangoFechas() { return lblRangoFechas; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTablaHorario() { return tablaHorario; }
    public JPanel getPanelTabla() { return panelTabla; }
}