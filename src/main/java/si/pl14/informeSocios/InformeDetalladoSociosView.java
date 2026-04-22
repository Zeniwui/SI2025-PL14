package si.pl14.informeSocios;

import si.pl14.util.ApplicationException;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InformeDetalladoSociosView {

    private static final Color COLOR_PRIMARIO    = new Color(25, 90, 160);
    private static final Color COLOR_FILA_PAR    = new Color(235, 244, 255);
    private static final Color COLOR_DEUDA_ALTA  = new Color(255, 235, 235);
    private static final Color COLOR_DEUDA_MEDIA = new Color(255, 250, 220);
    private static final Font  FONT_NORMAL       = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BOLD         = new Font("Segoe UI", Font.BOLD, 12);

    private static final DateTimeFormatter FMT_ENTRADA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JDialog    frame;
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JButton    btnConfirmar;
    private JButton    btnGuardar;
    private JComboBox<String> cbFiltrar;
    private JPanel     panelResultados;

    public InformeDetalladoSociosView() {
        buildView();
    }

    public JTextField       getTxtFechaDesde() { return txtFechaDesde; }
    public JTextField       getTxtFechaHasta() { return txtFechaHasta; }
    public JButton          getBtnConfirmar()  { return btnConfirmar;  }
    public JButton          getBtnGuardar()    { return btnGuardar;    }
    public JComboBox<String>getCbFiltrar()     { return cbFiltrar;     }

    public void setVisible(boolean visible) { frame.setVisible(visible); }

    private void buildView() {
        frame = new JDialog((Frame) null, "Generar Informe Detallado de Socios", true);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setSize(950, 660);
        frame.setMinimumSize(new Dimension(800, 540));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.setBackground(Color.WHITE);

        root.add(buildCabecera(),      BorderLayout.NORTH);
        root.add(buildPanelBusqueda(), BorderLayout.CENTER);
        root.add(buildPanelInferior(), BorderLayout.SOUTH);  // <-- nuevo panel inferior

        frame.add(root);
    }

    // ── Panel inferior con el botón Guardar Fichero ────────────────────────
    private JPanel buildPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        panel.setBackground(new Color(245, 248, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(180, 210, 255)),
            new EmptyBorder(2, 10, 2, 10)
        ));

        btnGuardar = new JButton("Guardar Fichero");
        btnGuardar.setFont(FONT_BOLD);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Arranca deshabilitado y gris hasta que haya resultados válidos
        btnGuardar.setEnabled(false);
        btnGuardar.setBackground(new Color(180, 180, 180));
        btnGuardar.setForeground(new Color(230, 230, 230));

        panel.add(btnGuardar);
        return panel;
    }

    private JPanel buildCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARIO));

        JLabel lblTitulo = new JLabel("  Informe Detallado de Socios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 6, 0));
        cabecera.add(lblTitulo, BorderLayout.CENTER);
        return cabecera;
    }

    private JPanel buildPanelBusqueda() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 8));
        contenedor.setBackground(Color.WHITE);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelBusqueda.setBackground(new Color(245, 248, 255));
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 255), 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        JLabel lblRango = new JLabel("Rango de Fechas —");
        lblRango.setFont(FONT_BOLD);
        lblRango.setForeground(COLOR_PRIMARIO);

        LocalDate hoy    = LocalDate.now();
        LocalDate inicio = hoy.withDayOfMonth(1);

        txtFechaDesde = new JTextField(inicio.format(FMT_ENTRADA), 10);
        txtFechaDesde.setFont(FONT_NORMAL);
        txtFechaDesde.setToolTipText("Formato: dd/MM/yyyy");

        txtFechaHasta = new JTextField(hoy.format(FMT_ENTRADA), 10);
        txtFechaHasta.setFont(FONT_NORMAL);
        txtFechaHasta.setToolTipText("Formato: dd/MM/yyyy");

        btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setFont(FONT_BOLD);
        btnConfirmar.setBackground(COLOR_PRIMARIO);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setOpaque(true);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cbFiltrar = new JComboBox<>(new String[]{
            "Deuda (mayor a menor)",
            "Reservas (mayor a menor)",
            "Actividades (mayor a menor)",
            "Nombre (A-Z)"
        });
        cbFiltrar.setFont(FONT_NORMAL);

        panelBusqueda.add(lblRango);
        panelBusqueda.add(new JLabel("Desde:"));
        panelBusqueda.add(txtFechaDesde);
        panelBusqueda.add(new JLabel("Hasta:"));
        panelBusqueda.add(txtFechaHasta);
        panelBusqueda.add(btnConfirmar);
        panelBusqueda.add(Box.createHorizontalStrut(20));
        panelBusqueda.add(new JLabel("Ordenar por:"));
        panelBusqueda.add(cbFiltrar);

        panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(Color.WHITE);
        panelResultados.add(crearPanelCentrado(
            "Informe de Socios",
            "Seleccione el rango de fechas y pulse <b>Confirmar</b> para generar el informe."
        ), BorderLayout.CENTER);

        contenedor.add(panelBusqueda,   BorderLayout.NORTH);
        contenedor.add(panelResultados, BorderLayout.CENTER);
        return contenedor;
    }

    // ── Habilita/deshabilita el botón cambiando su color ──────────────────
    public void setGuardarEnabled(boolean enabled) {
        btnGuardar.setEnabled(enabled);
        if (enabled) {
            btnGuardar.setBackground(new Color(34, 139, 34));
            btnGuardar.setForeground(Color.WHITE);
        } else {
            btnGuardar.setBackground(new Color(180, 180, 180));
            btnGuardar.setForeground(new Color(230, 230, 230));
        }
    }

    public void mostrarResultados(List<InformeDetalladoSocioDTO> datos,
                                   String fechaDesde, String fechaHasta) {
        panelResultados.removeAll();

        JPanel cabRes = new JPanel(new BorderLayout(0, 2));
        cabRes.setBackground(new Color(240, 246, 255));
        cabRes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 210, 255)),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitRes = new JLabel("  Informe: " + fechaDesde + "  →  " + fechaHasta);
        lblTitRes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitRes.setForeground(COLOR_PRIMARIO);

        String textoContador = datos.isEmpty()
            ? "Sin socios con actividad en este periodo"
            : datos.size() + " socio" + (datos.size() != 1 ? "s" : "");
        JLabel lblContador = new JLabel("  " + textoContador);
        lblContador.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblContador.setForeground(datos.isEmpty() ? Color.GRAY : new Color(0, 100, 0));

        cabRes.add(lblTitRes,   BorderLayout.NORTH);
        cabRes.add(lblContador, BorderLayout.SOUTH);

        panelResultados.add(cabRes, BorderLayout.NORTH);
        panelResultados.add(
            datos.isEmpty()
                ? crearPanelCentrado("Sin resultados",
                    "No se encontraron socios con actividad en el periodo indicado.")
                : crearTabla(datos),
            BorderLayout.CENTER
        );

        panelResultados.revalidate();
        panelResultados.repaint();
    }

    public void refrescarTabla(List<InformeDetalladoSocioDTO> datos) {
        Component centro = ((BorderLayout) panelResultados.getLayout())
            .getLayoutComponent(BorderLayout.CENTER);
        if (centro != null) panelResultados.remove(centro);
        panelResultados.add(crearTabla(datos), BorderLayout.CENTER);
        panelResultados.revalidate();
        panelResultados.repaint();
    }

    public void guardarInforme(List<InformeDetalladoSocioDTO> datos) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("informe_socios.csv"));
        int res = fc.showSaveDialog(frame);
        if (res != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
            fw.write("Socio;Reservas;Actividades;Deuda (EUR);Top Instalaciones\n");
            for (InformeDetalladoSocioDTO d : datos) {
                fw.write(String.format("%s;%d;%d;%.2f;%s\n",
                    d.getNombreSocio(), d.getNumReservas(),
                    d.getNumActividades(), d.getDeuda(),
                    d.getTopInstalaciones()));
            }
        } catch (IOException e) {
            throw new ApplicationException("No se pudo guardar el informe: " + e.getMessage());
        }
    }  
    

    private JScrollPane crearTabla(List<InformeDetalladoSocioDTO> datos) {
        String[] cabeceras = {"Socio", "Reservas realizadas", "Actividades", "Deuda (EUR)", "Instalación Favorita"};
        double[] pesos     = {0.8, 0.3, 0.3, 0.3, 0.6};

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
            h.setBorder(new EmptyBorder(6, 4, 6, 4));
            tabla.add(h, gc);
        }

        double totalDeuda    = 0;
        int    totalReservas = 0;

        for (int i = 0; i < datos.size(); i++) {
            InformeDetalladoSocioDTO d = datos.get(i);
            totalDeuda    += d.getDeuda();
            totalReservas += d.getNumReservas();

            Color bgFila = calcularColorFila(d.getDeuda(), i);

            String[] celdas = {
                d.getNombreSocio(),
                String.valueOf(d.getNumReservas()),
                String.valueOf(d.getNumActividades()),
                String.format("%.2f", d.getDeuda()),
                d.getInstalacionFavorita()
            };

            for (int col = 0; col < celdas.length; col++) {
                gc.gridx = col; gc.gridy = i + 1; gc.weightx = pesos[col];
                JLabel lbl = new JLabel("  " + celdas[col]);
                lbl.setFont(FONT_NORMAL);
                if (col == 3 && d.getDeuda() > 0)
                    lbl.setForeground(new Color(180, 50, 0));
                else
                    lbl.setForeground(new Color(20, 20, 60));
                lbl.setOpaque(true);
                lbl.setBackground(bgFila);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 225, 245)),
                    new EmptyBorder(4, 2, 4, 2)
                ));
                tabla.add(lbl, gc);
            }
        }

        int   filaTot = datos.size() + 1;
        Color bgTotal = new Color(215, 230, 255);
        String[] totales = {
            "  TOTAL  (" + datos.size() + " socios)",
            String.valueOf(totalReservas),
            "",
            String.format("%.2f", totalDeuda),
            ""
        };
        for (int col = 0; col < cabeceras.length; col++) {
            gc.gridx = col; gc.gridy = filaTot; gc.weightx = pesos[col];
            JLabel lbl = new JLabel(totales[col]);
            lbl.setFont(FONT_BOLD);
            lbl.setForeground(col == 3 ? new Color(150, 40, 0) : COLOR_PRIMARIO);
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

    private static Color calcularColorFila(double deuda, int fila) {
        if (deuda > 100) return COLOR_DEUDA_ALTA;
        if (deuda > 0)   return COLOR_DEUDA_MEDIA;
        return fila % 2 == 0 ? COLOR_FILA_PAR : Color.WHITE;
    }
}