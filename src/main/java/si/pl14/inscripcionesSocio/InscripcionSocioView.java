package si.pl14.inscripcionesSocio;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class InscripcionSocioView {

    private JFrame frame;
    private JTable tablaActividades;
    private DefaultTableModel modeloTabla;
    
    // Controles de Paginación
    private JButton btnAnterior;
    private JButton btnSiguiente;
    private JLabel lblPagina;

    // Componentes del Panel de Resumen
    private JPanel panelResumen;
    private JLabel lblResNombre;
    private JLabel lblResDescripcion;
    private JLabel lblResHorario;
    private JLabel lblResInicio;
    private JLabel lblResFin;
    private JLabel lblResPrecio;

    // Botones de acción
    private JButton btnCancelar;
    private JButton btnInscribirse;

    // Paleta de colores
    private final Color COLOR_FONDO = new Color(240, 242, 245); 
    private final Color COLOR_PANEL = Color.WHITE;
    private final Color COLOR_PRIMARIO = new Color(33, 150, 243); 
    private final Color COLOR_SECUNDARIO = new Color(71, 85, 105); 
    private final Color COLOR_EXITO = new Color(46, 204, 113); 
    private final Color COLOR_TEXTO = new Color(51, 51, 51);

    public InscripcionSocioView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Inscribirse a Actividades - Socio");
        frame.setName("InscripcionActividadView");
        frame.setBounds(100, 100, 950, 750);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(COLOR_FONDO);
        
        frame.getContentPane().setLayout(new MigLayout("fill, insets 20", "[grow]", "[50px:n][grow][][][]"));

        // --- 1. HEADER ---
        JPanel panelHeader = new JPanel(new MigLayout("fill, insets 0", "[grow][]", "[]"));
        panelHeader.setOpaque(false); 
        
        JLabel lblTitulo = new JLabel("INSCRIBIRSE A ACTIVIDADES - SOCIO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_SECUNDARIO);
        panelHeader.add(lblTitulo, "cell 0 0");
        
        JButton btnCerrar = new JButton("X");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCerrar.setForeground(COLOR_SECUNDARIO);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> frame.dispose());
        panelHeader.add(btnCerrar, "cell 1 0, alignx right");
        
        frame.getContentPane().add(panelHeader, "cell 0 0, growx");

        // --- 2. LISTA DE ACTIVIDADES (TABLA) ---
        JLabel lblLista = createLabel("Lista de actividades");
        frame.getContentPane().add(lblLista, "cell 0 1, gapbottom 5");

        String[] columnas = {"Nombre", "Instalación", "Plazas", "Fin Inscrip.", "Inicio", "Fin", "Precio/mes"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tablaActividades = new JTable(modeloTabla);
        tablaActividades.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaActividades.setRowHeight(30);
        tablaActividades.getTableHeader().setReorderingAllowed(false);
        tablaActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setBackground(COLOR_PRIMARIO);
        headerRenderer.setForeground(Color.WHITE);   
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1), 
            new EmptyBorder(5, 5, 5, 5)
        ));

        for (int i = 0; i < tablaActividades.getColumnModel().getColumnCount(); i++) {
            tablaActividades.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tablaActividades);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        frame.getContentPane().add(scrollPane, "cell 0 2, grow, hmin 220");
        // --- 3. CONTROLES DE PAGINACIÓN ---
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelPaginacion.setOpaque(false);
        
        btnAnterior = new JButton("<");
        lblPagina = new JLabel("Página 1 de 1");
        lblPagina.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSiguiente = new JButton(">");
        
        panelPaginacion.add(btnAnterior);
        panelPaginacion.add(lblPagina);
        panelPaginacion.add(btnSiguiente);
        
        frame.getContentPane().add(panelPaginacion, "cell 0 3, growx");

        // --- 4. PANEL DE RESUMEN ---
        panelResumen = new JPanel(new MigLayout("fill, insets 15", "[][grow]", "[]10[]10[]10[]"));
        panelResumen.setBackground(COLOR_PANEL);
        panelResumen.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200), 1, true), 
                "Resumen", 
                TitledBorder.LEFT, 
                TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14), 
                COLOR_SECUNDARIO));

        lblResNombre = createResumenField(panelResumen, "Nombre:", "cell 0 0", "cell 1 0");
        lblResDescripcion = createResumenField(panelResumen, "Descripción:", "cell 0 1", "cell 1 1");
        lblResHorario = createResumenField(panelResumen, "Horario:", "cell 0 2", "cell 1 2");
        
        JPanel panelFechas = new JPanel(new MigLayout("insets 0", "[][grow][][grow]", "[]"));
        panelFechas.setOpaque(false);
        panelFechas.add(new JLabel("Inicio:"), "cell 0 0");
        lblResInicio = createValueLabel();
        panelFechas.add(lblResInicio, "cell 1 0, w 100!");
        
        panelFechas.add(new JLabel("Fin:"), "cell 2 0, gapleft 20");
        lblResFin = createValueLabel();
        panelFechas.add(lblResFin, "cell 3 0, w 100!");
        panelResumen.add(panelFechas, "cell 0 3, span 2, growx");

        lblResPrecio = createResumenField(panelResumen, "Precio:", "cell 0 4", "cell 1 4");

        frame.getContentPane().add(panelResumen, "cell 0 4, growx, gapy 10");

        // --- 5. BOTONES DE ACCIÓN ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnCancelar = new JButton("Cancelar");
        styleButton(btnCancelar, new Color(200, 200, 200), Color.BLACK); 

        btnInscribirse = new JButton("Inscribirse");
        styleButton(btnInscribirse, COLOR_EXITO, Color.WHITE); 
        btnInscribirse.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        btnInscribirse.setEnabled(false); 

        panelBotones.add(btnCancelar);
        panelBotones.add(btnInscribirse);

        frame.getContentPane().add(panelBotones, "cell 0 5, growx, gaptop 15");
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(COLOR_SECUNDARIO);
        return lbl;
    }

    private JLabel createResumenField(JPanel panel, String labelText, String constraintTitle, String constraintValue) {
        JLabel lblTitle = new JLabel(labelText);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblTitle, constraintTitle);
        
        JLabel lblValue = createValueLabel();
        panel.add(lblValue, constraintValue + ", growx"); 
        return lblValue;
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("---");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(COLOR_TEXTO);
        return lbl;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false); 
        btn.setBorderPainted(false); 
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
    }

    public JFrame getFrame() { return frame; }
    public JTable getTablaActividades() { return tablaActividades; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JButton getBtnAnterior() { return btnAnterior; }
    public JButton getBtnSiguiente() { return btnSiguiente; }
    public JLabel getLblPagina() { return lblPagina; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JButton getBtnInscribirse() { return btnInscribirse; }
    
    public void setResumen(String nombre, String desc, String horario, String inicio, String fin, String precio) {
        lblResNombre.setText(nombre);
        lblResDescripcion.setText(desc);
        lblResHorario.setText(horario);
        lblResInicio.setText(inicio);
        lblResFin.setText(fin);
        lblResPrecio.setText(precio + " €");
        btnInscribirse.setEnabled(true);
    }
    
    public void limpiarResumen() {
        lblResNombre.setText("---");
        lblResDescripcion.setText("---");
        lblResHorario.setText("---");
        lblResInicio.setText("---");
        lblResFin.setText("---");
        lblResPrecio.setText("---");
        btnInscribirse.setEnabled(false);
    }
}
