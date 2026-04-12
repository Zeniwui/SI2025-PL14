package si.pl14.actividadesEmma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import java.awt.*;

@SuppressWarnings("serial")
public class CancelacionActividadView extends JFrame {
    private JTextField txtMinimo;
    private JButton btnMostrar, btnCancelar;
    private JTable tablaActividades;
    private DefaultTableModel modelo;
    private JLabel lblSeleccionada;

    public CancelacionActividadView() {
        setTitle("Administración: Cancelar Actividades bajo mínimo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        getContentPane().setLayout(new MigLayout("fill, insets 20", "[][100!][grow]", "[][grow][][]"));

        getContentPane().add(new JLabel("Número mínimo de personas inscritas:"), "cell 0 0");
        txtMinimo = new JTextField("5");
        getContentPane().add(txtMinimo, "cell 1 0, growx");
        btnMostrar = new JButton("MOSTRAR");
        getContentPane().add(btnMostrar, "cell 2 0, alignx left");

        String[] cols = {"Nombre", "Inscritos", "Plazas", "Instalación", "ID"};
        modelo = new DefaultTableModel(null, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaActividades = new JTable(modelo);
        
        JScrollPane scroll = new JScrollPane(tablaActividades);
        scroll.setBorder(BorderFactory.createTitledBorder("Lista de actividades con menos inscritos"));
        getContentPane().add(scroll, "cell 0 1 3 1, grow");

        lblSeleccionada = new JLabel("Actividad seleccionada: Ninguna");
        lblSeleccionada.setFont(new Font("Segoe UI", Font.BOLD, 14));
        getContentPane().add(lblSeleccionada, "cell 0 2 3 1, gaptop 10");

        btnCancelar = new JButton("CANCELAR ACTIVIDAD");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(255, 200, 200));
        getContentPane().add(btnCancelar, "cell 0 3, w 200!, h 40!");
    }

    // Getters
    public JTextField getTxtMinimo() { return txtMinimo; }
    public JButton getBtnMostrar() { return btnMostrar; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JTable getTablaActividades() { return tablaActividades; }
    public DefaultTableModel getModelo() { return modelo; }
    public JLabel getLblSeleccionada() { return lblSeleccionada; }
}