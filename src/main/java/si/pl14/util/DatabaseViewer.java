/**
 * Herramienta de depuración para visualizar el contenido de cualquier tabla.
 * Muestra los datos en formato tabular por consola, calculando anchos dinámicamente.
 */
package si.pl14.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseViewer extends JFrame {

    private JComboBox<String> cbTablas;
    private Database db;

    public DatabaseViewer() {
        this.db = new Database();
        initialize();
        cargarTablasDinamicamente();
    }

    private void initialize() {
        setTitle("Visor de Base de Datos (Debug)");
        setBounds(100, 100, 500, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(0, 10));
        setContentPane(contentPane);

        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblSeleccion = new JLabel("Selecciona Tabla:");
        panelTop.add(lblSeleccion);

        cbTablas = new JComboBox<>();
        cbTablas.setPreferredSize(new Dimension(200, 25));
        panelTop.add(cbTablas);

        JButton btnConsola = new JButton("Ver en Consola");
        btnConsola.addActionListener(e -> imprimirTablaFormatoTabular());
        panelTop.add(btnConsola);

        JButton btnVisual = new JButton("Ver Visualmente");
        btnVisual.addActionListener(e -> mostrarTablaVisual());
        panelTop.add(btnVisual);

        contentPane.add(panelTop, BorderLayout.CENTER);

        JLabel lblInfo = new JLabel("<html><center>Consola: resultados en Eclipse. Visual: ventana con tabla.</center></html>");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblInfo, BorderLayout.SOUTH);
    }

    private void cargarTablasDinamicamente() {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = metaData.getTables(null, null, "%", types);

            List<String> tablas = new ArrayList<>();
            while (rs.next()) {
                String nombreTabla = rs.getString("TABLE_NAME");
                if (!nombreTabla.startsWith("sqlite_")) {
                    tablas.add(nombreTabla);
                }
            }
            for (String t : tablas) {
                cbTablas.addItem(t);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { }
            }
        }
    }

    private void mostrarTablaVisual() {
        String tabla = (String) cbTablas.getSelectedItem();
        if (tabla == null) return;

        List<Map<String, Object>> filas = db.executeQueryMap("SELECT * FROM " + tabla);

        JDialog dialog = new JDialog(this, "Tabla: " + tabla.toUpperCase(), false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);

        if (filas.isEmpty()) {
            dialog.add(new JLabel("(Tabla vacía)", SwingConstants.CENTER));
            dialog.setVisible(true);
            return;
        }

        List<String> columnas = new ArrayList<>(filas.get(0).keySet());
        String[] colArray = columnas.toArray(new String[0]);

        DefaultTableModel tableModel = new DefaultTableModel(colArray, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Map<String, Object> fila : filas) {
            Object[] row = new Object[columnas.size()];
            for (int i = 0; i < columnas.size(); i++) {
                Object val = fila.get(columnas.get(i));
                row[i] = val != null ? val : "null";
            }
            tableModel.addRow(row);
        }

        JTable jTable = new JTable(tableModel);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable.setRowHeight(22);
        jTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        jTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jTable.setGridColor(new Color(200, 200, 200));
        jTable.setSelectionBackground(new Color(173, 216, 255));

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel(tabla.toUpperCase() + "  —  " + filas.size() + " registros");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfo.setForeground(new Color(30, 100, 180));
        panel.add(lblInfo, BorderLayout.NORTH);
        panel.add(new JScrollPane(jTable), BorderLayout.CENTER);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void imprimirTablaFormatoTabular() {
        String tabla = (String) cbTablas.getSelectedItem();
        if (tabla == null) return;

        List<Map<String, Object>> filas = db.executeQueryMap("SELECT * FROM " + tabla);

        System.out.println("\n>>> TABLA: " + tabla.toUpperCase() + " (" + filas.size() + " registros)");

        if (filas.isEmpty()) {
            System.out.println("(Tabla vacía)");
            return;
        }

        List<String> columnas = new ArrayList<>(filas.get(0).keySet());

        Map<String, Integer> anchos = new HashMap<>();
        for (String col : columnas) {
            anchos.put(col, col.length());
        }
        for (Map<String, Object> fila : filas) {
            for (String col : columnas) {
                String valor = String.valueOf(fila.get(col));
                if (valor.length() > anchos.get(col)) {
                    anchos.put(col, valor.length());
                }
            }
        }
        for (String col : columnas) {
            anchos.put(col, anchos.get(col) + 3);
        }

        printLine(columnas, anchos);
        for (String col : columnas) {
            System.out.print("-".repeat(anchos.get(col) - 1) + " ");
        }
        System.out.println();

        for (Map<String, Object> fila : filas) {
            for (String col : columnas) {
                String valor = String.valueOf(fila.get(col));
                System.out.printf("%-" + anchos.get(col) + "s", valor);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printLine(List<String> columnas, Map<String, Integer> anchos) {
        for (String col : columnas) {
            System.out.printf("%-" + anchos.get(col) + "s", col.toUpperCase());
        }
        System.out.println();
    }
}

