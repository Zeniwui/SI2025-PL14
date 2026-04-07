package si.pl14.estadoPagosSocio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista del caso de uso "Visualizar Estado de Pagos del Socio".
 * Se ocupa exclusivamente de construir y actualizar los componentes
 * Swing; no contiene lógica de negocio ni acceso a datos.
 */
public class EstadoPagosSocioView {

    // ── Constantes visuales ────────────────────────────────────────────────
    static final Color COLOR_PRIMARIO       = new Color(30, 100, 180);
    static final Color COLOR_FILA_PAR       = new Color(235, 244, 255);
    static final Color COLOR_FILA_PAGADO    = new Color(235, 255, 235);
    static final Color COLOR_FILA_PENDIENTE = new Color(255, 250, 225);
    static final Color COLOR_FILA_ACTIVIDAD = new Color(240, 235, 255);
    static final Font  FONT_NORMAL          = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font  FONT_BOLD            = new Font("Segoe UI", Font.BOLD, 12);

    private static final DateTimeFormatter FMT_ENTRADA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_TABLA   = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Componentes Swing ──────────────────────────────────────────────────
    private JDialog    frame;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton    btnConfirmar;
    private JPanel     panelResultados;

    // ── Constructor ────────────────────────────────────────────────────────
    public EstadoPagosSocioView() {
        buildView();
    }

    // ── Getters de componentes (para que el Controller enganche listeners) ─
    public JTextField getBtnFechaInicio() { return txtFechaInicio; }
    public JTextField getBtnFechaFin()    { return txtFechaFin;    }
    public JButton    getBtnConfirmar()   { return btnConfirmar;   }

    public void setVisible(boolean visible) { frame.setVisible(visible); }

    // ── Construcción de la interfaz ────────────────────────────────────────
    private void buildView() {
        frame = new JDialog((Frame) null, "Estado de Pagos — Socio", true);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setMinimumSize(new Dimension(750, 480));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(Color.WHITE);

        // Cabecera
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARIO));

        JLabel lblTitulo = new JLabel("  Estado de Mis Pagos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 6, 0));

        cabecera.add(lblTitulo, BorderLayout.CENTER);

        // Valores por defecto: inicio = día 1 del mes actual, fin = hoy
        LocalDate hoy       = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        String    defInicio = inicioMes.format(FMT_ENTRADA);
        String    defFin    = hoy.format(FMT_ENTRADA);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panelBusqueda.setBackground(new Color(245, 248, 255));
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 255), 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        JLabel lblBusqueda = new JLabel("Busqueda —");
        lblBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBusqueda.setForeground(COLOR_PRIMARIO);

        txtFechaInicio = new JTextField(defInicio, 10);
        txtFechaInicio.setFont(FONT_NORMAL);
        txtFechaInicio.setToolTipText("Formato: dd/MM/yyyy");

        txtFechaFin = new JTextField(defFin, 10);
        txtFechaFin.setFont(FONT_NORMAL);
        txtFechaFin.setToolTipText("Formato: dd/MM/yyyy");

        btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setFont(FONT_BOLD);
        btnConfirmar.setBackground(COLOR_PRIMARIO);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setOpaque(true);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panelBusqueda.add(lblBusqueda);
        panelBusqueda.add(new JLabel("Fecha Inicio:"));
        panelBusqueda.add(txtFechaInicio);
        panelBusqueda.add(new JLabel("Fecha Fin:"));
        panelBusqueda.add(txtFechaFin);
        panelBusqueda.add(btnConfirmar);

        // Panel de resultados (inicialmente muestra mensaje de bienvenida)
        panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(Color.WHITE);
        panelResultados.add(crearPanelCentrado("Consulta tus pagos",
            "Pulse <b>Confirmar</b> para ver el estado de tus pagos en el periodo indicado."),
            BorderLayout.CENTER);

        JPanel norte = new JPanel(new BorderLayout(0, 8));
        norte.setBackground(Color.WHITE);
        norte.add(cabecera,      BorderLayout.NORTH);
        norte.add(panelBusqueda, BorderLayout.SOUTH);

        root.add(norte,           BorderLayout.NORTH);
        root.add(panelResultados, BorderLayout.CENTER);
        frame.add(root);
    }

    // ── Actualización de resultados ────────────────────────────────────────

    /**
     * Muestra en el panel central la tabla de cargos del periodo consultado.
     *
     * @param cargos         lista de filas [tipo, estado_pago, fecha, metodo_pago, coste]
     * @param fechaInicio    fecha de inicio formateada (dd/MM/yyyy), para el título
     * @param fechaFin       fecha de fin formateada (dd/MM/yyyy), para el título
     * @param totalPendiente importe total con estado "Pendiente"
     */
    public void mostrarResultados(List<Object[]> cargos,
                                   String fechaInicio, String fechaFin,
                                   double totalPendiente) {
        panelResultados.removeAll();

        JPanel cabRes = new JPanel(new BorderLayout(0, 2));
        cabRes.setBackground(new Color(240, 246, 255));
        cabRes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 210, 255)),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitRes = new JLabel("  Pagos: " + fechaInicio + "  ->  " + fechaFin);
        lblTitRes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitRes.setForeground(COLOR_PRIMARIO);

        JPanel infoFila = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        infoFila.setBackground(new Color(240, 246, 255));

        String textoContador = cargos.isEmpty()
            ? "Sin cargos en este periodo"
            : cargos.size() + " resultado" + (cargos.size() != 1 ? "s" : "");
        JLabel lblContador = new JLabel(textoContador);
        lblContador.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblContador.setForeground(cargos.isEmpty() ? Color.GRAY : new Color(0, 100, 0));

        JLabel lblPendiente = new JLabel("  Pendiente: " + String.format("%.2f EUR", totalPendiente));
        lblPendiente.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPendiente.setForeground(totalPendiente > 0 ? new Color(180, 80, 0) : new Color(0, 120, 0));

        infoFila.add(lblContador);
        infoFila.add(lblPendiente);
        cabRes.add(lblTitRes, BorderLayout.NORTH);
        cabRes.add(infoFila,  BorderLayout.SOUTH);

        panelResultados.add(cabRes, BorderLayout.NORTH);
        panelResultados.add(
            cargos.isEmpty()
                ? crearPanelCentrado("Sin cargos en este periodo",
                    "No se han encontrado cargos en el rango de fechas seleccionado.")
                : crearTabla(cargos),
            BorderLayout.CENTER
        );
        if (!cargos.isEmpty()) panelResultados.add(crearLeyenda(), BorderLayout.SOUTH);

        panelResultados.revalidate();
        panelResultados.repaint();
    }

    // ── Helpers privados de construcción de componentes ───────────────────

    private JScrollPane crearTabla(List<Object[]> cargos) {
        String[] cabeceras = {"Tipo", "Estado", "Fecha", "Metodo pago", "Coste"};
        double[] pesos     = {0.5, 0.5, 0, 0.6, 0};

        JPanel tabla = new JPanel(new GridBagLayout());
        tabla.setBackground(Color.WHITE);
        tabla.setBorder(new EmptyBorder(6, 8, 8, 8));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(2, 4, 2, 4);

        for (int col = 0; col < cabeceras.length; col++) {
            gc.gridx = col; gc.gridy = 0; gc.weightx = pesos[col];
            JLabel h = new JLabel("  " + cabeceras[col]);
            h.setFont(FONT_BOLD);
            h.setForeground(Color.WHITE);
            h.setOpaque(true);
            h.setBackground(COLOR_PRIMARIO);
            h.setBorder(new EmptyBorder(5, 4, 5, 4));
            tabla.add(h, gc);
        }

        double totalCoste     = 0;
        double totalPendiente = 0;

        for (int i = 0; i < cargos.size(); i++) {
            Object[] c = cargos.get(i);

            String tipo   = c[0] != null ? c[0].toString() : "-";
            String estado = c[1] != null ? c[1].toString() : "-";

            String fechaRaw = c[2] != null ? c[2].toString() : "";
            String fechaFmt = fechaRaw;
            try {
                fechaFmt = LocalDate.parse(fechaRaw).format(FMT_TABLA);
            } catch (Exception ignored) {}

            String metodo   = c[3] != null ? c[3].toString() : "-";
            double costeVal = toDouble(c[4]);
            totalCoste += costeVal;
            if ("Pendiente".equalsIgnoreCase(estado)) totalPendiente += costeVal;
            String coste = String.format("%.2f EUR", costeVal);

            Color bgFila      = calcularColorFila(tipo, estado, i);
            Color colorEstado = calcularColorEstado(estado);
            Color colorTipo   = "Actividad".equalsIgnoreCase(tipo)
                ? new Color(80, 0, 160) : new Color(20, 20, 80);

            String[] celdas = {tipo, estado, fechaFmt, metodo, coste};

            for (int col = 0; col < celdas.length; col++) {
                gc.gridx = col; gc.gridy = i + 1; gc.weightx = pesos[col];
                JLabel lbl = new JLabel("  " + celdas[col]);
                lbl.setFont(FONT_NORMAL);
                if      (col == 0) lbl.setForeground(colorTipo);
                else if (col == 1) lbl.setForeground(colorEstado);
                else               lbl.setForeground(new Color(20, 20, 80));
                lbl.setOpaque(true);
                lbl.setBackground(bgFila);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 225, 245)),
                    new EmptyBorder(4, 2, 4, 2)
                ));
                tabla.add(lbl, gc);
            }
        }

        // Fila de total
        int   filaTot = cargos.size() + 1;
        Color bgTotal = new Color(215, 230, 255);
        for (int col = 0; col < cabeceras.length; col++) {
            gc.gridx = col; gc.gridy = filaTot; gc.weightx = pesos[col];
            String texto = col == 0
                ? "  TOTAL  (" + cargos.size() + " cargo" + (cargos.size() != 1 ? "s" : "") + ")"
                : col == 4 ? String.format("  %.2f EUR", totalCoste) : "";
            JLabel lbl = new JLabel(texto);
            lbl.setFont(FONT_BOLD);
            lbl.setForeground(col == 4 ? new Color(0, 80, 0) : COLOR_PRIMARIO);
            lbl.setOpaque(true);
            lbl.setBackground(bgTotal);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, COLOR_PRIMARIO),
                new EmptyBorder(5, 4, 5, 4)
            ));
            tabla.add(lbl, gc);
        }

        // Fila de pendiente
        int   filaPend = cargos.size() + 2;
        Color bgPend   = new Color(255, 245, 210);
        for (int col = 0; col < cabeceras.length; col++) {
            gc.gridx = col; gc.gridy = filaPend; gc.weightx = pesos[col];
            String texto = col == 0 ? "  PENDIENTE"
                : col == 4 ? String.format("  %.2f EUR", totalPendiente) : "";
            JLabel lbl = new JLabel(texto);
            lbl.setFont(FONT_BOLD);
            lbl.setForeground(new Color(160, 80, 0));
            lbl.setOpaque(true);
            lbl.setBackground(bgPend);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(220, 180, 100)),
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
        leyenda.add(chip(COLOR_FILA_PAGADO,    "Reserva pagada"));
        leyenda.add(chip(COLOR_FILA_PENDIENTE, "Reserva pendiente"));
        leyenda.add(chip(COLOR_FILA_ACTIVIDAD, "Actividad"));
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

    // ── Utilidades ─────────────────────────────────────────────────────────

    private static double toDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0.0; }
    }

    private static Color calcularColorFila(String tipo, String estado, int fila) {
        if ("Actividad".equalsIgnoreCase(tipo))   return COLOR_FILA_ACTIVIDAD;
        if ("Pagado".equalsIgnoreCase(estado))    return COLOR_FILA_PAGADO;
        if ("Pendiente".equalsIgnoreCase(estado)) return fila % 2 == 0 ? COLOR_FILA_PENDIENTE : new Color(255, 255, 240);
        return fila % 2 == 0 ? COLOR_FILA_PAR : Color.WHITE;
    }

    private static Color calcularColorEstado(String estado) {
        if ("Pagado".equalsIgnoreCase(estado))    return new Color(0, 130, 0);
        if ("Pendiente".equalsIgnoreCase(estado)) return new Color(180, 80, 0);
        return new Color(100, 100, 100);
    }
}