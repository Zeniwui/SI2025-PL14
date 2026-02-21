package si.pl14.disponibilidad;

import si.pl14.model.InstalacionEntity;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * Vista de la pantalla "Disponibilidad de Instalación para los Socios".
 * Sigue el patrón MVC del proyecto: solo construye la UI y expone getters/setters.
 * No contiene ninguna lógica de negocio ni handlers de eventos.
 */
public class DisponibilidadView {

    // ─── Colores y fuentes ───────────────────────────────────────────────────
    static final Color COLOR_PRIMARIO    = new Color(30, 100, 180);
    static final Color COLOR_HOY         = new Color(173, 216, 230);
    static final Color COLOR_SELECCIONADO = new Color(30, 100, 180);
    static final Color COLOR_LIBRE       = new Color(200, 240, 200);
    static final Color COLOR_OCUPADO     = new Color(255, 180, 180);
    static final Color COLOR_RESERVADA   = new Color(255, 220, 150);
    static final Font  FONT_TITULO       = new Font("Segoe UI", Font.BOLD, 15);
    static final Font  FONT_NORMAL       = new Font("Segoe UI", Font.PLAIN, 13);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── Componentes principales ─────────────────────────────────────────────
    private final JDialog      frame;
    private final JComboBox<InstalacionItem> cmbInstalacion;
    private final JButton      btnComprobar;
    private final JButton      btnCerrar;
    private final JPanel       panelCalendario;
    private final JPanel       panelHorario;

    // Componentes del calendario (se reconstruyen en cada refresh)
    private JButton btnAnteriorMes;
    private JButton btnSiguienteMes;

    // ─────────────────────────────────────────────────────────────────────────
    public DisponibilidadView() {
        frame = new JDialog((Frame) null, "Disponibilidad de Instalación para los Socios", true);
        frame.setSize(860, 720);
        frame.setMinimumSize(new Dimension(720, 600));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // ── Panel raíz ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(Color.WHITE);

        // ── Barra superior ───────────────────────────────────────────────────
        JPanel cabecera = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_PRIMARIO));

        JLabel lblTitulo = new JLabel("Disponibilidad de Instalación");
        lblTitulo.setFont(FONT_TITULO);
        lblTitulo.setForeground(COLOR_PRIMARIO);

        cmbInstalacion = new JComboBox<>();
        cmbInstalacion.setFont(FONT_NORMAL);
        cmbInstalacion.setPreferredSize(new Dimension(250, 30));
        cmbInstalacion.setName("cmbInstalacion");

        btnComprobar = new JButton("Comprobar");
        btnComprobar.setFont(FONT_NORMAL);
        btnComprobar.setBackground(COLOR_PRIMARIO);
        btnComprobar.setForeground(Color.WHITE);
        btnComprobar.setFocusPainted(false);
        btnComprobar.setName("btnComprobar");

        btnCerrar = new JButton("✕");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setForeground(Color.RED);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setName("btnCerrar");

        cabecera.add(lblTitulo);
        cabecera.add(Box.createHorizontalStrut(10));
        cabecera.add(new JLabel("Instalación:"));
        cabecera.add(cmbInstalacion);
        cabecera.add(btnComprobar);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.add(cabecera, BorderLayout.CENTER);
        topBar.add(btnCerrar, BorderLayout.EAST);

        // ── Área central (calendario + horario) ──────────────────────────────
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(Color.WHITE);

        panelCalendario = new JPanel(new BorderLayout());
        panelCalendario.setBackground(Color.WHITE);
        panelCalendario.setVisible(false);
        panelCalendario.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelHorario = new JPanel(new BorderLayout());
        panelHorario.setBackground(Color.WHITE);
        panelHorario.setVisible(false);
        panelHorario.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(panelCalendario);
        centro.add(Box.createVerticalStrut(12));
        centro.add(panelHorario);

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(topBar, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        frame.add(root);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Métodos de población de la UI llamados desde el Controlador
    // ═════════════════════════════════════════════════════════════════════════

    /** Rellena el combo con la lista de instalaciones. */
    public void setInstalaciones(List<InstalacionEntity> lista) {
        cmbInstalacion.removeAllItems();
        cmbInstalacion.addItem(new InstalacionItem(0, "-- Seleccione instalación --"));
        for (InstalacionEntity i : lista) {
            cmbInstalacion.addItem(new InstalacionItem(i.getIdInstalacion(), i.getNombre()));
        }
    }

    /**
     * Reconstruye el panel del calendario para el mes indicado.
     *
     * @param fechaBase     primer día del mes a mostrar
     * @param hoy           fecha actual (para colorear)
     * @param limite        último día seleccionable (hoy + 30)
     * @param fechaSelect   fecha actualmente seleccionada (puede ser null)
     * @param onDiaClick    callback que recibe la fecha cuando el usuario pulsa un día
     */
    public void mostrarCalendario(LocalDate fechaBase, LocalDate hoy, LocalDate limite,
                                   LocalDate fechaSelect, java.util.function.Consumer<LocalDate> onDiaClick) {
        panelCalendario.removeAll();

        // ── Navegación mes ────────────────────────────────────────────────
        JPanel navMes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        navMes.setBackground(new Color(240, 245, 255));
        navMes.setBorder(new EmptyBorder(4, 0, 4, 0));

        btnAnteriorMes  = new JButton("◀");
        btnSiguienteMes = new JButton("▶");
        btnAnteriorMes.setFont(FONT_NORMAL);
        btnSiguienteMes.setFont(FONT_NORMAL);
        btnAnteriorMes.setName("btnAnteriorMes");
        btnSiguienteMes.setName("btnSiguienteMes");

        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio",
                          "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        JLabel lblMes = new JLabel(meses[fechaBase.getMonthValue() - 1] + " " + fechaBase.getYear());
        lblMes.setFont(new Font("Segoe UI", Font.BOLD, 14));

        navMes.add(btnAnteriorMes);
        navMes.add(lblMes);
        navMes.add(btnSiguienteMes);

        // ── Rejilla ───────────────────────────────────────────────────────
        JPanel rejilla = new JPanel(new GridLayout(0, 7, 4, 4));
        rejilla.setBackground(Color.WHITE);
        rejilla.setBorder(new EmptyBorder(8, 8, 8, 8));

        for (String d : new String[]{"Lun","Mar","Mié","Jue","Vie","Sáb","Dom"}) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(COLOR_PRIMARIO);
            rejilla.add(lbl);
        }

        // Celdas vacías hasta el primer día del mes
        int primerDia = fechaBase.getDayOfWeek().getValue(); // 1=Lun, 7=Dom
        for (int i = 1; i < primerDia; i++) rejilla.add(new JLabel(""));

        LocalDate cursor = fechaBase;
        while (cursor.getMonthValue() == fechaBase.getMonthValue()) {
            final LocalDate fecha = cursor;
            boolean esHoy     = fecha.equals(hoy);
            boolean enRango   = !fecha.isBefore(hoy) && !fecha.isAfter(limite);
            boolean seleccion = fecha.equals(fechaSelect);

            JButton btn = new JButton(String.valueOf(fecha.getDayOfMonth()));
            btn.setFont(FONT_NORMAL);
            btn.setFocusPainted(false);
            btn.setOpaque(true);

            if (seleccion) {
                btn.setBackground(COLOR_SELECCIONADO);
                btn.setForeground(Color.WHITE);
            } else if (esHoy) {
                btn.setBackground(COLOR_HOY);
                btn.setForeground(Color.BLACK);
            } else if (enRango) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            } else {
                btn.setBackground(new Color(230, 230, 230));
                btn.setForeground(Color.GRAY);
                btn.setEnabled(false);
            }

            if (enRango) {
                btn.addActionListener(e -> onDiaClick.accept(fecha));
            }
            rejilla.add(btn);
            cursor = cursor.plusDays(1);
        }

        // ── Leyenda ───────────────────────────────────────────────────────
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leyenda.setBackground(Color.WHITE);
        leyenda.add(chip(COLOR_HOY,              "Hoy"));
        leyenda.add(chip(COLOR_SELECCIONADO,      "Seleccionado"));
        leyenda.add(chip(new Color(230, 230, 230),"Fuera de rango"));

        panelCalendario.add(navMes,   BorderLayout.NORTH);
        panelCalendario.add(rejilla,  BorderLayout.CENTER);
        panelCalendario.add(leyenda,  BorderLayout.SOUTH);
        panelCalendario.setVisible(true);
        panelCalendario.revalidate();
        panelCalendario.repaint();
    }

    /**
     * Construye y muestra el horario completo (00:00-23:00) de un día.
     *
     * @param fecha          día seleccionado
     * @param nombreInstalacion nombre para mostrar en la cabecera
     * @param ocupaciones    mapa hora → lista de etiquetas "[A]/[R] descripcion"
     */
    public void mostrarHorario(LocalDate fecha, String nombreInstalacion,
                                Map<Integer, List<String>> ocupaciones) {
        panelHorario.removeAll();

        JLabel lblFecha = new JLabel(fecha.format(FMT) + "  —  " + nombreInstalacion);
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFecha.setForeground(COLOR_PRIMARIO);
        lblFecha.setBorder(new EmptyBorder(8, 8, 6, 8));

        JPanel tabla = new JPanel(new GridBagLayout());
        tabla.setBackground(Color.WHITE);
        tabla.setBorder(new EmptyBorder(0, 8, 8, 8));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(2, 4, 2, 4);

        for (int hora = 0; hora <= 23; hora++) {
            gc.gridx = 0; gc.gridy = hora; gc.weightx = 0;
            JLabel lblHora = new JLabel(String.format("%02d:00", hora));
            lblHora.setFont(new Font("Monospaced", Font.BOLD, 13));
            lblHora.setPreferredSize(new Dimension(65, 28));
            tabla.add(lblHora, gc);

            gc.gridx = 1; gc.weightx = 1.0;
            List<String> eventos = ocupaciones.getOrDefault(hora, Collections.emptyList());
            tabla.add(crearCeldaHora(eventos), gc);
        }

        JScrollPane scrollH = new JScrollPane(tabla);
        scrollH.setPreferredSize(new Dimension(720, 300));
        scrollH.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollH.getVerticalScrollBar().setUnitIncrement(16);

        // Leyenda horario
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leyenda.setBackground(Color.WHITE);
        leyenda.add(chip(COLOR_LIBRE,    "Libre ✓"));
        leyenda.add(chip(COLOR_OCUPADO,  "Actividad ✗"));
        leyenda.add(chip(COLOR_RESERVADA,"Reserva de socio ✗"));

        panelHorario.add(lblFecha, BorderLayout.NORTH);
        panelHorario.add(scrollH,  BorderLayout.CENTER);
        panelHorario.add(leyenda,  BorderLayout.SOUTH);
        panelHorario.setVisible(true);
        panelHorario.revalidate();
        panelHorario.repaint();
        frame.revalidate();
    }

    /** Oculta el horario (al cambiar la instalación o recomprobar) */
    public void ocultarHorario() {
        panelHorario.setVisible(false);
    }

    /** Oculta el calendario */
    public void ocultarCalendario() {
        panelCalendario.setVisible(false);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Helpers de UI
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel crearCeldaHora(List<String> eventos) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        p.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        if (eventos.isEmpty()) {
            p.setBackground(COLOR_LIBRE);
            JLabel l = new JLabel("Libre  ✓");
            l.setFont(FONT_NORMAL);
            l.setForeground(new Color(0, 128, 0));
            p.add(l);
        } else {
            boolean hayReserva = eventos.stream().anyMatch(e -> e.startsWith("[R]"));
            p.setBackground(hayReserva ? COLOR_RESERVADA : COLOR_OCUPADO);
            for (int i = 0; i < eventos.size(); i++) {
                String ev = eventos.get(i);
                String texto = ev.replaceFirst("^\\[.\\] ", "");
                JLabel l = new JLabel(texto + "  ✗");
                l.setFont(FONT_NORMAL);
                p.add(l);
                if (i < eventos.size() - 1) p.add(new JLabel(" | "));
            }
        }
        return p;
    }

    private JPanel chip(Color color, String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(Color.WHITE);
        JLabel cuadro = new JLabel("  ");
        cuadro.setOpaque(true);
        cuadro.setBackground(color);
        cuadro.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        p.add(cuadro);
        p.add(lbl);
        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Getters requeridos por el controlador
    // ═════════════════════════════════════════════════════════════════════════

    public JDialog getFrame()                               { return frame; }
    public JButton getBtnComprobar()                        { return btnComprobar; }
    public JButton getBtnCerrar()                           { return btnCerrar; }
    public JButton getBtnAnteriorMes()                      { return btnAnteriorMes; }
    public JButton getBtnSiguienteMes()                     { return btnSiguienteMes; }
    public JComboBox<InstalacionItem> getCmbInstalacion()   { return cmbInstalacion; }

    /** Devuelve la InstalacionItem seleccionada o null si no hay selección válida. */
    public InstalacionItem getInstalacionSeleccionada() {
        InstalacionItem item = (InstalacionItem) cmbInstalacion.getSelectedItem();
        return (item != null && item.getId() != 0) ? item : null;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DTO interno para el combo
    // ═════════════════════════════════════════════════════════════════════════

    public static class InstalacionItem {
        private final int    id;
        private final String nombre;
        public InstalacionItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        public int    getId()     { return id; }
        public String getNombre() { return nombre; }
        @Override public String toString() { return nombre; }
    }
}