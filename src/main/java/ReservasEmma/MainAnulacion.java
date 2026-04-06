package ReservasEmma;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import si.pl14.util.Database;
import si.pl14.util.DatabaseViewer;

public class MainAnulacion {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> {

			Database db = new Database();
			db.createDatabase(false);
			db.loadDatabase();

			System.out.println("Base de datos refrescada con éxito desde data.sql");

			// mvc
			AnulacionModel model = new AnulacionModel();
			AnulacionView view = new AnulacionView();
			new AnulacionController(model, view);

			view.setVisible(true);
			new DatabaseViewer().setVisible(true);
		});
	}
}