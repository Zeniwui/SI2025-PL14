/**
 * Herramienta de depuración para visualizar el contenido de cualquier tabla.
 * Incluye:
 *   - Pestaña "Tablas (Debug)": vista genérica de todas las tablas de la BD.
 *   - Pestaña "Reservas":       vista filtrada de reservas directas de socios con
 *                                filtros combinables por usuario, rango de fechas
 *                                y estado de pago pendiente.
 *   - Pestaña "Actividades":    vista filtrada de inscripciones a actividades con
 *                                filtros combinables por usuario, rango de fechas
 *                                de la actividad y socios con pagos pendientes.
 */
package si.pl14.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseViewer extends JFrame {

    // ── Colores y fuentes ────────────────────────────────────────────────────
    private static final Color C_HEADER     = new Color(25, 90, 160);
    private static final Color C_PENDIENTE  = new Color(255, 235, 220);
    private static final Color C_FILA_PAR   = new Color(240, 246, 255);
    private static final Font  F_BOLD       = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font  F_NORMAL     = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font  F_SMALL      = new Font("Segoe UI", Font.PLAIN, 11);

    // ── Infraestructura ──────────────────────────────────────────────────────
    private final Database db = new Database();

    // ── Pestaña genérica ─────────────────────────────────────────────────────
    private JComboBox<String> cbTablas;

    // ── Pestaña Reservas ─────────────────────────────────────────────────────
    private JList<String>   lstUsuariosRes;
    private DefaultListModel<String> modeloUsuariosRes;
    private JTextField      txtDesdeRes, txtHastaRes;
    private JCheckBox       chkPendienteRes;
    private JPanel          panelTablaRes;
    private JLabel          lblContadorRes;

    // ── Pestaña Actividades ──────────────────────────────────────────────────
    private JList<String>   lstUsuariosAct;
    private DefaultListModel<String> modeloUsuariosAct;
    private JTextField      txtDesdeAct, txtHastaAct;
    private JCheckBox       chkPendienteAct;
    private JPanel          panelTablaAct;
    private JLabel          lblContadorAct;

    // ── Mapa DNI → id_socio para filtros ─────────────────────────────────────
    /** Clave: "Nombre Apellidos (DNI)"  →  Valor: id_socio */
    private final Map<String, Integer> socioIdPorNombre = new HashMap<>();

    // ─────────────────────────────────────────────────────────────────────────

    public DatabaseViewer() {
        db.createDatabase(true);
        initialize();
        cargarTablasDinamicamente();
        cargarListasUsuarios();
    }

    // =========================================================================
    // Construcción de la UI
    // =========================================================================

    private void initialize() {
        setTitle("Visor de Base de Datos (Debug)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(F_BOLD);
        tabs.addTab("🗄  Tablas (Debug)",   buildTabGenerica());
        tabs.addTab("📋  Reservas",          buildTabConFiltros(true));
        tabs.addTab("🏃  Actividades",        buildTabConFiltros(false));

        setContentPane(tabs);
    }

    // ── Tab 1: genérica ───────────────────────────────────────────────────────

    private JPanel buildTabGenerica() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.setBackground(Color.WHITE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        top.setBackground(new Color(245, 248, 255));
        top.setBorder(BorderFactory.createLineBorder(new Color(190, 215, 255)));

        top.add(etiqueta("Tabla:", F_BOLD));
        cbTablas = new JComboBox<>();
        cbTablas.setPreferredSize(new Dimension(200, 26));
        cbTablas.setFont(F_NORMAL);
        top.add(cbTablas);

        JButton btnConsola = botonEstilo("Ver en Consola", new Color(90, 90, 90));
        btnConsola.addActionListener(e -> imprimirTablaFormatoTabular());
        top.add(btnConsola);

        JButton btnVisual = botonEstilo("Ver Visualmente", C_HEADER);
        btnVisual.addActionListener(e -> mostrarTablaVisual());
        top.add(btnVisual);

        JLabel info = etiqueta(
            "  Consola: resultados en Eclipse / terminal.   Visual: ventana emergente con tabla.",
            F_SMALL);
        info.setForeground(Color.GRAY);

        p.add(top,  BorderLayout.NORTH);
        p.add(info, BorderLayout.CENTER);
        return p;
    }

    // ── Tabs 2 y 3: filtros ────────────────────────────────────────────────────

    /**
     * Construye el panel completo de una pestaña con filtros.
     *
     * @param esReservas {@code true} → pestaña Reservas; {@code false} → Actividades
     */
    private JPanel buildTabConFiltros(boolean esReservas) {
        JPanel root = new JPanel(new BorderLayout(8, 0));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setBackground(Color.WHITE);

        // Panel lateral izquierdo con los filtros
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setPreferredSize(new Dimension(230, 0));
        lateral.setBackground(new Color(248, 250, 255));
        lateral.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 215, 255)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // — Título del lateral —
        JLabel titulo = etiqueta(esReservas ? "Filtros – Reservas" : "Filtros – Actividades", F_BOLD);
        titulo.setForeground(C_HEADER);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(titulo);
        lateral.add(separador());

        // — Filtro 1: Usuarios (multi-selección) —
        JLabel lblU = etiqueta("1. Usuarios (Ctrl+clic):", F_BOLD);
        lblU.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(lblU);
        lateral.add(Box.createVerticalStrut(4));

        DefaultListModel<String> modeloU = new DefaultListModel<>();
        JList<String> lista = new JList<>(modeloU);
        lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lista.setFont(F_SMALL);
        lista.setFixedCellHeight(20);

        JScrollPane scrollU = new JScrollPane(lista);
        scrollU.setPreferredSize(new Dimension(210, 130));
        scrollU.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        scrollU.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(scrollU);

        JButton btnDesseleccionarU = botonPequeño("Quitar selección");
        btnDesseleccionarU.addActionListener(e -> lista.clearSelection());
        btnDesseleccionarU.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(Box.createVerticalStrut(3));
        lateral.add(btnDesseleccionarU);
        lateral.add(separador());

        // — Filtro 2: Rango de fechas —
        JLabel lblF = etiqueta("2. Rango de fechas:", F_BOLD);
        lblF.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(lblF);
        lateral.add(Box.createVerticalStrut(4));

        JPanel filaDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filaDe.setBackground(lateral.getBackground());
        filaDe.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaDe.add(etiqueta("Desde:", F_SMALL));
        JTextField txtDe = new JTextField(10);
        txtDe.setFont(F_SMALL);
        txtDe.setToolTipText("dd/MM/yyyy");
        filaDe.add(txtDe);
        lateral.add(filaDe);
        lateral.add(Box.createVerticalStrut(4));

        JPanel filaHa = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filaHa.setBackground(lateral.getBackground());
        filaHa.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaHa.add(etiqueta("Hasta:", F_SMALL));
        JTextField txtHa = new JTextField(10);
        txtHa.setFont(F_SMALL);
        txtHa.setToolTipText("dd/MM/yyyy");
        filaHa.add(txtHa);
        lateral.add(filaHa);

        JButton btnLimpiarFechas = botonPequeño("Limpiar fechas");
        btnLimpiarFechas.addActionListener(e -> { txtDe.setText(""); txtHa.setText(""); });
        btnLimpiarFechas.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(Box.createVerticalStrut(3));
        lateral.add(btnLimpiarFechas);
        lateral.add(separador());

        // — Filtro 3: Solo pendientes de pago —
        JCheckBox chkPend = new JCheckBox(esReservas
            ? "3. Solo pendientes de pago"
            : "3. Socios con pagos pendientes");
        chkPend.setFont(F_SMALL);
        chkPend.setBackground(lateral.getBackground());
        chkPend.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (!esReservas) {
            chkPend.setToolTipText(
                "<html>Filtra inscripciones cuyos socios<br>tienen estado_pagos = 'Pendiente'</html>");
        }
        lateral.add(chkPend);
        lateral.add(separador());

        // — Botones Aplicar / Limpiar todo —
        JButton btnAplicar = botonEstilo("▶  Aplicar filtros", new Color(34, 139, 34));
        JButton btnLimpiar = botonEstilo("✖  Limpiar todo",    new Color(160, 60, 60));
        btnAplicar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLimpiar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAplicar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnLimpiar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        lateral.add(btnAplicar);
        lateral.add(Box.createVerticalStrut(5));
        lateral.add(btnLimpiar);
        lateral.add(Box.createVerticalGlue());

        // Panel derecho con cabecera + tabla
        JPanel derecho = new JPanel(new BorderLayout(0, 6));
        derecho.setBackground(Color.WHITE);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(new Color(240, 246, 255));
        cabecera.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(180, 210, 255)),
            new EmptyBorder(6, 10, 6, 10)
        ));
        JLabel lblTitTabla = etiqueta(
            esReservas ? "Reservas directas de socios" : "Inscripciones a actividades", F_BOLD);
        lblTitTabla.setForeground(C_HEADER);
        lblTitTabla.setFont(lblTitTabla.getFont().deriveFont(13f));

        JLabel lblContador = etiqueta("–", F_SMALL);
        lblContador.setForeground(Color.GRAY);

        cabecera.add(lblTitTabla,  BorderLayout.CENTER);
        cabecera.add(lblContador,  BorderLayout.EAST);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.add(mensajeVacio(
            "Usa los filtros de la izquierda y pulsa <b>Aplicar filtros</b>."
        ), BorderLayout.CENTER);

        derecho.add(cabecera,    BorderLayout.NORTH);
        derecho.add(panelTabla,  BorderLayout.CENTER);

        root.add(lateral, BorderLayout.WEST);
        root.add(derecho, BorderLayout.CENTER);

        // Guardar referencias a los controles según la pestaña
        if (esReservas) {
            lstUsuariosRes    = lista;
            modeloUsuariosRes = modeloU;
            txtDesdeRes       = txtDe;
            txtHastaRes       = txtHa;
            chkPendienteRes   = chkPend;
            panelTablaRes     = panelTabla;
            lblContadorRes    = lblContador;

            btnAplicar.addActionListener(e -> aplicarFiltrosReservas());
            btnLimpiar.addActionListener(e -> {
                lstUsuariosRes.clearSelection();
                txtDesdeRes.setText(""); txtHastaRes.setText("");
                chkPendienteRes.setSelected(false);
                aplicarFiltrosReservas();
            });
        } else {
            lstUsuariosAct    = lista;
            modeloUsuariosAct = modeloU;
            txtDesdeAct       = txtDe;
            txtHastaAct       = txtHa;
            chkPendienteAct   = chkPend;
            panelTablaAct     = panelTabla;
            lblContadorAct    = lblContador;

            btnAplicar.addActionListener(e -> aplicarFiltrosActividades());
            btnLimpiar.addActionListener(e -> {
                lstUsuariosAct.clearSelection();
                txtDesdeAct.setText(""); txtHastaAct.setText("");
                chkPendienteAct.setSelected(false);
                aplicarFiltrosActividades();
            });
        }

        return root;
    }

    // =========================================================================
    // Carga de datos iniciales
    // =========================================================================

    private void cargarTablasDinamicamente() {
        Connection conn = null;
        try {
            conn = db.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String nombre = rs.getString("TABLE_NAME");
                if (!nombre.startsWith("sqlite_")) cbTablas.addItem(nombre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al listar tablas: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    /**
     * Carga todos los Usuarios vinculados a Socios y rellena las listas de
     * ambas pestañas. También puebla el mapa DNI → id_socio para construir
     * las cláusulas IN de los filtros.
     */
    private void cargarListasUsuarios() {
        List<Object[]> filas = db.executeQueryArray(
            "SELECT s.id_socio, u.nombre || ' ' || u.apellidos AS nomcomp, u.dni " +
            "FROM Socios s JOIN Usuarios u ON s.dni = u.dni " +
            "ORDER BY u.apellidos, u.nombre"
        );

        for (Object[] f : filas) {
            int    idSocio = f[0] instanceof Number ? ((Number) f[0]).intValue() : 0;
            String nombre  = f[1] != null ? f[1].toString() : "–";
            String dni     = f[2] != null ? f[2].toString() : "";
            String entrada = nombre + " (" + dni + ")";

            socioIdPorNombre.put(entrada, idSocio);
            modeloUsuariosRes.addElement(entrada);
            modeloUsuariosAct.addElement(entrada);
        }
    }

    // =========================================================================
    // Lógica de filtros – Reservas
    // =========================================================================

    /**
     * Construye y ejecuta la consulta de Reservas aplicando los tres filtros
     * de forma combinada según el estado actual de los controles.
     *
     * <p>Solo se incluyen reservas directas de socios
     * ({@code id_actividad IS NULL, id_socio IS NOT NULL}).
     */
    private void aplicarFiltrosReservas() {
        // 1) Recoger IDs de socios seleccionados
        List<Integer> idsSocios = getIdsSociosSeleccionados(lstUsuariosRes);

        // 2) Fechas
        String desde = txtDesdeRes.getText().trim();
        String hasta = txtHastaRes.getText().trim();
        String desdeISO = parseFecha(desde);
        String hastaISO = parseFecha(hasta);

        // 3) Solo pendientes
        boolean soloPendientes = chkPendienteRes.isSelected();

        // ── Construir SQL dinámico ────────────────────────────────────────────
        StringBuilder sql = new StringBuilder(
            "SELECT r.id_reserva, " +
            "       u.nombre || ' ' || u.apellidos AS Socio, " +
            "       u.dni                          AS DNI, " +
            "       ins.nombre                     AS Instalacion, " +
            "       r.fecha                        AS Fecha, " +
            "       r.hora_inicio || ' – ' || r.hora_fin AS Horario, " +
            "       COALESCE(CAST(r.coste_reserva AS TEXT), '–') AS Coste_EUR, " +
            "       COALESCE(r.estado_pago,  '–') AS Estado_Pago, " +
            "       COALESCE(r.metodo_pago,  '–') AS Metodo_Pago " +
            "FROM   Reservas r " +
            "JOIN   Socios       s   ON r.id_socio       = s.id_socio " +
            "JOIN   Usuarios     u   ON s.dni             = u.dni " +
            "JOIN   Instalaciones ins ON r.id_instalacion = ins.id_instalacion " +
            "WHERE  r.id_actividad IS NULL " +
            "  AND  r.id_socio IS NOT NULL "
        );
        List<Object> params = new ArrayList<>();

        if (!idsSocios.isEmpty()) {
            sql.append("AND r.id_socio IN (")
               .append(placeholders(idsSocios.size()))
               .append(") ");
            params.addAll(idsSocios.stream().map(i -> (Object) i).collect(java.util.stream.Collectors.toList()));
        }
        if (desdeISO != null) {
            sql.append("AND r.fecha >= ? ");
            params.add(desdeISO);
        }
        if (hastaISO != null) {
            sql.append("AND r.fecha <= ? ");
            params.add(hastaISO);
        }
        if (soloPendientes) {
            sql.append("AND r.estado_pago = 'Pendiente' ");
        }
        sql.append("ORDER BY r.fecha DESC, u.apellidos");

        List<Map<String, Object>> filas = db.executeQueryMap(sql.toString(), params.toArray());
        renderizarTabla(panelTablaRes, lblContadorRes, filas, "Reservas", soloPendientes, "Estado_Pago", "Pendiente");
    }

    // =========================================================================
    // Lógica de filtros – Actividades
    // =========================================================================

    /**
     * Construye y ejecuta la consulta de Inscripciones / Actividades aplicando
     * los tres filtros de forma combinada.
     *
     * <p>El filtro "pendientes" marca los socios cuyo {@code estado_pagos = 'Pendiente'}
     * en la tabla Socios, ya que las inscripciones no disponen de campo de pago propio.
     */
    private void aplicarFiltrosActividades() {
        List<Integer> idsSocios = getIdsSociosSeleccionados(lstUsuariosAct);

        String desde = txtDesdeAct.getText().trim();
        String hasta = txtHastaAct.getText().trim();
        String desdeISO = parseFecha(desde);
        String hastaISO = parseFecha(hasta);

        boolean soloPendientes = chkPendienteAct.isSelected();

        StringBuilder sql = new StringBuilder(
            "SELECT a.nombre                              AS Actividad, " +
            "       u.nombre || ' ' || u.apellidos        AS Socio, " +
            "       u.dni                                 AS DNI, " +
            "       ins.nombre                            AS Instalacion, " +
            "       a.fecha_inicio                        AS Inicio_Actividad, " +
            "       a.fecha_fin                           AS Fin_Actividad, " +
            "       COALESCE(CAST(a.precio_socio AS TEXT), '–') AS Precio_Socio_EUR, " +
            "       i.fecha_inscripcion                   AS Fecha_Inscripcion, " +
            "       s.estado_pagos                        AS Estado_Pagos_Socio " +
            "FROM   Inscripciones  i " +
            "JOIN   Socios         s   ON i.id_socio       = s.id_socio " +
            "JOIN   Usuarios       u   ON s.dni             = u.dni " +
            "JOIN   Actividades    a   ON i.id_actividad    = a.id_actividad " +
            "JOIN   Instalaciones  ins ON a.id_instalacion  = ins.id_instalacion " +
            "WHERE  1 = 1 "
        );
        List<Object> params = new ArrayList<>();

        if (!idsSocios.isEmpty()) {
            sql.append("AND i.id_socio IN (")
               .append(placeholders(idsSocios.size()))
               .append(") ");
            params.addAll(idsSocios.stream().map(i -> (Object) i).collect(java.util.stream.Collectors.toList()));
        }
        if (desdeISO != null) {
            sql.append("AND a.fecha_inicio >= ? ");
            params.add(desdeISO);
        }
        if (hastaISO != null) {
            sql.append("AND a.fecha_fin <= ? ");
            params.add(hastaISO);
        }
        if (soloPendientes) {
            sql.append("AND s.estado_pagos = 'Pendiente' ");
        }
        sql.append("ORDER BY a.fecha_inicio, u.apellidos");

        List<Map<String, Object>> filas = db.executeQueryMap(sql.toString(), params.toArray());
        renderizarTabla(panelTablaAct, lblContadorAct, filas,
            "Actividades (inscripciones)", soloPendientes, "Estado_Pagos_Socio", "Pendiente");
    }

    // =========================================================================
    // Renderizado común de tabla filtrada
    // =========================================================================

    /**
     * Sustituye el contenido del panel de resultados por una JTable con los
     * datos recibidos. Colorea en naranja pálido las filas cuya columna
     * {@code colColorear} contenga el valor {@code valorColorear}.
     */
    private void renderizarTabla(JPanel panelTabla, JLabel lblContador,
                                  List<Map<String, Object>> filas,
                                  String nombreVista,
                                  boolean resaltarPendientes,
                                  String colColorear, String valorColorear) {
        panelTabla.removeAll();

        if (filas.isEmpty()) {
            lblContador.setText("Sin resultados");
            lblContador.setForeground(Color.GRAY);
            panelTabla.add(mensajeVacio("No se encontraron registros con los filtros aplicados."),
                BorderLayout.CENTER);
            panelTabla.revalidate();
            panelTabla.repaint();
            return;
        }

        lblContador.setText(filas.size() + " registro" + (filas.size() != 1 ? "s" : ""));
        lblContador.setForeground(new Color(0, 110, 30));

        // ── Construir DefaultTableModel ───────────────────────────────────────
        List<String> cols = new ArrayList<>(filas.get(0).keySet());
        String[] colArray = cols.toArray(new String[0]);

        // Índice de la columna a colorear
        int idxColor = cols.indexOf(colColorear);

        DefaultTableModel tm = new DefaultTableModel(colArray, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Map<String, Object> fila : filas) {
            Object[] row = new Object[cols.size()];
            for (int i = 0; i < cols.size(); i++) {
                Object v = fila.get(cols.get(i));
                row[i] = v != null ? v : "–";
            }
            tm.addRow(row);
        }

        JTable tabla = new JTable(tm);
        tabla.setRowHeight(22);
        tabla.setFont(F_SMALL);
        tabla.getTableHeader().setFont(F_BOLD);
        tabla.getTableHeader().setBackground(C_HEADER);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setGridColor(new Color(210, 225, 245));
        tabla.setSelectionBackground(new Color(173, 216, 255));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setFillsViewportHeight(true);

        // Renderer que colorea filas pendientes y alterna colores pares/impares
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                if (!sel) {
                    boolean esPendiente = idxColor >= 0
                        && valorColorear.equals(String.valueOf(tm.getValueAt(row, idxColor)));
                    if (esPendiente && resaltarPendientes) {
                        setBackground(C_PENDIENTE);
                    } else {
                        setBackground(row % 2 == 0 ? C_FILA_PAR : Color.WHITE);
                    }
                }
                setBorder(new EmptyBorder(2, 6, 2, 6));
                return this;
            }
        });

        // Ajuste automático de anchos de columna
        ajustarAnchos(tabla, tm, cols);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(190, 215, 255)));

        panelTabla.add(scroll, BorderLayout.CENTER);
        panelTabla.revalidate();
        panelTabla.repaint();
    }

    // =========================================================================
    // Tab genérica – funcionalidad original (mejorada)
    // =========================================================================

    private void mostrarTablaVisual() {
        String tabla = (String) cbTablas.getSelectedItem();
        if (tabla == null) return;

        List<Map<String, Object>> filas = db.executeQueryMap("SELECT * FROM " + tabla);

        JDialog dialog = new JDialog(this, "Tabla: " + tabla.toUpperCase(), false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(950, 520);
        dialog.setLocationRelativeTo(this);

        if (filas.isEmpty()) {
            dialog.add(new JLabel("(Tabla vacía)", SwingConstants.CENTER));
            dialog.setVisible(true);
            return;
        }

        List<String> columnas = new ArrayList<>(filas.get(0).keySet());
        String[] colArray = columnas.toArray(new String[0]);

        DefaultTableModel tableModel = new DefaultTableModel(colArray, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Map<String, Object> fila : filas) {
            Object[] row = new Object[columnas.size()];
            for (int i = 0; i < columnas.size(); i++) {
                Object val = fila.get(columnas.get(i));
                row[i] = val != null ? val : "null";
            }
            tableModel.addRow(row);
        }

        JTable jTable = new JTable(tableModel);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable.setRowHeight(22);
        jTable.getTableHeader().setFont(F_BOLD);
        jTable.getTableHeader().setBackground(C_HEADER);
        jTable.getTableHeader().setForeground(Color.WHITE);
        jTable.setFont(F_NORMAL);
        jTable.setGridColor(new Color(200, 200, 200));
        jTable.setSelectionBackground(new Color(173, 216, 255));
        ajustarAnchos(jTable, tableModel, columnas);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = etiqueta(tabla.toUpperCase() + "  —  " + filas.size() + " registros", F_BOLD);
        lblInfo.setForeground(C_HEADER);
        panel.add(lblInfo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(jTable);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scroll, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void imprimirTablaFormatoTabular() {
        String tabla = (String) cbTablas.getSelectedItem();
        if (tabla == null) return;

        List<Map<String, Object>> filas = db.executeQueryMap("SELECT * FROM " + tabla);
        System.out.println("\n>>> TABLA: " + tabla.toUpperCase() + " (" + filas.size() + " registros)");

        if (filas.isEmpty()) { System.out.println("(Tabla vacía)"); return; }

        List<String> columnas = new ArrayList<>(filas.get(0).keySet());
        Map<String, Integer> anchos = new HashMap<>();
        for (String col : columnas) anchos.put(col, col.length());
        for (Map<String, Object> fila : filas)
            for (String col : columnas) {
                int len = String.valueOf(fila.get(col)).length();
                if (len > anchos.get(col)) anchos.put(col, len);
            }
        for (String col : columnas) anchos.put(col, anchos.get(col) + 3);

        for (String col : columnas) System.out.printf("%-" + anchos.get(col) + "s", col.toUpperCase());
        System.out.println();
        for (String col : columnas) System.out.print("-".repeat(anchos.get(col) - 1) + " ");
        System.out.println();
        for (Map<String, Object> fila : filas) {
            for (String col : columnas)
                System.out.printf("%-" + anchos.get(col) + "s", String.valueOf(fila.get(col)));
            System.out.println();
        }
        System.out.println();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** Devuelve los id_socio de los elementos seleccionados en la JList. */
    private List<Integer> getIdsSociosSeleccionados(JList<String> lista) {
        List<Integer> ids = new ArrayList<>();
        for (String s : lista.getSelectedValuesList()) {
            Integer id = socioIdPorNombre.get(s);
            if (id != null) ids.add(id);
        }
        return ids;
    }

    /**
     * Convierte una fecha en formato {@code dd/MM/yyyy} a {@code yyyy-MM-dd}.
     * Devuelve {@code null} si el texto está vacío o no tiene el formato correcto.
     */
    private static String parseFecha(String texto) {
        if (texto == null || texto.isEmpty()) return null;
        try {
            String[] partes = texto.split("/");
            if (partes.length != 3) return null;
            return partes[2] + "-" + partes[1] + "-" + partes[0];
        } catch (Exception e) {
            return null;
        }
    }

    /** Genera N marcadores de posición {@code ?,?,?} para una cláusula IN. */
    private static String placeholders(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) sb.append(',');
            sb.append('?');
        }
        return sb.toString();
    }

    /** Ajusta los anchos de columna en función del contenido del modelo. */
    private static void ajustarAnchos(JTable tabla, DefaultTableModel tm, List<String> cols) {
        for (int col = 0; col < cols.size(); col++) {
            int maxW = tabla.getTableHeader().getFontMetrics(F_BOLD)
                            .stringWidth(cols.get(col)) + 20;
            for (int row = 0; row < tm.getRowCount(); row++) {
                Object v = tm.getValueAt(row, col);
                int w = tabla.getFontMetrics(F_SMALL)
                             .stringWidth(v != null ? v.toString() : "") + 16;
                if (w > maxW) maxW = w;
            }
            tabla.getColumnModel().getColumn(col).setPreferredWidth(Math.min(maxW, 260));
        }
    }

    private static JLabel etiqueta(String texto, Font font) {
        JLabel l = new JLabel(texto);
        l.setFont(font);
        return l;
    }

    private static JButton botonEstilo(String texto, Color bg) {
        JButton b = new JButton(texto);
        b.setFont(F_BOLD);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static JButton botonPequeño(String texto) {
        JButton b = new JButton(texto);
        b.setFont(F_SMALL);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static Component separador() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        return sep;
    }

    private static JPanel mensajeVacio(String html) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(248, 250, 255));
        JLabel l = new JLabel("<html><center>" + html + "</center></html>", SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        l.setForeground(new Color(130, 130, 150));
        p.add(l);
        return p;
    }
}