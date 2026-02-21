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

public class DisponibilidadView {

    static final Color COLOR_PRIMARIO     = new Color(30, 100, 180);
    static final Color COLOR_HOY          = new Color(173, 216, 230);
    static final Color COLOR_SELECCIONADO = new Color(30, 100, 180);
    static final Color COLOR_LIBRE        = new Color(200, 240, 200);
    static final Color COLOR_OCUPADO      = new Color(255, 180, 180);
    static final Color COLOR_RESERVADA    = new Color(255, 220, 150);
    static final Color COLOR_MIS_RESERVAS = new Color(173, 216, 255);
    static final Color COLOR_CERRADO      = new Color(220, 220, 220);
    static final Font  FONT_TITULO        = new Font("Segoe UI", Font.BOLD, 15);
    static final Font  FONT_NORMAL        = new Font("Segoe UI", Font.PLAIN, 13);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JDialog                    frame;
    private final JComboBox<InstalacionItem> cmbInstalacion;
    private final JButton                    btnComprobar;
    private final JButton                    btnCerrar;
    private final JPanel                     panelCalendario;
    private final JPanel                     panelHorario;
    private final JTabbedPane                tabbedHorario;
    private final JPanel                     tabDisponibilidad;
    private final JPanel                     tabMisReservas;
    private JButton btnAnteriorMes;
    private JButton btnSiguienteMes;

    public DisponibilidadView() {
        frame = new JDialog((Frame) null, "Disponibilidad de Instalación para los Socios", true);
        frame.setSize(860, 760);
        frame.setMinimumSize(new Dimension(720, 620));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(Color.WHITE);

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

        tabbedHorario = new JTabbedPane();
        tabbedHorario.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabDisponibilidad = new JPanel(new BorderLayout());
        tabDisponibilidad.setBackground(Color.WHITE);
        tabbedHorario.addTab("Disponibilidad", tabDisponibilidad);

        tabMisReservas = new JPanel(new BorderLayout());
        tabMisReservas.setBackground(Color.WHITE);
        tabMisReservas.add(crearPanelMisReservasVacio(), BorderLayout.CENTER);
        tabbedHorario.addTab("Mis Reservas", tabMisReservas);
        tabbedHorario.setBackgroundAt(1, new Color(30, 100, 180));
        tabbedHorario.setForegroundAt(1, Color.WHITE);

        panelHorario.add(tabbedHorario, BorderLayout.CENTER);

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

    public void setInstalaciones(List<InstalacionEntity> lista) {
        cmbInstalacion.removeAllItems();
        cmbInstalacion.addItem(new InstalacionItem(0, "-- Seleccione instalación --"));
        for (InstalacionEntity i : lista) {
            cmbInstalacion.addItem(new InstalacionItem(i.getIdInstalacion(), i.getNombre()));
        }
    }

    public void mostrarCalendario(LocalDate fechaBase, LocalDate hoy, LocalDate limite,
                                   LocalDate fechaSelect,
                                   java.util.function.Consumer<LocalDate> onDiaClick) {
        panelCalendario.removeAll();

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

        JPanel rejilla = new JPanel(new GridLayout(0, 7, 4, 4));
        rejilla.setBackground(Color.WHITE);
        rejilla.setBorder(new EmptyBorder(8, 8, 8, 8));

        for (String d : new String[]{"Lun","Mar","Mié","Jue","Vie","Sáb","Dom"}) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(COLOR_PRIMARIO);
            rejilla.add(lbl);
        }

        int primerDia = fechaBase.getDayOfWeek().getValue();
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

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leyenda.setBackground(Color.WHITE);
        leyenda.add(chip(COLOR_HOY,               "Hoy"));
        leyenda.add(chip(COLOR_SELECCIONADO,       "Seleccionado"));
        leyenda.add(chip(new Color(230, 230, 230), "Fuera de rango"));

        panelCalendario.add(navMes,  BorderLayout.NORTH);
        panelCalendario.add(rejilla, BorderLayout.CENTER);
        panelCalendario.add(leyenda, BorderLayout.SOUTH);
        panelCalendario.setVisible(true);
        panelCalendario.revalidate();
        panelCalendario.repaint();
    }

    public void mostrarHorario(LocalDate fecha, String nombreInstalacion,
                                Map<Integer, List<String>> ocupaciones) {

        // ── Calcular horario de apertura según día de la semana ───────────
        // Lun-Vie (1-5): 08:00 - 21:00  |  Sáb-Dom (6-7): 09:00 - 15:00
        boolean esFinDeSemana = fecha.getDayOfWeek().getValue() >= 6;
        int horaApertura = esFinDeSemana ? 9  : 8;
        int horaCierre   = esFinDeSemana ? 15 : 21;
        String horarioTexto = esFinDeSemana
            ? "Sábado/Domingo: 09:00 – 15:00"
            : "Lunes–Viernes: 08:00 – 21:00";

        // ── Cabecera de fecha + horario de apertura ───────────────────────
        JPanel cabHorario = new JPanel(new BorderLayout());
        cabHorario.setBackground(Color.WHITE);

        JLabel lblFecha = new JLabel(fecha.format(FMT) + "  —  " + nombreInstalacion);
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFecha.setForeground(COLOR_PRIMARIO);
        lblFecha.setBorder(new EmptyBorder(8, 8, 2, 8));

        JLabel lblHorario = new JLabel("🕐  Horario de apertura: " + horarioTexto);
        lblHorario.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHorario.setForeground(new Color(80, 80, 80));
        lblHorario.setBorder(new EmptyBorder(0, 8, 6, 8));

        cabHorario.add(lblFecha,   BorderLayout.NORTH);
        cabHorario.add(lblHorario, BorderLayout.SOUTH);

        // ── Tabla de horas ────────────────────────────────────────────────
        JPanel tabla = new JPanel(new GridBagLayout());
        tabla.setBackground(Color.WHITE);
        tabla.setBorder(new EmptyBorder(0, 8, 8, 8));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(2, 4, 2, 4);

        int fila = 0;
        for (int hora = 0; hora <= 23; hora++) {
            boolean abierto = hora >= horaApertura && hora < horaCierre;

            gc.gridx = 0; gc.gridy = fila; gc.weightx = 0;
            JLabel lblHora = new JLabel(String.format("%02d:00", hora));
            lblHora.setFont(new Font("Monospaced", Font.BOLD, 13));
            lblHora.setPreferredSize(new Dimension(65, 28));
            // Hora en gris si el centro está cerrado
            lblHora.setForeground(abierto ? Color.BLACK : new Color(180, 180, 180));
            tabla.add(lblHora, gc);

            gc.gridx = 1; gc.weightx = 1.0;
            if (abierto) {
                List<String> eventos = ocupaciones.getOrDefault(hora, Collections.emptyList());
                tabla.add(crearCeldaHora(eventos), gc);
            } else {
                tabla.add(crearCeldaCerrada(), gc);
            }
            fila++;
        }

        JScrollPane scrollH = new JScrollPane(tabla);
        scrollH.setPreferredSize(new Dimension(720, 300));
        scrollH.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollH.getVerticalScrollBar().setUnitIncrement(16);

        // Scroll automático a la hora de apertura
        SwingUtilities.invokeLater(() -> {
            int posY = horaApertura * 34; // aprox. altura de cada fila
            scrollH.getVerticalScrollBar().setValue(posY);
        });

        // ── Leyenda ───────────────────────────────────────────────────────
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leyenda.setBackground(Color.WHITE);
        leyenda.add(chip(COLOR_LIBRE,        "Libre ✓"));
        leyenda.add(chip(COLOR_OCUPADO,      "Actividad ✗"));
        leyenda.add(chip(COLOR_RESERVADA,    "Reserva de socio ✗"));
        leyenda.add(chip(COLOR_MIS_RESERVAS, "Mis reservas"));
        leyenda.add(chip(COLOR_CERRADO,      "Centro cerrado"));

        // ── Rellena la pestaña Disponibilidad ─────────────────────────────
        tabDisponibilidad.removeAll();
        tabDisponibilidad.add(cabHorario, BorderLayout.NORTH);
        tabDisponibilidad.add(scrollH,    BorderLayout.CENTER);
        tabDisponibilidad.add(leyenda,    BorderLayout.SOUTH);
        tabDisponibilidad.revalidate();
        tabDisponibilidad.repaint();

        tabbedHorario.setSelectedIndex(0);

        panelHorario.setVisible(true);
        panelHorario.revalidate();
        panelHorario.repaint();
        frame.revalidate();
    }

    public void ocultarHorario()    { panelHorario.setVisible(false); }
    public void ocultarCalendario() { panelCalendario.setVisible(false); }

    /**
     * Rellena la pestana "Mis Reservas" con el listado de reservas del socio actual
     * para la fecha e instalacion seleccionadas.
     * Cada fila del listado recibido contiene:
     *   [0] horaInicioInt, [1] horaFinInt, [2] hora_inicio(str), [3] hora_fin(str),
     *   [4] estado_pago,   [5] metodo_pago, [6] coste_reserva
     */
    public void mostrarMisReservas(java.time.LocalDate fecha, String nombreInstalacion,
                                    java.util.List<Object[]> reservas) {
        tabMisReservas.removeAll();

        // Cabecera
        JLabel lblCab = new JLabel(fecha.format(FMT) + "  —  " + nombreInstalacion);
        lblCab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCab.setForeground(COLOR_PRIMARIO);
        lblCab.setBorder(new EmptyBorder(8, 8, 6, 8));

        if (reservas.isEmpty()) {
            // Sin reservas: mostrar mensaje informativo
            JPanel sinReservas = new JPanel(new GridBagLayout());
            sinReservas.setBackground(new Color(235, 244, 255));

            JPanel tarjeta = new JPanel();
            tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
            tarjeta.setBackground(Color.WHITE);
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 100, 180), 2, true),
                new EmptyBorder(20, 36, 20, 36)
            ));
            JLabel ico = new JLabel("📭", SwingConstants.CENTER);
            ico.setFont(new Font("Segoe UI", Font.PLAIN, 36));
            ico.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel msg = new JLabel("No tienes reservas en esta instalacion para este dia.", SwingConstants.CENTER);
            msg.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            msg.setForeground(Color.GRAY);
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            tarjeta.add(ico);
            tarjeta.add(Box.createVerticalStrut(8));
            tarjeta.add(msg);
            sinReservas.add(tarjeta);

            tabMisReservas.add(lblCab,       BorderLayout.NORTH);
            tabMisReservas.add(sinReservas,  BorderLayout.CENTER);

        } else {
            // Tabla de reservas propias
            JPanel tabla = new JPanel(new GridBagLayout());
            tabla.setBackground(Color.WHITE);
            tabla.setBorder(new EmptyBorder(4, 8, 8, 8));
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(3, 6, 3, 6);

            // Cabeceras de columna
            String[] headers = {"Hora", "Duración", "Estado pago", "Método pago", "Coste"};
            for (int col = 0; col < headers.length; col++) {
                gc.gridx = col; gc.gridy = 0; gc.weightx = col == 0 ? 0 : 1.0;
                JLabel h = new JLabel(headers[col]);
                h.setFont(new Font("Segoe UI", Font.BOLD, 12));
                h.setForeground(COLOR_PRIMARIO);
                h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARIO));
                tabla.add(h, gc);
            }

            // Filas de reservas
            for (int fila = 0; fila < reservas.size(); fila++) {
                Object[] r = reservas.get(fila);
                String horaInicio = r[2] != null ? r[2].toString() : "--";
                String horaFin    = r[3] != null ? r[3].toString() : "--";
                int    hIni       = r[0] instanceof Number ? ((Number)r[0]).intValue() : 0;
                int    hFin       = r[1] instanceof Number ? ((Number)r[1]).intValue() : 0;
                String estado     = r[4] != null ? r[4].toString() : "-";
                String metodo     = r[5] != null ? r[5].toString() : "-";
                String coste      = r[6] != null ? String.format("%.2f €", ((Number)r[6]).doubleValue()) : "0.00 €";
                String duracion   = (hFin - hIni) + "h";

                String[] celdas = { horaInicio + " – " + horaFin, duracion, estado, metodo, coste };
                for (int col = 0; col < celdas.length; col++) {
                    gc.gridx = col; gc.gridy = fila + 1; gc.weightx = col == 0 ? 0 : 1.0;
                    JPanel celda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
                    celda.setBackground(fila % 2 == 0 ? new Color(235, 244, 255) : Color.WHITE);
                    celda.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 225, 245)));
                    JLabel lbl = new JLabel(celdas[col]);
                    lbl.setFont(FONT_NORMAL);
                    lbl.setForeground(new Color(0, 70, 160));
                    celda.add(lbl);
                    tabla.add(celda, gc);
                }
            }

            JScrollPane scrollR = new JScrollPane(tabla);
            scrollR.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 255)));
            scrollR.getVerticalScrollBar().setUnitIncrement(16);

            tabMisReservas.add(lblCab,   BorderLayout.NORTH);
            tabMisReservas.add(scrollR,  BorderLayout.CENTER);
        }

        tabMisReservas.revalidate();
        tabMisReservas.repaint();
    }

    private JPanel crearPanelMisReservasVacio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(235, 244, 255));

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 100, 180), 2, true),
            new EmptyBorder(24, 40, 24, 40)
        ));

        JLabel icono = new JLabel("📅", SwingConstants.CENTER);
        icono.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Mis Reservas", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(30, 100, 180));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Funcionalidad próximamente disponible", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitulo.setForeground(Color.GRAY);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descripcion = new JLabel(
            "<html><center>Aquí podrás consultar y gestionar<br>todas tus reservas en esta instalación.</center></html>",
            SwingConstants.CENTER);
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcion.setForeground(new Color(80, 80, 80));
        descripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        tarjeta.add(icono);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(descripcion);

        panel.add(tarjeta);
        return panel;
    }

    private JPanel crearCeldaCerrada() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        p.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        p.setBackground(COLOR_CERRADO);
        JLabel l = new JLabel("Centro cerrado");
        l.setFont(FONT_NORMAL);
        l.setForeground(new Color(150, 150, 150));
        p.add(l);
        return p;
    }

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
            // Prioridad de color: mis reservas [M] > otros socios [R] > actividades [A]
            boolean hayMia   = eventos.stream().anyMatch(e -> e.startsWith("[M]"));
            boolean hayOtros = eventos.stream().anyMatch(e -> e.startsWith("[R]"));
            if (hayMia)        p.setBackground(COLOR_MIS_RESERVAS);
            else if (hayOtros) p.setBackground(COLOR_RESERVADA);
            else               p.setBackground(COLOR_OCUPADO);
            for (int i = 0; i < eventos.size(); i++) {
                String ev    = eventos.get(i);
                boolean esMia = ev.startsWith("[M]");
                String texto = ev.replaceFirst("^\\[.\\] ", "");
                JLabel l = new JLabel(texto + (esMia ? "  ✓" : "  ✗"));
                l.setFont(FONT_NORMAL);
                l.setForeground(esMia ? new Color(0, 70, 160) : Color.BLACK);
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

    public JDialog                    getFrame()                  { return frame; }
    public JButton                    getBtnComprobar()           { return btnComprobar; }
    public JButton                    getBtnCerrar()              { return btnCerrar; }
    public JButton                    getBtnAnteriorMes()         { return btnAnteriorMes; }
    public JButton                    getBtnSiguienteMes()        { return btnSiguienteMes; }
    public JComboBox<InstalacionItem> getCmbInstalacion()         { return cmbInstalacion; }
    public JTabbedPane                getTabbedHorario()          { return tabbedHorario; }
    public JPanel                     getTabMisReservas()         { return tabMisReservas; }

    public InstalacionItem getInstalacionSeleccionada() {
        InstalacionItem item = (InstalacionItem) cmbInstalacion.getSelectedItem();
        return (item != null && item.getId() != 0) ? item : null;
    }

    public static class InstalacionItem {
        private final int    id;
        private final String nombre;
        public InstalacionItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        public int    getId()     { return id; }
        public String getNombre() { return nombre; }
        @Override public String toString() { return nombre; }
    }
}
