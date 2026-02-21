package si.pl14.actividades;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import si.pl14.model.ActividadEntity;
import si.pl14.util.ApplicationException;

public class Planificar_Actividad_Controller {
	private Planificar_Actividad_Model model;
	private Planificar_Actividad_Admin_View view;
	private List<HorarioEntity> horariosTemporales = new ArrayList<>();

	public Planificar_Actividad_Controller(Planificar_Actividad_Model m, Planificar_Actividad_Admin_View v) {
		this.model = m;
		this.view = v;
		this.initView();
	}

	public void initController() {
		view.cargarPeriodos(model.obtenerPeriodos());
		view.cargarInstalaciones(model.obtenerInstalaciones());

		// Botón para añadir horario a la lista
		view.getBtnCrearHorario().addActionListener(e -> {
			HorarioEntity h = new HorarioEntity();
			h.setDiaSemana(view.getDiaSemana());
			h.setHoraInicio(view.getHoraInicio());
			h.setHoraFin(view.getHoraFin());

			horariosTemporales.add(h);
			view.getListModelHorarios().addElement(h.getDiaSemana() + ": " + h.getHoraInicio() + "-" + h.getHoraFin());
		});

		// Botón Crear Actividad
		view.getBtnCrear().addActionListener(e -> {
			try {
				if (horariosTemporales.isEmpty())
					throw new ApplicationException("Debe añadir al menos un horario.");

				ActividadEntity actividad = mappearActividadDesdeVista();
				model.insertarActividad(actividad, horariosTemporales);

				JOptionPane.showMessageDialog(view, "Actividad y horarios creados correctamente");
			} catch (ApplicationException ex) {
				JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		view.getCbPeriodoInscripcion().addActionListener(e -> {
			int id = view.getIdPeriodoSeleccionado();
			if (id > 0) {
				Object[] datos = model.obtenerDetallesPeriodo(id);
				if (datos != null) {
					String inicio = datos[0].toString();
					String fin = datos[1].toString();

					// 1. Actualizamos los campos de texto de la fecha
					view.getTxtFechaInicio().setText(inicio);
					view.getTxtFechaFin().setText(fin);

					// 2. Actualizamos el label de información inferior
					view.setTextoFechas("<html>Periodo de validez:<br>Desde " + inicio + " hasta " + fin + "</html>");
				}
			}
		});

		if (view.getCbPeriodoInscripcion().getItemCount() > 0) {
			view.getCbPeriodoInscripcion().setSelectedIndex(0);
		}

		// boton eliminar horario seleccionado
		view.getBtnEliminarHorario().addActionListener(e -> {
			int index = view.getIndiceHorarioSeleccionado();

			if (index != -1) { // -1 significa que no hay nada seleccionado
				// 1. Lo eliminamos de la lista lógica (horariosTemporales)
				horariosTemporales.remove(index);

				// 2. Lo eliminamos del modelo de la lista visual
				view.getListModelHorarios().remove(index);
			} else {
				JOptionPane.showMessageDialog(view, "Por favor, seleccione un horario de la lista para eliminarlo.");
			}
		});

	}

	public void initView() {
		// Hace visible la ventana
		view.setVisible(true);
	}

	private ActividadEntity mappearActividadDesdeVista() {
		ActividadEntity actividad = new ActividadEntity();

		actividad.setNombre(view.getNombre());
		actividad.setDescripcion(view.getDescripcion());
		try {
			actividad.setAforo(Integer.parseInt(view.getAforo()));
			actividad.setPrecioSocio(Double.parseDouble(view.getPrecioSocio()));
			actividad.setPrecioNoSocio(Double.parseDouble(view.getPrecioNoSocio()));
		} catch (NumberFormatException e) {
			throw new ApplicationException("El aforo y los precios deben ser valores numéricos válidos.");
		}

		// Mapeo de fechas (formato AAAA-MM-DD)
		actividad.setFechaInicio(view.getFechaInicio());
		actividad.setFechaFin(view.getFechaFin());

		// seleccionar id
		actividad.setIdInstalacion(view.getIdInstalacionSeleccionada());
		actividad.setIdPeriodo(view.getIdPeriodoSeleccionado());

		return actividad;
	}
}
