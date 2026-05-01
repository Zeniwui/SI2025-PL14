package si.pl14.inscripcionesNoSocio;

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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class InscripcionNoSocioView {

    private JFrame frame;
    private JTable tablaActividades;
    private DefaultTableModel modeloTabla;
    
    // Componentes para el formulario de los no socios
    private JTextField txtDni, txtNombre, txtApellidos, txtTelefono, txtEmail;
    
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

    public InscripcionNoSocioView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Inscripción Actividades - No Socio");
        frame.setName("InscripcionActividadView");
        frame.setBounds(100, 100, 950, 850); // Ligeramente más alto para acomodar el formulario arriba
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(COLOR_FONDO);
        
        // Ajustamos el layout general para tener las filas necesarias en orden
        frame.getContentPane().setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][][][grow][][][]"));

        // --- 1. HEADER ---
        JPanel panelHeader = new JPanel(new MigLayout("fill, insets 0", "[grow][]", "[]"));
        panelHeader.setOpaque(false); 
        
        JLabel lblTitulo = new JLabel("INSCRIPCIÓN ACTIVIDADES - NO SOCIO");
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 24)); // Adaptado al dibujo (sin negrita excesiva)
        lblTitulo.setForeground(COLOR_SECUNDARIO);
        panelHeader.add(lblTitulo, "cell 0 0");
        
        frame.getContentPane().add(panelHeader, "cell 0 0, growx");

        // --- 2. FORMULARIO NO SOCIOS (Fiel al prototipo) ---
        JLabel lblDatos = createLabel("Datos del No socio");
        frame.getContentPane().add(lblDatos, "cell 0 1, gaptop 10, gapbottom 5");

        JPanel panelFormulario = new JPanel(new MigLayout("insets 0", "[100px][250px]", "[]5[]5[]5[]5[]"));
        panelFormulario.setOpaque(false);

        panelFormulario.add(new JLabel("Nombre:"), "cell 0 0");
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre, "cell 1 0, growx");

        panelFormulario.add(new JLabel("Apellidos:"), "cell 0 1");
        txtApellidos = new JTextField();
        panelFormulario.add(txtApellidos, "cell 1 1, growx");

        panelFormulario.add(new JLabel("DNI:"), "cell 0 2");
        txtDni = new JTextField();
        panelFormulario.add(txtDni, "cell 1 2, growx");

        panelFormulario.add(new JLabel("Teléfono:"), "cell 0 3");
        txtTelefono = new JTextField();
        panelFormulario.add(txtTelefono, "cell 1 3, growx");

        panelFormulario.add(new JLabel("E-mail:"), "cell 0 4");
        txtEmail = new JTextField();
        panelFormulario.add(txtEmail, "cell 1 4, growx");

        frame.getContentPane().add(panelFormulario, "cell 0 2, growx, gapbottom 10");

        // --- 3. LISTA DE ACTIVIDADES (TABLA) ---
        JLabel lblLista = createLabel("Lista de actividades");
        frame.getContentPane().add(lblLista, "cell 0 3, gapbottom 5");

        // Nombres de las columnas exactos al dibujo
        String[] columnas = {"Nombre", "Instalación", "Plazas disponibles", "Fecha fin inscripción", "Inicio", "Fin", "Precio"};
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
        frame.getContentPane().add(scrollPane, "cell 0 4, grow, hmin 150");

        // --- 4. CONTROLES DE PAGINACIÓN ---
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelPaginacion.setOpaque(false);
        
        btnAnterior = new JButton("<");
        lblPagina = new JLabel("Página 1 de 4"); // Dato placeholder de tu mockup
        lblPagina.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSiguiente = new JButton(">");
        
        panelPaginacion.add(btnAnterior);
        panelPaginacion.add(lblPagina);
        panelPaginacion.add(btnSiguiente);
        
        frame.getContentPane().add(panelPaginacion, "cell 0 5, growx");

        // --- 5. PANEL DE RESUMEN Y BOTONES ---
        JLabel lblResumenTitle = createLabel("Resumen");
        frame.getContentPane().add(lblResumenTitle, "cell 0 6, gaptop 5, gapbottom 5");

        // Añadimos una columna más en MigLayout para encajar los botones a la derecha del resumen
        panelResumen = new JPanel(new MigLayout("fill, insets 15", "[][grow][]", "[]10[]10[]10[]10[]"));
        panelResumen.setBackground(COLOR_PANEL);
        panelResumen.setBorder(new LineBorder(COLOR_SECUNDARIO, 1)); // Borde cuadrado simple como el dibujo

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

        // --- BOTONES DE ACCIÓN (Integrados en el contenedor de Resumen como el dibujo) ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        btnCancelar = new JButton("Cancelar");
        styleButton(btnCancelar, new Color(200, 200, 200), Color.BLACK); 

        btnInscribirse = new JButton("Inscribir"); // Renombrado a "Inscribir" como en tu mockup
        styleButton(btnInscribirse, COLOR_EXITO, Color.WHITE); 
        btnInscribirse.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        btnInscribirse.setEnabled(false); 

        panelBotones.add(btnCancelar);
        panelBotones.add(btnInscribirse);

     // Los colocamos abajo a la derecha dentro del panel de resumen
        panelResumen.add(panelBotones, "cell 2 4, alignx right, aligny bottom");

        frame.getContentPane().add(panelResumen, "cell 0 7, growx, gapy 10");
    }

    // Método modificado para crear la línea de subrayado del mockup
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lbl.setForeground(COLOR_SECUNDARIO);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_SECUNDARIO)); // Línea inferior
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

    // --- GETTERS ---
    public JFrame getFrame() { return frame; }
    public JTable getTablaActividades() { return tablaActividades; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JButton getBtnAnterior() { return btnAnterior; }
    public JButton getBtnSiguiente() { return btnSiguiente; }
    public JLabel getLblPagina() { return lblPagina; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JButton getBtnInscribirse() { return btnInscribirse; }
    public JTextField getTxtDni() { return txtDni; }
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtApellidos() { return txtApellidos; }
    public JTextField getTxtEmail() { return txtEmail; }
    public JTextField getTxtTelefono() { return txtTelefono; }
    
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