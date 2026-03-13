package si.pl14.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Herramienta de depuración para visualizar el contenido de cualquier tabla.
 * Muestra los datos en formato tabular por consola, calculando anchos dinámicamente.
 */
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
        setBounds(100, 100, 450, 150);
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

        JButton btnImprimir = new JButton("Ver Tabla en Consola");
        btnImprimir.addActionListener(e -> imprimirTablaFormatoTabular());
        panelTop.add(btnImprimir);

        contentPane.add(panelTop, BorderLayout.CENTER);

        JLabel lblInfo = new JLabel("<html><center>Los resultados aparecerán formateados en la consola de Eclipse.</center></html>");
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


    private void imprimirTablaFormatoTabular() {
        String tabla = (String) cbTablas.getSelectedItem();
        if (tabla == null) return;

        String sql = "SELECT * FROM " + tabla;
        List<Map<String, Object>> filas = db.executeQueryMap(sql);

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
                
                String format = "%-" + anchos.get(col) + "s";
                System.out.printf(format, valor);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printLine(List<String> columnas, Map<String, Integer> anchos) {
        for (String col : columnas) {
            String format = "%-" + anchos.get(col) + "s";
            System.out.printf(format, col.toUpperCase());
        }
        System.out.println();
    }
}