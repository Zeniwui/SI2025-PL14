package si.pl14.periodosinscripcion;

import si.pl14.model.PeriodoInscripcionEntity;
import si.pl14.util.SwingUtil;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Controlador de "Crear Periodo de Inscripcion". Patron MVC.
 *
 * Flujo:
 *  1. Al abrir la pantalla carga en la tabla los periodos ya guardados en BD.
 *  2. El resumen se actualiza en tiempo real conforme se escribe.
 *  3. "Confirmar periodo" valida (en el modelo), guarda en BD,
 *     refresca la tabla y resetea el formulario.
 */
public class PeriodosInscripciónController {

    private final PeriodosInscripciónModel model;
    private final PeriodosInscripciónView  view;

    public PeriodosInscripciónController(PeriodosInscripciónModel m,
                                          PeriodosInscripciónView  v) {
        this.model = m;
        this.view  = v;
    }

    public void initController() {

        // Cargar periodos existentes en la tabla al abrir la pantalla
        cargarPeriodos();

        // Cerrar ventana
        view.getBtnCerrar().addActionListener(e -> view.getFrame().dispose());

        // Resumen en tiempo real
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { refrescarResumen(); }
            @Override public void removeUpdate(DocumentEvent e)  { refrescarResumen(); }
            @Override public void changedUpdate(DocumentEvent e) { refrescarResumen(); }
        };
        view.getTxtNombre()       .getDocument().addDocumentListener(dl);
        view.getTxtInicioSocios() .getDocument().addDocumentListener(dl);
        view.getTxtFinSocios()    .getDocument().addDocumentListener(dl);
        view.getTxtFinNoSocios()  .getDocument().addDocumentListener(dl);

        // Confirmar: guardar y refrescar tabla
        view.getBtnConfirmar().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::confirmar));

        view.getFrame().setVisible(true);
    }

    // ── Acciones ──────────────────────────────────────────────────────────────

    private void confirmar() {
        String nombre = view.getNombreValor();
        String iniS   = PeriodosInscripciónView.fechaValor(view.getTxtInicioSocios());
        String finS   = PeriodosInscripciónView.fechaValor(view.getTxtFinSocios());
        String finN   = PeriodosInscripciónView.fechaValor(view.getTxtFinNoSocios());

        PeriodoInscripcionEntity creado = model.crearPeriodo(nombre, iniS, finS, finN);

        // Mostrar confirmacion, resetear formulario y refrescar tabla con el nuevo periodo
        view.mostrarExito(creado.getNombre());
        cargarPeriodos();
    }

    private void cargarPeriodos() {
        view.cargarTablaPeriodos(model.getPeriodos());
    }

    private void refrescarResumen() {
        view.actualizarResumen(
            view.getNombreValor(),
            PeriodosInscripciónView.fechaValor(view.getTxtInicioSocios()),
            PeriodosInscripciónView.fechaValor(view.getTxtFinSocios()),
            PeriodosInscripciónView.fechaValor(view.getTxtFinNoSocios())
        );
    }
}