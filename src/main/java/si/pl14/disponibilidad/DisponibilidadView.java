package si.pl14.disponibilidad;

import si.pl14.model.InstalacionEntity;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
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
        frame.setSize(900, 920);
        frame.setMinimumSize(new Dimension(760, 780));
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

        panelCalendario = new JPanel(new BorderLayout());
        panelCalendario.setBackground(Color.WHITE);
        panelCalendario.setVisible(false);

        panelHorario = new JPanel(new BorderLayout());
        panelHorario.setBackground(Color.WHITE);
        panelHorario.setVisible(false);

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

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setBackground(Color.WHITE);
        centro.setBorder(new EmptyBorder(0, 0, 0, 0));
        centro.add(panelCalendario, BorderLayout.NORTH);
        centro.add(panelHorario,   BorderLayout.CENTER);

        root.add(topBar, BorderLayout.NORTH);
        root.add(centro, BorderLayout.CENTER);
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

        JPanel aviso = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        aviso.setBackground(new Color(255, 248, 220));
        aviso.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 180, 60), 1, true),
            new EmptyBorder(3, 8, 3, 8)
        ));
        JLabel lblAviso = new JLabel("⚠  Solo se puede consultar la disponibilidad con un máximo de 30 días de antelación.");
        lblAviso.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblAviso.setForeground(new Color(140, 90, 0));
        aviso.add(lblAviso);

        JPanel sur = new JPanel();
        sur.setLayout(new BoxLayout(sur, BoxLayout.Y_AXIS));
        sur.setBackground(Color.WHITE);
        sur.add(leyenda);
        sur.add(Box.createVerticalStrut(4));
        sur.add(aviso);

        panelCalendario.add(navMes,  BorderLayout.NORTH);
        panelCalendario.add(rejilla, BorderLayout.CENTER);
        panelCalendario.add(sur,     BorderLayout.SOUTH);
        panelCalendario.setVisible(true);
        panelCalendario.revalidate();
        panelCalendario.repaint();
    }

    public void mostrarHorario(LocalDate fecha, String nombreInstalacion,
                                Map<Integer, List<String>> ocupaciones) {

        boolean esFinDeSemana = fecha.getDayOfWeek().getValue() >= 6;
        int horaApertura = esFinDeSemana ? 9  : 8;
        int horaCierre   = esFinDeSemana ? 15 : 21;
        String horarioTexto = esFinDeSemana
            ? "Sábado/Domingo: 09:00 – 15:00"
            : "Lunes–Viernes: 08:00 – 21:00";

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
        scrollH.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollH.getVerticalScrollBar().setUnitIncrement(16);

        SwingUtilities.invokeLater(() -> {
            int posY = horaApertura * 34;
            scrollH.getVerticalScrollBar().setValue(posY);
        });

        // Leyenda — sin entrada de "Evento Social"
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leyenda.setBackground(Color.WHITE);
        leyenda.add(chip(COLOR_LIBRE,        "Libre ✓"));
        leyenda.add(chip(COLOR_OCUPADO,      "Actividad ✗"));
        leyenda.add(chip(COLOR_RESERVADA,    "Reserva de socio ✗"));
        leyenda.add(chip(COLOR_MIS_RESERVAS, "Mis reservas"));
        leyenda.add(chip(COLOR_CERRADO,      "Centro cerrado"));

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

    public void mostrarMisReservasPeriodo(String nombreInstalacion, LocalDate desde, LocalDate hasta,
                                          List<Object[]> reservas) {
        tabMisReservas.removeAll();

        DateTimeFormatter fmtFecha      = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtFechaTabla = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        JPanel cab = new JPanel(new BorderLayout(0, 2));
        cab.setBackground(new Color(240, 246, 255));
        cab.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 210, 255)),
            new EmptyBorder(8, 12, 8, 12)));

        JLabel lblTit = new JLabel("📋  Mis Reservas — " + nombreInstalacion);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTit.setForeground(COLOR_PRIMARIO);

        JPanel infoPeriodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        infoPeriodo.setBackground(new Color(240, 246, 255));

        JLabel lblPeriodo = new JLabel("📅  " + desde.format(fmtFecha) + "  →  " + hasta.format(fmtFecha));
        lblPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPeriodo.setForeground(new Color(60, 60, 60));

        JLabel lblContador = new JLabel(reservas.isEmpty()
            ? "Sin reservas en este periodo"
            : reservas.size() + " reserva" + (reservas.size() != 1 ? "s" : "") +
              " encontrada" + (reservas.size() != 1 ? "s" : ""));
        lblContador.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblContador.setForeground(reservas.isEmpty() ? Color.GRAY : new Color(0, 120, 0));

        infoPeriodo.add(lblPeriodo);
        infoPeriodo.add(lblContador);

        cab.add(lblTit,      BorderLayout.NORTH);
        cab.add(infoPeriodo, BorderLayout.SOUTH);

        if (reservas.isEmpty()) {
            JPanel panelVacio = new JPanel(new GridBagLayout());
            panelVacio.setBackground(new Color(245, 248, 255));

            JPanel tarjeta = new JPanel();
            tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
            tarjeta.setBackground(Color.WHITE);
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 100, 180), 2, true),
                new EmptyBorder(28, 48, 28, 48)));

            JLabel ico = new JLabel("📭", SwingConstants.CENTER);
            ico.setFont(new Font("Segoe UI", Font.PLAIN, 40));
            ico.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel msg = new JLabel("No tienes reservas en esta instalación en los próximos 30 días.",
                SwingConstants.CENTER);
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            msg.setForeground(new Color(90, 90, 90));
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);

            tarjeta.add(ico);
            tarjeta.add(Box.createVerticalStrut(12));
            tarjeta.add(msg);
            panelVacio.add(tarjeta);

            tabMisReservas.add(cab,        BorderLayout.NORTH);
            tabMisReservas.add(panelVacio, BorderLayout.CENTER);

        } else {
            String[] headers = {"Nº", "Fecha", "Día", "Inicio", "Fin", "Duración", "Estado pago", "Método pago", "Coste", "Reservado el"};
            double[] colWeights = { 0, 0, 0.8, 0, 0, 0, 0.8, 0.8, 0, 1.0 };

            JPanel tabla = new JPanel(new GridBagLayout());
            tabla.setBackground(Color.WHITE);
            tabla.setBorder(new EmptyBorder(6, 8, 8, 8));
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill   = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(2, 5, 2, 5);

            Color bgHeader = new Color(30, 100, 180);
            for (int col = 0; col < headers.length; col++) {
                gc.gridx = col; gc.gridy = 0; gc.weightx = colWeights[col];
                JLabel h = new JLabel("  " + headers[col]);
                h.setFont(new Font("Segoe UI", Font.BOLD, 12));
                h.setForeground(Color.WHITE);
                h.setOpaque(true);
                h.setBackground(bgHeader);
                h.setBorder(new EmptyBorder(5, 4, 5, 4));
                tabla.add(h, gc);
            }

            double totalCoste = 0;

            for (int filaN = 0; filaN < reservas.size(); filaN++) {
                Object[] r = reservas.get(filaN);
                String idReserva     = r[0] != null ? "#" + r[0].toString() : "-";
                String fechaRaw      = r[1] != null ? r[1].toString() : "";
                String hIni          = r[2] != null ? r[2].toString() : "--";
                String hFin          = r[3] != null ? r[3].toString() : "--";
                int    durHoras      = r[4] instanceof Number ? ((Number)r[4]).intValue() : 0;
                String duracion      = durHoras + "h";
                String estado        = r[5] != null ? r[5].toString() : "-";
                String metodo        = r[6] != null ? r[6].toString() : "—";
                double costeVal      = r[7] instanceof Number ? ((Number)r[7]).doubleValue() : 0.0;
                String coste         = String.format("%.2f €", costeVal);
                String fechaCreacion = r[8] != null ? r[8].toString() : "-";
                totalCoste += costeVal;

                String fechaFmt  = fechaRaw;
                String diaSemana = "";
                try {
                    LocalDate ld = LocalDate.parse(fechaRaw);
                    fechaFmt  = ld.format(fmtFechaTabla);
                    diaSemana = ld.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
                    diaSemana = Character.toUpperCase(diaSemana.charAt(0)) + diaSemana.substring(1).replace(".", "");
                } catch (Exception ignored) {}

                Color bgFila;
                if      ("Pagado".equalsIgnoreCase(estado))    bgFila = new Color(235, 255, 235);
                else if ("Pendiente".equalsIgnoreCase(estado)) bgFila = filaN % 2 == 0 ? new Color(235, 244, 255) : Color.WHITE;
                else                                            bgFila = new Color(255, 250, 230);

                Color colorEstado;
                if      ("Pagado".equalsIgnoreCase(estado))    colorEstado = new Color(0, 130, 0);
                else if ("Pendiente".equalsIgnoreCase(estado)) colorEstado = new Color(180, 100, 0);
                else                                            colorEstado = new Color(100, 100, 100);

                String[] celdas = { idReserva, fechaFmt, diaSemana, hIni, hFin, duracion, estado, metodo, coste, fechaCreacion };

                for (int col = 0; col < celdas.length; col++) {
                    gc.gridx = col; gc.gridy = filaN + 1; gc.weightx = colWeights[col];
                    JLabel lbl = new JLabel("  " + celdas[col]);
                    lbl.setFont(col == 2 ? new Font("Segoe UI", Font.BOLD, 12) : FONT_NORMAL);
                    lbl.setForeground(col == 6 ? colorEstado : new Color(20, 20, 80));
                    lbl.setOpaque(true);
                    lbl.setBackground(bgFila);
                    lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 225, 245)),
                        new EmptyBorder(4, 2, 4, 2)));
                    tabla.add(lbl, gc);
                }
            }

            int filaTotal = reservas.size() + 1;
            Color bgTotal = new Color(215, 230, 255);

            gc.gridx = 0; gc.gridy = filaTotal; gc.gridwidth = 8; gc.weightx = 1.0;
            JLabel lblTotalTxt = new JLabel("  TOTAL  (" + reservas.size()
                + " reserva" + (reservas.size() != 1 ? "s" : "") + ")");
            lblTotalTxt.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblTotalTxt.setForeground(COLOR_PRIMARIO);
            lblTotalTxt.setOpaque(true);
            lblTotalTxt.setBackground(bgTotal);
            lblTotalTxt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_PRIMARIO),
                new EmptyBorder(5, 4, 5, 4)));
            tabla.add(lblTotalTxt, gc);

            gc.gridx = 8; gc.gridy = filaTotal; gc.gridwidth = 1; gc.weightx = 0;
            JLabel lblTotalCoste = new JLabel(String.format("  %.2f €", totalCoste));
            lblTotalCoste.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblTotalCoste.setForeground(new Color(0, 80, 0));
            lblTotalCoste.setOpaque(true);
            lblTotalCoste.setBackground(bgTotal);
            lblTotalCoste.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_PRIMARIO),
                new EmptyBorder(5, 4, 5, 4)));
            tabla.add(lblTotalCoste, gc);

            gc.gridx = 9; gc.gridy = filaTotal; gc.gridwidth = 1; gc.weightx = 1.0;
            JLabel lblTotalBlank = new JLabel("");
            lblTotalBlank.setOpaque(true);
            lblTotalBlank.setBackground(bgTotal);
            lblTotalBlank.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_PRIMARIO));
            tabla.add(lblTotalBlank, gc);

            JScrollPane scrollR = new JScrollPane(tabla);
            scrollR.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 255)));
            scrollR.getVerticalScrollBar().setUnitIncrement(16);
            scrollR.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            tabMisReservas.add(cab,     BorderLayout.NORTH);
            tabMisReservas.add(scrollR, BorderLayout.CENTER);
        }

        tabMisReservas.revalidate();
        tabMisReservas.repaint();
        panelHorario.setVisible(true);
        panelHorario.revalidate();
        panelHorario.repaint();
        tabbedHorario.setSelectedIndex(1);
    }

    private JPanel crearPanelMisReservasVacio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(235, 244, 255));

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 100, 180), 2, true),
            new EmptyBorder(24, 40, 24, 40)));

        JLabel icono = new JLabel("📅", SwingConstants.CENTER);
        icono.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Mis Reservas", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(30, 100, 180));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descripcion = new JLabel(
            "<html><center>Aquí podrás consultar y gestionar<br>todas tus reservas en esta instalación.</center></html>",
            SwingConstants.CENTER);
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcion.setForeground(new Color(80, 80, 80));
        descripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        tarjeta.add(icono);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(titulo);
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
            // Prioridad de color: mis reservas [M] > reserva otro socio [R] > actividad [A]
            boolean hayMia    = eventos.stream().anyMatch(e -> e.startsWith("[M]"));
            boolean hayOtros  = eventos.stream().anyMatch(e -> e.startsWith("[R]"));
            if      (hayMia)   p.setBackground(COLOR_MIS_RESERVAS);
            else if (hayOtros) p.setBackground(COLOR_RESERVADA);
            else               p.setBackground(COLOR_OCUPADO);

            for (int i = 0; i < eventos.size(); i++) {
                String ev     = eventos.get(i);
                boolean esMia = ev.startsWith("[M]");
                String texto  = ev.replaceFirst("^\\[.\\] ", "");
                String icono  = esMia ? "  ✓" : "  ✗";
                JLabel l = new JLabel(texto + icono);
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

    public JDialog                    getFrame()              { return frame; }
    public JButton                    getBtnComprobar()       { return btnComprobar; }
    public JButton                    getBtnCerrar()          { return btnCerrar; }
    public JButton                    getBtnAnteriorMes()     { return btnAnteriorMes; }
    public JButton                    getBtnSiguienteMes()    { return btnSiguienteMes; }
    public JComboBox<InstalacionItem> getCmbInstalacion()     { return cmbInstalacion; }
    public JTabbedPane                getTabbedHorario()      { return tabbedHorario; }
    public JPanel                     getTabMisReservas()     { return tabMisReservas; }

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