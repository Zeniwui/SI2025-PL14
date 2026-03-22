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
public class PeriodosInscripcionController {

    private final PeriodosInscripcionModel model;
    private final PeriodosInscripcionView  view;

    public PeriodosInscripcionController(PeriodosInscripcionModel m,
                                          PeriodosInscripcionView  v) {
        this.model = m;
        this.view  = v;
    }

    public void initController() {

        cargarPeriodos();

        view.getBtnCerrar().addActionListener(e -> view.getFrame().dispose());

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { refrescarResumen(); }
            @Override public void removeUpdate(DocumentEvent e)  { refrescarResumen(); }
            @Override public void changedUpdate(DocumentEvent e) { refrescarResumen(); }
        };
        view.getTxtNombre()       .getDocument().addDocumentListener(dl);
        view.getTxtDescripcion()  .getDocument().addDocumentListener(dl);
        view.getTxtInicioSocios() .getDocument().addDocumentListener(dl);
        view.getTxtFinSocios()    .getDocument().addDocumentListener(dl);
        view.getTxtFinNoSocios()  .getDocument().addDocumentListener(dl);

        view.getBtnConfirmar().addActionListener(
            e -> SwingUtil.exceptionWrapper(this::confirmar));

        view.getFrame().setVisible(true);
    }

    private void confirmar() {
        String nombre = view.getNombreValor();
        String desc   = view.getDescripcionValor();
        String iniS   = PeriodosInscripcionView.fechaValor(view.getTxtInicioSocios());
        String finS   = PeriodosInscripcionView.fechaValor(view.getTxtFinSocios());
        String finN   = PeriodosInscripcionView.fechaValor(view.getTxtFinNoSocios());

        PeriodoInscripcionEntity creado = model.crearPeriodo(nombre, desc, iniS, finS, finN);
        view.mostrarExito(creado.getNombre());
        cargarPeriodos();
    }

    private void cargarPeriodos() {
        view.cargarTablaPeriodos(model.getPeriodos());
    }

    private void refrescarResumen() {
        view.actualizarResumen(
            view.getNombreValor(),
            view.getDescripcionValor(),
            PeriodosInscripcionView.fechaValor(view.getTxtInicioSocios()),
            PeriodosInscripcionView.fechaValor(view.getTxtFinSocios()),
            PeriodosInscripcionView.fechaValor(view.getTxtFinNoSocios())
        );
    }
}