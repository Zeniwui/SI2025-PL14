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
 * Campos:
 *   - Nombre         (obligatorio)
 *   - Descripcion    (opcional, ej: "Periodo de septiembre")
 *   - Fecha Inicio Socios
 *   - Fecha Fin Socios
 *   - Fecha Fin No Socios
 */
public class PeriodosInscripcionView {

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
    private static final Font  F_RES_IT  = new Font("Segoe UI", Font.ITALIC, 12);
    private static final Font  F_TABLA   = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Componentes ───────────────────────────────────────────────────────────
    private final JDialog    frame;
    private final JButton    btnCerrar;
    private final JButton    btnConfirmar;
    private final JTextField txtNombre;
    private final JTextField txtDescripcion;
    private final JTextField txtInicioSocios;
    private final JTextField txtFinSocios;
    private final JTextField txtFinNoSocios;

    // Resumen
    private final JPanel panelResumen;
    private final JLabel lblResNombre;
    private final JLabel lblResDescripcion;
    private final JLabel lblResSocios;
    private final JLabel lblResNoSocios;

    // Tabla
    private final DefaultTableModel tableModel;
    private final JTable            tablaPeriodos;

    // ── Constructor ───────────────────────────────────────────────────────────
    public PeriodosInscripcionView() {
        frame = new JDialog((Frame) null, "Crear periodo de inscripcion", true);
        frame.setSize(640, 720);
        frame.setMinimumSize(new Dimension(580, 660));
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
        txtNombre = campo("");
        g.gridx = 1; g.gridy = 0; g.weightx = 1.0;
        form.add(txtNombre, g);

        // Fila 1 – Descripcion
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        form.add(label("Descripcion:"), g);
        txtDescripcion = campoConPlaceholder("Ej: Periodo de septiembre  (opcional)");
        txtDescripcion.setName("txtDescripcion");
        g.gridx = 1; g.gridy = 1; g.weightx = 1.0;
        form.add(txtDescripcion, g);

        // Separador 1
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.weightx = 1.0;
        g.insets = new Insets(10, 4, 4, 4);
        form.add(separador("Fechas del periodo  (cada sub-periodo debe durar más de "
            + PeriodosInscripcionModel.MIN_DIAS_PERIODO + " días y menos de "
            + PeriodosInscripcionModel.MAX_DIAS_PERIODO + " días)"), g);

        // Separador 2
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2; g.weightx = 1.0;
        form.add(separador("A su vez se tiene que tener en cuenta cualquier incongruencia posible"), g);

        g.gridwidth = 1;
        g.insets = new Insets(6, 4, 6, 4);

        // Fila 4 – Fecha Inicio Socios
        g.gridx = 0; g.gridy = 4; g.weightx = 0;
        form.add(label("Fecha Inicio Socios →"), g);
        txtInicioSocios = campoFecha();
        txtInicioSocios.setName("txtInicioSocios");
        g.gridx = 1; g.gridy = 4; g.weightx = 1.0;
        form.add(txtInicioSocios, g);

        // Fila 5 – Fecha Fin Socios
        g.gridx = 0; g.gridy = 5; g.weightx = 0;
        form.add(label("Fecha Fin Socios →"), g);
        txtFinSocios = campoFecha();
        txtFinSocios.setName("txtFinSocios");
        g.gridx = 1; g.gridy = 5; g.weightx = 1.0;
        form.add(txtFinSocios, g);

        // Fila 6 – Fecha Fin No Socios
        g.gridx = 0; g.gridy = 6; g.weightx = 0;
        form.add(label("Fecha Fin No Socios →"), g);
        txtFinNoSocios = campoFecha();
        txtFinNoSocios.setName("txtFinNoSocios");
        g.gridx = 1; g.gridy = 6; g.weightx = 1.0;
        form.add(txtFinNoSocios, g);

        // ── Resumen + boton Confirmar (fila 7) ────────────────────────────────
        panelResumen = new JPanel();
        panelResumen.setLayout(new BoxLayout(panelResumen, BoxLayout.Y_AXIS));
        panelResumen.setBackground(FONDO_RES);
        panelResumen.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1, true),
            new EmptyBorder(10, 14, 10, 14)));

        lblResNombre      = resLabel(true,  false);
        lblResDescripcion = resLabel(false, true);   // cursiva para la descripcion
        lblResSocios      = resLabel(false, false);
        lblResNoSocios    = resLabel(false, false);

        panelResumen.add(lblResNombre);
        panelResumen.add(Box.createVerticalStrut(3));
        panelResumen.add(lblResDescripcion);
        panelResumen.add(Box.createVerticalStrut(5));
        panelResumen.add(lblResSocios);
        panelResumen.add(Box.createVerticalStrut(2));
        panelResumen.add(lblResNoSocios);

        g.gridx = 0; g.gridy = 7; g.gridwidth = 1; g.weightx = 1.0;
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

        g.gridx = 1; g.gridy = 7; g.gridwidth = 1; g.weightx = 0;
        g.insets = new Insets(16, 0, 4, 4);
        g.fill = GridBagConstraints.BOTH;
        form.add(btnConfirmar, g);

        // ── Tabla de periodos guardados ───────────────────────────────────────
        String[] columnas = {"#", "Nombre", "Descripcion", "Inicio Socios", "Fin Socios", "Fin No Socios"};
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

        // Anchos de columna
        tablaPeriodos.getColumnModel().getColumn(0).setMaxWidth(40);
        tablaPeriodos.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaPeriodos.getColumnModel().getColumn(2).setPreferredWidth(160);
        tablaPeriodos.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaPeriodos.getColumnModel().getColumn(4).setPreferredWidth(90);
        tablaPeriodos.getColumnModel().getColumn(5).setPreferredWidth(90);

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
        centro.add(form,       BorderLayout.NORTH);
        centro.add(panelTabla, BorderLayout.CENTER);

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

    /** Campo de texto simple sin placeholder. */
    private JTextField campo(String texto) {
        JTextField tf = new JTextField(texto);
        tf.setFont(F_CAMPO);
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(280, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1, true),
            new EmptyBorder(3, 8, 3, 8)));
        return tf;
    }

    /** Campo de texto con placeholder en gris que desaparece al escribir. */
    private JTextField campoConPlaceholder(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(F_CAMPO);
        tf.setForeground(Color.GRAY);
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(280, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1, true),
            new EmptyBorder(3, 8, 3, 8)));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (placeholder.equals(tf.getText())) { tf.setText(""); tf.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (tf.getText().trim().isEmpty()) { tf.setText(placeholder); tf.setForeground(Color.GRAY); }
            }
        });
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

    private JLabel resLabel(boolean negrita, boolean cursiva) {
        JLabel l = new JLabel(" ");
        if (negrita)       l.setFont(F_RES_NEG);
        else if (cursiva)  l.setFont(F_RES_IT);
        else               l.setFont(F_RES);
        l.setForeground(negrita ? AZUL : new Color(40, 40, 60));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ── Actualizacion del resumen ─────────────────────────────────────────────

    public void actualizarResumen(String nombre, String desc, String iniS,
                                   String finS, String finN) {
        boolean ok = !nombre.isEmpty() && !iniS.isEmpty()
                  && !finS.isEmpty()   && !finN.isEmpty();
        if (ok) {
            lblResNombre.setText("Periodo de inscripcion para: \"" + nombre + "\"");
            // Descripcion: solo se muestra si se ha escrito algo
            if (!desc.isEmpty()) {
                lblResDescripcion.setText("   " + desc);
                lblResDescripcion.setVisible(true);
            } else {
                lblResDescripcion.setVisible(false);
            }
            lblResSocios  .setText("   Socios:    " + iniS + " - " + finS);
            lblResNoSocios.setText("   No Socios: hasta " + finN);
            panelResumen.setBackground(FONDO_OK);
        } else {
            lblResNombre     .setText("Rellene los campos para ver el resumen");
            lblResDescripcion.setVisible(false);
            lblResSocios     .setText(" ");
            lblResNoSocios   .setText(" ");
            panelResumen.setBackground(FONDO_RES);
        }
        panelResumen.revalidate();
        panelResumen.repaint();
    }

    public void cargarTablaPeriodos(List<PeriodoInscripcionEntity> periodos) {
        tableModel.setRowCount(0);
        for (PeriodoInscripcionEntity p : periodos) {
            tableModel.addRow(new Object[]{
                p.getIdPeriodo(),
                p.getNombre(),
                p.getDescripcion() != null ? p.getDescripcion() : "",
                PeriodosInscripcionModel.isoADisplay(p.getInicioSocios()),
                PeriodosInscripcionModel.isoADisplay(p.getFinSocios()),
                PeriodosInscripcionModel.isoADisplay(p.getFinNoSocios())
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
        resetPlaceholder(txtDescripcion, "Ej: Periodo de septiembre  (opcional)");
        resetFecha(txtInicioSocios);
        resetFecha(txtFinSocios);
        resetFecha(txtFinNoSocios);
        actualizarResumen("", "", "", "", "");
    }

    private void resetFecha(JTextField tf) {
        tf.setText("dd/MM/yyyy");
        tf.setForeground(Color.GRAY);
    }

    private void resetPlaceholder(JTextField tf, String placeholder) {
        tf.setText(placeholder);
        tf.setForeground(Color.GRAY);
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public JDialog    getFrame()            { return frame; }
    public JButton    getBtnCerrar()        { return btnCerrar; }
    public JButton    getBtnConfirmar()     { return btnConfirmar; }
    public JTextField getTxtNombre()        { return txtNombre; }
    public JTextField getTxtDescripcion()   { return txtDescripcion; }
    public JTextField getTxtInicioSocios()  { return txtInicioSocios; }
    public JTextField getTxtFinSocios()     { return txtFinSocios; }
    public JTextField getTxtFinNoSocios()   { return txtFinNoSocios; }

    public String getNombreValor() { return txtNombre.getText().trim(); }

    public String getDescripcionValor() {
        String v = txtDescripcion.getText().trim();
        return v.equals("Ej: Periodo de septiembre  (opcional)") ? "" : v;
    }

    public static String fechaValor(JTextField tf) {
        String v = tf.getText().trim();
        return "dd/MM/yyyy".equals(v) ? "" : v;
    }
}