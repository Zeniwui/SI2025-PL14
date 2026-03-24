package si.pl14.visualizareservas;

import si.pl14.model.InstalacionEntity;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class VisualizarReservasSocioView {

    static final Color COLOR_PRIMARIO       = new Color(30, 100, 180);
    static final Color COLOR_FILA_PAR       = new Color(235, 244, 255);
    static final Color COLOR_FILA_PAGADO    = new Color(235, 255, 235);
    static final Color COLOR_FILA_PENDIENTE = new Color(255, 250, 225);
    static final Color COLOR_FILA_OTRO      = new Color(250, 235, 235);
    static final Font  FONT_TITULO          = new Font("Segoe UI", Font.BOLD, 15);
    static final Font  FONT_NORMAL          = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font  FONT_BOLD            = new Font("Segoe UI", Font.BOLD, 12);

    private static final DateTimeFormatter FMT_FECHA_TABLA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JDialog                    frame;
    private final JComboBox<InstalacionItem> cmbInstalacion;
    private final JTextField                 txtFechaInicio;
    private final JTextField                 txtFechaFin;
    private final JButton                    btnConfirmar;
    private final JPanel                     panelResultados;

    public VisualizarReservasSocioView() {
        frame = new JDialog((Frame) null, "Mis Reservas - Socio", true);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(820, 540));
        frame.setSize(980, 640);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(Color.WHITE);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARIO));

        JLabel lblTitulo = new JLabel("  Mis Reservas");
        lblTitulo.setFont(FONT_TITULO);
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 6, 0));

        cabecera.add(lblTitulo, BorderLayout.CENTER);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panelBusqueda.setBackground(new Color(245, 248, 255));
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 255), 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        JLabel lblInstalacion = new JLabel("Instalacion:");
        lblInstalacion.setFont(FONT_NORMAL);
        cmbInstalacion = new JComboBox<>();
        cmbInstalacion.setName("cmbInstalacion");
        cmbInstalacion.setFont(FONT_NORMAL);
        cmbInstalacion.setPreferredSize(new Dimension(210, 28));

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 26));
        sep.setForeground(new Color(180, 210, 255));

        JLabel lblBusqueda = new JLabel("Busqueda:");
        lblBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBusqueda.setForeground(COLOR_PRIMARIO);

        JLabel lblInicio = new JLabel("Fecha Inicio:");
        lblInicio.setFont(FONT_NORMAL);
        txtFechaInicio = new JTextField(10);
        txtFechaInicio.setName("txtFechaInicio");
        txtFechaInicio.setFont(FONT_NORMAL);
        txtFechaInicio.setToolTipText("Formato: dd/MM/yyyy");

        JLabel lblFin = new JLabel("Fecha Fin:");
        lblFin.setFont(FONT_NORMAL);
        txtFechaFin = new JTextField(10);
        txtFechaFin.setName("txtFechaFin");
        txtFechaFin.setFont(FONT_NORMAL);
        txtFechaFin.setToolTipText("Formato: dd/MM/yyyy");

        btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setName("btnConfirmar");
        btnConfirmar.setFont(FONT_BOLD);
        btnConfirmar.setBackground(COLOR_PRIMARIO);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setOpaque(true);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panelBusqueda.add(lblInstalacion);
        panelBusqueda.add(cmbInstalacion);
        panelBusqueda.add(sep);
        panelBusqueda.add(lblBusqueda);
        panelBusqueda.add(lblInicio);
        panelBusqueda.add(txtFechaInicio);
        panelBusqueda.add(lblFin);
        panelBusqueda.add(txtFechaFin);
        panelBusqueda.add(btnConfirmar);

        panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(Color.WHITE);
        panelResultados.add(crearPanelCentrado("Consulta tus reservas",
            "Introduce un rango de fechas y pulsa Confirmar para ver tus reservas."), BorderLayout.CENTER);

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.setBackground(Color.WHITE);
        norte.add(cabecera,      BorderLayout.NORTH);
        norte.add(panelBusqueda, BorderLayout.SOUTH);

        root.add(norte,           BorderLayout.NORTH);
        root.add(panelResultados, BorderLayout.CENTER);
        frame.add(root);
    }

    public void setInstalaciones(List<InstalacionEntity> lista) {
        cmbInstalacion.removeAllItems();
        cmbInstalacion.addItem(new InstalacionItem(0, "Todas las instalaciones"));
        for (InstalacionEntity i : lista) {
            cmbInstalacion.addItem(new InstalacionItem(i.getIdInstalacion(), i.getNombre()));
        }
    }

    public void mostrarResultados(List<ReservasSocioDTO> reservas,
                                   String fechaInicio, String fechaFin,
                                   String nombreInstalacion) {
        panelResultados.removeAll();

        JPanel cabRes = new JPanel(new BorderLayout(0, 2));
        cabRes.setBackground(new Color(240, 246, 255));
        cabRes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 210, 255)),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitRes = new JLabel("  Mis Reservas - " + nombreInstalacion);
        lblTitRes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitRes.setForeground(COLOR_PRIMARIO);

        JPanel infoPeriodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        infoPeriodo.setBackground(new Color(240, 246, 255));

        JLabel lblPeriodo = new JLabel(fechaInicio + " -> " + fechaFin);
        lblPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPeriodo.setForeground(new Color(60, 60, 60));

        String textoContador = reservas.isEmpty()
            ? "Sin reservas en este periodo"
            : reservas.size() + " reserva" + (reservas.size() != 1 ? "s" : "") +
              " encontrada" + (reservas.size() != 1 ? "s" : "");
        JLabel lblContador = new JLabel(textoContador);
        lblContador.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblContador.setForeground(reservas.isEmpty() ? Color.GRAY : new Color(0, 120, 0));

        infoPeriodo.add(lblPeriodo);
        infoPeriodo.add(lblContador);
        cabRes.add(lblTitRes,   BorderLayout.NORTH);
        cabRes.add(infoPeriodo, BorderLayout.SOUTH);

        JComponent cuerpo = reservas.isEmpty()
            ? crearPanelCentrado("Sin reservas en este periodo",
                "No se han encontrado reservas en el rango de fechas seleccionado.")
            : crearTablaResultados(reservas);

        panelResultados.add(cabRes,  BorderLayout.NORTH);
        panelResultados.add(cuerpo,  BorderLayout.CENTER);
        if (!reservas.isEmpty())
            panelResultados.add(crearLeyenda(), BorderLayout.SOUTH);

        panelResultados.revalidate();
        panelResultados.repaint();
    }

    private JScrollPane crearTablaResultados(List<ReservasSocioDTO> reservas) {
        String[] cabeceras = {"Fecha", "Dia", "Inicio", "Fin", "Instalacion",
                               "Estado pago", "Metodo pago", "Coste", "Reservado el"};
        double[] pesosCols = {0, 0.4, 0, 0, 1.2, 0.6, 0.6, 0, 1.0};

        JPanel tabla = new JPanel(new GridBagLayout());
        tabla.setBackground(Color.WHITE);
        tabla.setBorder(new EmptyBorder(6, 8, 8, 8));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(2, 4, 2, 4);

        for (int col = 0; col < cabeceras.length; col++) {
            gc.gridx = col; gc.gridy = 0; gc.weightx = pesosCols[col];
            JLabel h = new JLabel("  " + cabeceras[col]);
            h.setFont(FONT_BOLD);
            h.setForeground(Color.WHITE);
            h.setOpaque(true);
            h.setBackground(COLOR_PRIMARIO);
            h.setBorder(new EmptyBorder(5, 4, 5, 4));
            tabla.add(h, gc);
        }

        double totalCoste = 0;
        for (int i = 0; i < reservas.size(); i++) {
            ReservasSocioDTO r = reservas.get(i);

            String fechaRaw  = r.getFecha();
            String fechaFmt  = fechaRaw;
            String diaSemana = "";
            try {
                LocalDate ld = LocalDate.parse(fechaRaw);
                fechaFmt  = ld.format(FMT_FECHA_TABLA);
                String dia = ld.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
                diaSemana = Character.toUpperCase(dia.charAt(0)) + dia.substring(1).replace(".", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            String hIni     = formatHora(r.getHoraInicio());
            String hFin     = formatHora(r.getHoraFin());
            String estado   = r.getEstadoPago();
            String metodo   = r.getMetodoPago();
            double costeVal = r.getCosteReserva();
            totalCoste     += costeVal;
            String coste         = String.format("%.2f EUR", costeVal);
            String fechaCreacion = r.getFechaCreacion();
            String instalacion   = r.getNombreInstalacion();

            Color bgFila      = calcularColorFila(estado, i);
            Color colorEstado = calcularColorEstado(estado);

            String[] celdas = {fechaFmt, diaSemana, hIni, hFin, instalacion,
                                estado, metodo, coste, fechaCreacion};

            for (int col = 0; col < celdas.length; col++) {
                gc.gridx = col; gc.gridy = i + 1; gc.weightx = pesosCols[col];
                JLabel lbl = new JLabel("  " + celdas[col]);
                lbl.setFont(col == 1 ? FONT_BOLD : FONT_NORMAL);
                lbl.setForeground(col == 5 ? colorEstado : new Color(20, 20, 80));
                lbl.setOpaque(true);
                lbl.setBackground(bgFila);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 225, 245)),
                    new EmptyBorder(4, 2, 4, 2)
                ));
                tabla.add(lbl, gc);
            }
        }

        int   filaTot = reservas.size() + 1;
        Color bgTotal = new Color(215, 230, 255);
        for (int col = 0; col < cabeceras.length; col++) {
            gc.gridx = col; gc.gridy = filaTot; gc.weightx = pesosCols[col];
            String texto = col == 0
                ? "  TOTAL  (" + reservas.size() + " reserva" + (reservas.size() != 1 ? "s" : "") + ")"
                : col == 7 ? String.format("  %.2f EUR", totalCoste) : "";
            JLabel lbl = new JLabel(texto);
            lbl.setFont(FONT_BOLD);
            lbl.setForeground(col == 7 ? new Color(0, 80, 0) : COLOR_PRIMARIO);
            lbl.setOpaque(true);
            lbl.setBackground(bgTotal);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_PRIMARIO),
                new EmptyBorder(5, 4, 5, 4)
            ));
            tabla.add(lbl, gc);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 255)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    private JPanel crearPanelCentrado(String titulo, String descripcion) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 248, 255));
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2, true),
            new EmptyBorder(28, 48, 28, 48)
        ));
        JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setForeground(COLOR_PRIMARIO);
        lblTit.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblDesc = new JLabel("<html><center>" + descripcion + "</center></html>", SwingConstants.CENTER);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(90, 90, 90));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(lblTit);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(lblDesc);
        panel.add(tarjeta);
        return panel;
    }

    private JPanel crearLeyenda() {
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        leyenda.setBackground(Color.WHITE);
        leyenda.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 225, 245)));
        leyenda.add(chip(COLOR_FILA_PAGADO,    "Pagado"));
        leyenda.add(chip(COLOR_FILA_PENDIENTE, "Pendiente"));
        leyenda.add(chip(COLOR_FILA_OTRO,      "Otro estado"));
        return leyenda;
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

    private static String formatHora(String s) {
        if (s == null || s.isEmpty()) return "--:--";
        if (s.contains(":")) {
            String[] p = s.split(":");
            return p[0] + ":" + p[1];
        }
        try {
            return String.format("%02d:00", Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return s;
        }
    }

    private static Color calcularColorFila(String estado, int fila) {
        if ("Pagado".equalsIgnoreCase(estado))    return COLOR_FILA_PAGADO;
        if ("Pendiente".equalsIgnoreCase(estado)) return fila % 2 == 0 ? COLOR_FILA_PAR : Color.WHITE;
        return COLOR_FILA_OTRO;
    }

    private static Color calcularColorEstado(String estado) {
        if ("Pagado".equalsIgnoreCase(estado))    return new Color(0, 130, 0);
        if ("Pendiente".equalsIgnoreCase(estado)) return new Color(180, 100, 0);
        return new Color(100, 100, 100);
    }

    public JDialog    getFrame()          { return frame; }
    public JButton    getBtnConfirmar()   { return btnConfirmar; }
    public JTextField getTxtFechaInicio() { return txtFechaInicio; }
    public JTextField getTxtFechaFin()    { return txtFechaFin; }

    public InstalacionItem getInstalacionSeleccionada() {
        return (InstalacionItem) cmbInstalacion.getSelectedItem();
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