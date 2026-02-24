package si.pl14.periodosinscripcion;

/**
 * Punto de entrada de la pantalla "Crear Periodo de Inscripcion".
 *
 * Historia de usuario de REDKANBAN:
 *   "Como administración quiero crear un periodo de inscripción para una actividad.
 *    Se definirá la fecha de inicio y final del periodo de inscripción para socios,
 *    y la fecha final del periodo de inscripción para no socios."
 *
 * Los periodos se crean independientemente de las actividades.(Correción en base a lo dicho en el ONENOTE por el profe)
 * Al crear una actividad se le asigna uno de los periodos existentes.
 *
 * Uso:
 *   PeriodosInscripcionFrame.launch();
 */
public class PeriodosInscripciónFrame {

    private PeriodosInscripciónFrame() { }

    
     //Crea y muestra la ventana. Llamar desde el EDT.
     
    public static void launch() {
        PeriodosInscripciónController ctrl =
            new PeriodosInscripciónController(
                new PeriodosInscripciónModel(),
                new PeriodosInscripciónView()
            );
        ctrl.initController();
    }

    // main de prueba independiente 
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            launch();
        });
    }
}
