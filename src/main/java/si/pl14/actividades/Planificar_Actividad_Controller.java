package si.pl14.actividades;

import si.pl14.model.ActividadEntity;
import si.pl14.util.SwingUtil;

public class Planificar_Actividad_Controller {
    private Planificar_Actividad_Model model;
    private Planificar_Actividad_Admin_View view;

    public Planificar_Actividad_Controller(Planificar_Actividad_Model m, Planificar_Actividad_Admin_View v) {
        this.model = m;
        this.view = v;
        this.initView();
    }

    public void initController() {
        // boton crear
        view.getBtnCrear().addActionListener(e -> SwingUtil.exceptionWrapper(() -> crearActividad()));
    }

    public void initView() {
        // Hace visible la ventana
        view.setVisible(true);
    }

    private void crearActividad() {
        // Crear la entidad y mapear los campos de la vista
        ActividadEntity actividad = new ActividadEntity();
        
        actividad.setNombre(view.getNombre());
        actividad.setDescripcion(view.getDescripcion());
        actividad.setAforo(Integer.parseInt(view.getAforo()));
        actividad.setFechaInicio(view.getFechaInicio());
        actividad.setFechaFin(view.getFechaFin());
        actividad.setPrecioSocio(Double.parseDouble(view.getPrecioSocio()));
        actividad.setPrecioNoSocio(Double.parseDouble(view.getPrecioNoSocio()));
        
        // Valores fijos de ejemplo
        actividad.setIdInstalacion(1); 
        actividad.setIdPeriodo(1);

        // Llamar al modelo para insertar en BD
        model.insertarActividad(actividad);

        // mensaje de aviso para saber que funcionó
        javax.swing.JOptionPane.showMessageDialog(view, "Actividad creada correctamente");
        
    }

}
