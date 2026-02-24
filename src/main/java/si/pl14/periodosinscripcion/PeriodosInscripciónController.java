package si.pl14.periodosinscripcion;

import si.pl14.model.PeriodoInscripcionEntity;
import si.pl14.util.SwingUtil;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Controlador de "Crear Periodo de Inscripcion". Patron MVC.
 *
 * Flujo:
 *  1. Usuario rellena nombre, fecha inicio socios, fecha fin socios,
 *     fecha fin no socios.
 *  2. El resumen se actualiza en tiempo real conforme se escribe.
 *  3. "Confirmar periodo" valida (en el modelo), guarda y resetea.
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

        // Cerrar ventana
        view.getBtnCerrar().addActionListener(e -> view.getFrame().dispose());

        // Resumen en tiempo real: escuchar cambios en todos los campos
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { refrescarResumen(); }
            @Override public void removeUpdate(DocumentEvent e)  { refrescarResumen(); }
            @Override public void changedUpdate(DocumentEvent e) { refrescarResumen(); }
        };
        view.getTxtNombre()       .getDocument().addDocumentListener(dl);
        view.getTxtInicioSocios() .getDocument().addDocumentListener(dl);
        view.getTxtFinSocios()    .getDocument().addDocumentListener(dl);
        view.getTxtFinNoSocios()  .getDocument().addDocumentListener(dl);

        // Confirmar
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
        view.mostrarExito(creado.getNombre());
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
