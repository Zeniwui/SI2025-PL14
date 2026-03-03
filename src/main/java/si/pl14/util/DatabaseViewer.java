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
import java.util.List;
import java.util.Map;

public class DatabaseViewer extends JFrame {

    private JComboBox<String> cbTablas;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private Database db;

    public DatabaseViewer() {
        this.db = new Database();
        initialize();
        cargarTablasDinamicamente();
    }

    private void initialize() {
        setTitle("Visor de Base de Datos (Debug)");
        setBounds(100, 100, 800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(0, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // Panel superior con selector y botón
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("Selecciona Tabla:"));

        cbTablas = new JComboBox<>();
        cbTablas.setPreferredSize(new Dimension(200, 25));
        panelTop.add(cbTablas);

        JButton btnVer = new JButton("Ver Tabla");
        btnVer.addActionListener(e -> mostrarTablaEnVentana());
        panelTop.add(btnVer);

        contentPane.add(panelTop, BorderLayout.NORTH);

        // Tabla vacía inicial
        modeloTabla = new DefaultTableModel();
        tabla = new JTable(modeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setEnabled(false); // solo lectura

        JScrollPane scrollPane = new JScrollPane(tabla);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarTablasDinamicamente() {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String nombreTabla = rs.getString("TABLE_NAME");
                if (!nombreTabla.startsWith("sqlite_"))
                    cbTablas.addItem(nombreTabla);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar tablas: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { }
        }
    }

    private void mostrarTablaEnVentana() {
        String nombreTabla = (String) cbTablas.getSelectedItem();
        if (nombreTabla == null) return;

        List<Map<String, Object>> filas = db.executeQueryMap("SELECT * FROM " + nombreTabla);

        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);

        if (filas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La tabla está vacía.");
            return;
        }

        // Añadir columnas
        List<String> columnas = new ArrayList<>(filas.get(0).keySet());
        for (String col : columnas)
            modeloTabla.addColumn(col.toUpperCase());

        // Añadir filas
        for (Map<String, Object> fila : filas) {
            Object[] fila2 = columnas.stream()
                .map(col -> fila.get(col))
                .toArray();
            modeloTabla.addRow(fila2);
        }
    }
}