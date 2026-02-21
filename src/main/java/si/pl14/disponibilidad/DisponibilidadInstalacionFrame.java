package si.pl14.disponibilidad;

/**
 * Punto de entrada de la pantalla "Disponibilidad de Instalación para los Socios".
 * Instancia el trío MVC y arranca el controlador.
 *
 * Uso desde SwingMain (o cualquier otro punto):
 * <pre>
 *   DisponibilidadInstalacionFrame.launch();
 * </pre>
 */
public class DisponibilidadInstalacionFrame {

    private DisponibilidadInstalacionFrame() {
        // Solo método estático de lanzamiento, no instanciar directamente
    }

    /**
     * Crea y muestra la ventana de disponibilidad.
     * Debe llamarse desde el Event Dispatch Thread (EDT).
     */
    public static void launch() {
        DisponibilidadController ctrl =
            new DisponibilidadController(
                new DisponibilidadModel(),
                new DisponibilidadView()
            );
        ctrl.initController();
    }

    /** main de prueba independiente */
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