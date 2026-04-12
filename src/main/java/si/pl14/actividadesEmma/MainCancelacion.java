package si.pl14.actividadesEmma;

import javax.swing.UIManager;

import si.pl14.util.Database;
import si.pl14.util.DatabaseViewer;

public class MainCancelacion {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Database db = new Database();
		db.loadDatabase();
		DatabaseViewer dbv = new DatabaseViewer();
		
		// Inicializar MVC
		CancelacionActividadModel model = new CancelacionActividadModel();
		CancelacionActividadView view = new CancelacionActividadView();
		new CancelacionActividadController(model, view);

		view.setVisible(true);
		dbv.setVisible(true);

	}
}
