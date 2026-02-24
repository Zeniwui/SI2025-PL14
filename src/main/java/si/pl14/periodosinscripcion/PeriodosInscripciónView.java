package si.pl14.periodosinscripcion;

import si.pl14.model.PeriodoInscripcionEntity;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

/**
 * Vista de "Crear Periodo de Inscripcion".
 *
 * Campos (segun historia de usuario):
 *   - Nombre
 *   - Fecha Inicio Socios
 *   - Fecha Fin Socios
 *   - Fecha Fin No Socios
 *
 * Adicionalmente (ya se que no se pedía en RedKanban pero me parecía necesario para comprobar el funcionamiento) muestra una tabla con todos los periodos ya guardados en BD.
 */
public class PeriodosInscripciónView {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color AZUL      = new Color(30, 100, 180);
    private static final Color FONDO     = new Color(248, 250, 254);
    private static final Color FONDO_RES = new Color(235, 244, 255);
    private static final Color FONDO_OK  = new Color(220, 245, 225);
    private static final Color BORDE     = new Color(190, 210, 240);
    private static final Font  F_TITULO  = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font  F_LABEL   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font  F_CAMPO   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  F_RES     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  F_RES_NEG = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font  F_TABLA   = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Componentes ───────────────────────────────────────────────────────────
    private final JDialog    frame;
    private final JButton    btnCerrar;
    private final JButton    btnConfirmar;
    private final JTextField txtNombre;
    private final JTextField txtInicioSocios;
    private final JTextField txtFinSocios;
    private final JTextField txtFinNoSocios;

    // Resumen
    private final JPanel panelResumen;
    private final JLabel lblResNombre;
    private final JLabel lblResSocios;
    private final JLabel lblResNoSocios;

    // Tabla de periodos guardados
    private final DefaultTableModel tableModel;
    private final JTable            tablaPeriodos;

    // ── Constructor ───────────────────────────────────────────────────────────
    public PeriodosInscripciónView() {
        frame = new JDialog((Frame) null, "Crear periodo de inscripcion", true);
        frame.setSize(600, 660);
        frame.setMinimumSize(new Dimension(560, 600));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);

        // ── Barra de titulo ───────────────────────────────────────────────────
        JPanel barTitulo = new JPanel(new BorderLayout());
        barTitulo.setBackground(AZUL);
        barTitulo.setBorder(new EmptyBorder(10, 16, 10, 10));

        JLabel lblTitulo = new JLabel("🕐  Crear periodo de inscripcion  📋");
        String[] fuentes = {"Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji", "Segoe UI"};
        Font fuenteEmoji = null;
        for (String f : fuentes) {
            fuenteEmoji = new Font(f, Font.BOLD, 15);
            if (!fuenteEmoji.getFamily().equals("Dialog")) break;
        }
        lblTitulo.setFont(fuenteEmoji);
        lblTitulo.setForeground(Color.WHITE);

        btnCerrar = new JButton("✕");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(AZUL);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setName("btnCerrar");

        barTitulo.add(lblTitulo, BorderLayout.CENTER);
        barTitulo.add(btnCerrar, BorderLayout.EAST);

        // ── Formulario ────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(FONDO);
        form.setBorder(new EmptyBorder(20, 28, 10, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.fill   = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 4, 6, 4);

        // Fila 0 – Nombre
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        form.add(label("Nombre del periodo:"), g);
        txtNombre = campo();
        g.gridx = 1; g.gridy = 0; g.weightx = 1.0;
        form.add(txtNombre, g);

        // Separador
        g.gridx = 0; g.gridy = 1; g.gridwidth = 2; g.weightx = 1.0;
        g.insets = new Insets(10, 4, 4, 4);
        form.add(separador("Fechas del periodo  (cada sub-periodo debe durar más de " 
        	    + PeriodosInscripciónModel.MIN_DIAS_PERIODO + " días y menos de "
        	    + PeriodosInscripciónModel.MAX_DIAS_PERIODO + " días)"), g);
        g.gridwidth = 1;
        g.insets = new Insets(6, 4, 6, 4);
        
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.weightx = 1.0;
        g.insets = new Insets(10, 4, 4, 4);
        form.add(separador("A su vez se tiene que tener en cuenta cualquier incongruencia posible " 
        	    + PeriodosInscripciónModel.MAX_DIAS_PERIODO + " días)"), g);
        g.gridwidth = 1;
        g.insets = new Insets(6, 4, 6, 4);

        // Fila 2 – Fecha Inicio Socios
        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        form.add(label("Fecha Inicio Socios →"), g);
        txtInicioSocios = campoFecha();
        txtInicioSocios.setName("txtInicioSocios");
        g.gridx = 1; g.gridy = 3; g.weightx = 1.0;
        form.add(txtInicioSocios, g);

        // Fila 3 – Fecha Fin Socios
        g.gridx = 0; g.gridy = 4; g.weightx = 0;
        form.add(label("Fecha Fin Socios →"), g);
        txtFinSocios = campoFecha();
        txtFinSocios.setName("txtFinSocios");
        g.gridx = 1; g.gridy = 4; g.weightx = 1.0;
        form.add(txtFinSocios, g);

        // Fila 4 – Fecha Fin No Socios
        g.gridx = 0; g.gridy = 5; g.weightx = 0;
        form.add(label("Fecha Fin No Socios →"), g);
        txtFinNoSocios = campoFecha();
        txtFinNoSocios.setName("txtFinNoSocios");
        g.gridx = 1; g.gridy = 5; g.weightx = 1.0;
        form.add(txtFinNoSocios, g);

        // ── Resumen + botón Confirmar ─────────────────────────────────────────
        panelResumen = new JPanel();
        panelResumen.setLayout(new BoxLayout(panelResumen, BoxLayout.Y_AXIS));
        panelResumen.setBackground(FONDO_RES);
        panelResumen.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1, true),
            new EmptyBorder(10, 14, 10, 14)));

        lblResNombre   = resLabel(true);
        lblResSocios   = resLabel(false);
        lblResNoSocios = resLabel(false);

        panelResumen.add(lblResNombre);
        panelResumen.add(Box.createVerticalStrut(4));
        panelResumen.add(lblResSocios);
        panelResumen.add(Box.createVerticalStrut(2));
        panelResumen.add(lblResNoSocios);

        g.gridx = 0; g.gridy = 6; g.gridwidth = 1; g.weightx = 1.0;
        g.insets = new Insets(16, 4, 4, 8);
        g.fill = GridBagConstraints.BOTH;
        form.add(panelResumen, g);

        btnConfirmar = new JButton("<html><center>Confirmar<br>periodo</center></html>");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirmar.setBackground(AZUL);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setOpaque(true);
        btnConfirmar.setPreferredSize(new Dimension(130, 80));
        btnConfirmar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirmar.setName("btnConfirmar");

        g.gridx = 1; g.gridy = 6; g.gridwidth = 1; g.weightx = 0;
        g.insets = new Insets(16, 0, 4, 4);
        g.fill = GridBagConstraints.BOTH;
        form.add(btnConfirmar, g);

        // ── Tabla de periodos guardados ───────────────────────────────────────
        String[] columnas = {"#", "Nombre", "Inicio Socios", "Fin Socios", "Fin No Socios"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPeriodos = new JTable(tableModel);
        tablaPeriodos.setFont(F_TABLA);
        tablaPeriodos.setRowHeight(24);
        tablaPeriodos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaPeriodos.getTableHeader().setBackground(AZUL);
        tablaPeriodos.getTableHeader().setForeground(Color.WHITE);
        tablaPeriodos.setSelectionBackground(new Color(210, 228, 255));
        tablaPeriodos.setGridColor(new Color(210, 220, 240));
        tablaPeriodos.setShowGrid(true);

        // Ancho columna id
        tablaPeriodos.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scrollTabla = new JScrollPane(tablaPeriodos);
        scrollTabla.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 28, 14, 28),
            BorderFactory.createLineBorder(BORDE, 1, true)));
        scrollTabla.setPreferredSize(new Dimension(0, 160));

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(FONDO);

        JPanel cabeceraTabla = new JPanel(new BorderLayout());
        cabeceraTabla.setBackground(FONDO);
        cabeceraTabla.setBorder(new EmptyBorder(4, 28, 4, 28));
        JLabel lblTabla = new JLabel("📋  Periodos de inscripcion guardados:");
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTabla.setForeground(new Color(50, 60, 90));
        cabeceraTabla.add(lblTabla, BorderLayout.CENTER);

        panelTabla.add(cabeceraTabla, BorderLayout.NORTH);
        panelTabla.add(scrollTabla,   BorderLayout.CENTER);

        // ── Montaje final ─────────────────────────────────────────────────────
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.add(form,        BorderLayout.NORTH);
        centro.add(panelTabla,  BorderLayout.CENTER);

        root.add(barTitulo, BorderLayout.NORTH);
        root.add(centro,    BorderLayout.CENTER);
        frame.add(root);
    }

    // ── Helpers de construccion ───────────────────────────────────────────────

    private JLabel label(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(F_LABEL);
        l.setForeground(new Color(50, 60, 90));
        return l;
    }

    private JTextField campo() {
        JTextField tf = new JTextField();
        tf.setFont(F_CAMPO);
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(280, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1, true),
            new EmptyBorder(3, 8, 3, 8)));
        return tf;
    }

    private JTextField campoFecha() {
        final String PH = "dd/MM/yyyy";
        JTextField tf = new JTextField(PH);
        tf.setFont(F_CAMPO);
        tf.setForeground(Color.GRAY);
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(130, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1, true),
            new EmptyBorder(3, 8, 3, 8)));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (PH.equals(tf.getText())) { tf.setText(""); tf.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (tf.getText().trim().isEmpty()) { tf.setText(PH); tf.setForeground(Color.GRAY); }
            }
        });
        return tf;
    }

    private JPanel separador(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(225, 235, 252));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, AZUL),
            new EmptyBorder(4, 10, 4, 10)));
        JLabel l = new JLabel(titulo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(AZUL);
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private JLabel resLabel(boolean negrita) {
        JLabel l = new JLabel(" ");
        l.setFont(negrita ? F_RES_NEG : F_RES);
        l.setForeground(negrita ? AZUL : new Color(40, 40, 60));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ── Actualizacion del resumen ─────────────────────────────────────────────

    public void actualizarResumen(String nombre, String iniS, String finS, String finN) {
        boolean ok = !nombre.isEmpty() && !iniS.isEmpty()
                  && !finS.isEmpty()   && !finN.isEmpty();
        if (ok) {
            lblResNombre  .setText("Periodo de inscripcion para: \"" + nombre + "\"");
            lblResSocios  .setText("   Socios:    " + iniS + " - " + finS);
            lblResNoSocios.setText("   No Socios: hasta " + finN);
            panelResumen.setBackground(FONDO_OK);
        } else {
            lblResNombre  .setText("Rellene los campos para ver el resumen");
            lblResSocios  .setText(" ");
            lblResNoSocios.setText(" ");
            panelResumen.setBackground(FONDO_RES);
        }
        panelResumen.revalidate();
        panelResumen.repaint();
    }

    /**
     * Recarga la tabla inferior con la lista de periodos guardados en BD.
     * Convierte fechas ISO a formato dd/MM/yyyy para mostrar.
     */
    public void cargarTablaPeriodos(List<PeriodoInscripcionEntity> periodos) {
        tableModel.setRowCount(0);
        for (PeriodoInscripcionEntity p : periodos) {
            tableModel.addRow(new Object[]{
                p.getIdPeriodo(),
                p.getNombre(),
                PeriodosInscripciónModel.isoADisplay(p.getInicioSocios()),
                PeriodosInscripciónModel.isoADisplay(p.getFinSocios()),
                PeriodosInscripciónModel.isoADisplay(p.getFinNoSocios())
            });
        }
    }

    public void mostrarExito(String nombre) {
        JOptionPane.showMessageDialog(frame,
            "Periodo \"" + nombre + "\" creado correctamente.",
            "Periodo creado", JOptionPane.INFORMATION_MESSAGE);
        resetFormulario();
    }

    public void resetFormulario() {
        txtNombre.setText("");
        resetFecha(txtInicioSocios);
        resetFecha(txtFinSocios);
        resetFecha(txtFinNoSocios);
        actualizarResumen("", "", "", "");
    }

    private void resetFecha(JTextField tf) {
        tf.setText("dd/MM/yyyy");
        tf.setForeground(Color.GRAY);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public JDialog    getFrame()            { return frame; }
    public JButton    getBtnCerrar()        { return btnCerrar; }
    public JButton    getBtnConfirmar()     { return btnConfirmar; }
    public JTextField getTxtNombre()        { return txtNombre; }
    public JTextField getTxtInicioSocios()  { return txtInicioSocios; }
    public JTextField getTxtFinSocios()     { return txtFinSocios; }
    public JTextField getTxtFinNoSocios()   { return txtFinNoSocios; }

    public String getNombreValor() { return txtNombre.getText().trim(); }

    public static String fechaValor(JTextField tf) {
        String v = tf.getText().trim();
        return "dd/MM/yyyy".equals(v) ? "" : v;
    }
}