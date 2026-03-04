package si.pl14.reservas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import net.miginfocom.swing.MigLayout;
import java.awt.*;
import java.beans.PropertyChangeEvent; // NUEVO: Para escuchar cambios
import java.beans.PropertyChangeListener; // NUEVO
import java.text.SimpleDateFormat; // NUEVO: Para formatear la fecha
import java.util.Date; // NUEVO
import com.toedter.calendar.JCalendar;

public class ReservaView {

    private JFrame frame;

    private JTextField txtDniSocio;
    private JButton btnBuscarSocio;
    private JLabel lblNombreSocio;
    private JComboBox<Object> cbInstalaciones;
    private JCalendar calendarFecha;
    private JSpinner spinHoraInicio;
    private JSpinner spinHoraFin;
    private JButton btnComprobar;
    private JLabel lblInformacion;
    private JLabel lblPrecioTotal;
    private JRadioButton rdbtnPagoInmediato;
    private JRadioButton rdbtnCuotaMensual;
    private ButtonGroup grupoPago;
    private JButton btnCancelar;
    private JButton btnReservar;
    private JTextArea txtResumen;
    private JLabel lblFechaSeleccionada;

    private final Color COLOR_FONDO = new Color(240, 242, 245); 
    private final Color COLOR_PANEL = Color.WHITE;
    private final Color COLOR_PRIMARIO = new Color(33, 150, 243); 
    private final Color COLOR_SECUNDARIO = new Color(71, 85, 105); 
    private final Color COLOR_EXITO = new Color(46, 204, 113); 
    private final Color COLOR_TEXTO = new Color(51, 51, 51);

    public ReservaView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Reservas Instalación - ADMIN");
        frame.setName("ReservaView");
        frame.setBounds(100, 100, 800, 800); 
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(COLOR_FONDO);
        
        // Layout Principal
        frame.getContentPane().setLayout(new MigLayout("fill, insets 20", "[grow]", "[50px:n][grow]"));

        // --- 1. HEADER ---
        JPanel panelHeader = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[]"));
        panelHeader.setOpaque(false); 
        
        JLabel lblTitulo = new JLabel("Nueva Reserva");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_SECUNDARIO);
        panelHeader.add(lblTitulo, "cell 0 0");
        
        frame.getContentPane().add(panelHeader, "cell 0 0, growx");

        // --- 2. TARJETA PRINCIPAL ---
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(COLOR_PANEL);
        cardPanel.setBorder(new LineBorder(new Color(220, 220, 220), 1, true)); 
        cardPanel.setLayout(new MigLayout("fill, insets 30", "[grow][grow]", "[][][grow][][]"));
        
        frame.getContentPane().add(cardPanel, "cell 0 1, grow");

        // ==========================================
        // SECCIÓN SUPERIOR (BÚSQUEDA SOCIO)
        // ==========================================
        JPanel panelSocio = new JPanel(new MigLayout("insets 0", "[][150!][][grow]", "[]"));
        panelSocio.setOpaque(false);
        
        panelSocio.add(createLabel("DNI del Socio:"), "aligny center");
        
        txtDniSocio = new JTextField();
        txtDniSocio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelSocio.add(txtDniSocio, "growx, aligny center");
        
        btnBuscarSocio = new JButton("Buscar");
        btnBuscarSocio.setName("btnBuscarSocio");
        styleButton(btnBuscarSocio, COLOR_SECUNDARIO, Color.WHITE);
        panelSocio.add(btnBuscarSocio, "aligny center");
        
        lblNombreSocio = new JLabel("Introduce un DNI para buscar...");
        lblNombreSocio.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblNombreSocio.setForeground(new Color(150, 150, 150));
        panelSocio.add(lblNombreSocio, "gapleft 20, aligny center");
        
        cardPanel.add(panelSocio, "cell 0 0 2 1, growx, gapbottom 20");
        
        // ==========================================
        // SECCIÓN IZQUIERDA
        // ==========================================

        // -- Instalación --
        JLabel lblInstalacion = createLabel("Seleccione Instalación");
        cardPanel.add(lblInstalacion, "cell 0 1, flowy, gapbottom 5");

        cbInstalaciones = new JComboBox<>();
        cbInstalaciones.setName("cbInstalaciones");
        cbInstalaciones.setBackground(Color.WHITE);
        cbInstalaciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardPanel.add(cbInstalaciones, "cell 0 1, growx, gapbottom 20");

        // -- Calendario --
        JLabel lblFecha = createLabel("Fecha de Reserva");
        cardPanel.add(lblFecha, "cell 0 2, flowy, gapbottom 5, aligny top");

        JPanel calendarContainer = new JPanel(new BorderLayout());
        calendarContainer.setBorder(new LineBorder(new Color(230,230,230), 1));
        
        calendarFecha = new JCalendar();
        calendarFecha.setName("calendarFecha");
        calendarFecha.setDecorationBackgroundColor(Color.WHITE);
        calendarFecha.setSundayForeground(new Color(231, 76, 60));
        calendarFecha.setWeekdayForeground(COLOR_SECUNDARIO);
        
        calendarContainer.add(calendarFecha, BorderLayout.CENTER);
        cardPanel.add(calendarContainer, "cell 0 2, grow, gapbottom 20");

        // -- Resumen --
        JLabel lblResumen = createLabel("Resumen de operación");
        cardPanel.add(lblResumen, "cell 0 3, wrap"); 

        txtResumen = new JTextArea();
        txtResumen.setName("txtResumen");
        txtResumen.setEditable(false);
        txtResumen.setLineWrap(true);
        txtResumen.setWrapStyleWord(true);
        txtResumen.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtResumen.setBackground(new Color(248, 249, 250)); 
        txtResumen.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220,220,220)), 
                new EmptyBorder(10, 10, 10, 10))); 

        JScrollPane scrollPane = new JScrollPane(txtResumen);
        scrollPane.setBorder(null); 
        cardPanel.add(scrollPane, "cell 0 4, grow, hmin 100, w :100%:");

        // ==========================================
        // SECCIÓN DERECHA
        // ==========================================

        lblFechaSeleccionada = new JLabel();
        lblFechaSeleccionada.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFechaSeleccionada.setForeground(COLOR_PRIMARIO);
        lblFechaSeleccionada.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Inicializamos el calendario con la fecha actual
        actualizarLabelFecha(calendarFecha.getDate());

        calendarFecha.addPropertyChangeListener("calendar", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                actualizarLabelFecha(calendarFecha.getDate());
            }
        });

        cardPanel.add(lblFechaSeleccionada, "cell 1 2, flowy, aligny top, gapbottom 20");       
        
        // -- Horarios --
        JLabel lblHorario = createLabel("Horario Disponible");
        cardPanel.add(lblHorario, "cell 1 2, flowy, aligny top, gapbottom 10");

        JPanel panelHoras = new JPanel(new MigLayout("insets 0", "[]10[]10[]10[]", "[]"));
        panelHoras.setOpaque(false);
        
        panelHoras.add(new JLabel("De:"));
        spinHoraInicio = new JSpinner(); 
        styleSpinner(spinHoraInicio);
        panelHoras.add(spinHoraInicio, "w 60!");

        panelHoras.add(new JLabel("A:"));
        spinHoraFin = new JSpinner();
        styleSpinner(spinHoraFin);
        panelHoras.add(spinHoraFin, "w 60!");
        
        cardPanel.add(panelHoras, "cell 1 2, aligny top, wrap");
        
        // Etiqueta de Precio Total
        lblPrecioTotal = new JLabel("Precio Total: -- €");
        lblPrecioTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPrecioTotal.setForeground(COLOR_PRIMARIO);
        cardPanel.add(lblPrecioTotal, "cell 1 2, flowy, gapy 10, wrap");
        
        // Mensajes de información para las comprobaciones
        lblInformacion = new JLabel();
        lblInformacion.setForeground(new Color(231, 76, 60));
        lblInformacion.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardPanel.add(lblInformacion, "cell 1 2, flowy, wrap, w 300!, h 40!");

        // -- Pago --
        JLabel lblPago = createLabel("Método de Pago");
        cardPanel.add(lblPago, "cell 1 3, flowy, gapbottom 5");

        rdbtnPagoInmediato = new JRadioButton("Pago inmediato (Tarjeta/Efectivo)");
        rdbtnPagoInmediato.setBackground(COLOR_PANEL);
        rdbtnPagoInmediato.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rdbtnPagoInmediato.setSelected(true);

        rdbtnCuotaMensual = new JRadioButton("Cargar a la cuota mensual");
        rdbtnCuotaMensual.setBackground(COLOR_PANEL);
        rdbtnCuotaMensual.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        grupoPago = new ButtonGroup();
        grupoPago.add(rdbtnPagoInmediato);
        grupoPago.add(rdbtnCuotaMensual);

        cardPanel.add(rdbtnPagoInmediato, "cell 1 3");
        cardPanel.add(rdbtnCuotaMensual, "cell 1 3");

        // -- Botones de Acción --
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(COLOR_PANEL);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setName("btnCancelar");
        styleButton(btnCancelar, new Color(200, 200, 200), Color.BLACK); 

        btnReservar = new JButton("Confirmar Reserva");
        btnReservar.setName("btnReservar");
        styleButton(btnReservar, COLOR_EXITO, Color.WHITE); 
        btnReservar.setFont(new Font("Segoe UI", Font.BOLD, 14)); 

        panelBotones.add(btnCancelar);
        panelBotones.add(btnReservar);

        cardPanel.add(panelBotones, "cell 1 4, growx, aligny bottom");
    }

    private void actualizarLabelFecha(Date fecha) {
        if (fecha != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE d 'de' MMMM");
            String fechaStr = sdf.format(fecha);
            fechaStr = fechaStr.substring(0, 1).toUpperCase() + fechaStr.substring(1);
            lblFechaSeleccionada.setText(fechaStr);
        }
    }

    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(COLOR_SECUNDARIO);
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
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor)editor).getTextField().setHorizontalAlignment(JTextField.CENTER);
        }
    }
    
    public void setHorariosDisponibles(int horaApertura, int horaCierre) {
        int totalHoras = horaCierre - horaApertura + 1;
        Object[] horas = new Object[totalHoras + 1];
        
        horas[0] = "";
        
        for (int i = 0; i < totalHoras; i++) {
            horas[i + 1] = horaApertura + i;
        }
        
        spinHoraInicio.setModel(new SpinnerListModel(horas));
        spinHoraFin.setModel(new SpinnerListModel(horas));
    }
    
    public void setDatosSocio(String mensaje, boolean esError) {
        lblNombreSocio.setText(mensaje);
        lblNombreSocio.setFont(new Font("Segoe UI", esError ? Font.ITALIC : Font.BOLD, 14));
        lblNombreSocio.setForeground(esError ? new Color(231, 76, 60) : COLOR_TEXTO);
    }

    public JFrame getFrame() { return this.frame; }
    public String getDniBusqueda() { return this.txtDniSocio.getText().trim(); }
    public JButton getBtnBuscarSocio() { return this.btnBuscarSocio; }
    public JComboBox<Object> getCbInstalaciones() { return this.cbInstalaciones; }
    public JCalendar getCalendarFecha() { return this.calendarFecha; }
    public JButton getBtnComprobar() { return this.btnComprobar; }
    public JSpinner getSpinHoraInicio() { return this.spinHoraInicio; }
    public JSpinner getSpinHoraFin() { return this.spinHoraFin; }
    public JLabel getLblPrecioTotal() { return this.lblPrecioTotal; }
    public JLabel getLblInformacion() { return this.lblInformacion; }
    public JButton getBtnCancelar() { return this.btnCancelar; }
    public JButton getBtnReservar() { return this.btnReservar; }
    public boolean esPagoInmediato() { return this.rdbtnPagoInmediato.isSelected(); }
    public boolean esPagoCuota() { return this.rdbtnCuotaMensual.isSelected(); }
    public JTextArea getTextoResumen() { return this.txtResumen; }
    public void setTextoResumen(String mensaje) { this.txtResumen.setText(mensaje); }
    public JLabel getLblFechaSeleccionada() { return lblFechaSeleccionada; }

    public int getHoraInicio() { 
        Object val = this.spinHoraInicio.getValue();
        return (val instanceof Integer) ? (int) val : -1; 
    }
    public int getHoraFin() { 
        Object val = this.spinHoraFin.getValue();
        return (val instanceof Integer) ? (int) val : -1; 
    }
    
    public void setTextoInformacion(String mensaje) { 
        if (mensaje == null || mensaje.isEmpty()) {
            this.lblInformacion.setText("");
        } else {
            this.lblInformacion.setText("<html>" + mensaje + "</html>"); 
        }
    }
}