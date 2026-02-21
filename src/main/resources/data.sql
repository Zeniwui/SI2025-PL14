-- Datos iniciales para pruebas

-- Ejemplo original (carreras)
delete from carreras;
insert into carreras(id,inicio,fin,fecha,descr) values 
	(100,'2016-10-05','2016-10-25','2016-11-09','finalizada'),
	(101,'2016-10-05','2016-10-25','2016-11-10','en fase 3'),
	(102,'2016-11-05','2016-11-09','2016-11-20','en fase 2'),
	(103,'2016-11-10','2016-11-15','2016-11-21','en fase 1'),
	(104,'2016-11-11','2016-11-15','2016-11-22','antes inscripcion');

-- Usuarios de prueba
delete from Horarios;
delete from Reservas;
delete from Actividades;
delete from PeriodosInscripcion;
delete from Instalaciones;
delete from Socios;
delete from Usuarios;

insert into Usuarios(dni, nombre, apellidos, telefono, email) values
	('11111112A', 'Carlos',  'Garcia Lopez',    600111111, 'carlosgarc@email.com'),
	('22222223B', 'Maria',   'Fernandez Ruiz',  600222222, 'mariafr@email.com'),
	('33333334C', 'Pedro',   'Martinez Sanz',   600333333, 'pedropedri@email.com');

insert into Socios(id_socio, dni, contrasena, estado_pagos) values
	(1, '11111112A', '1234', 'Al Corriente'),
	(2, '22222223B', '1234', 'Al Corriente'),
	(3, '33333334C', '1234', 'Pendiente');

-- Instalaciones
insert into Instalaciones(nombre, tipo, coste_hora) values
	('Pista de Tenis 1',    'Tenis',    10.0),
	('Pista de Tenis 2',    'Tenis',    10.0),
	('Piscina Cubierta',    'Natacion',  5.0),
	('Gimnasio Principal',  'Fitness',   3.0),
	('Pista de Padel',      'Padel',    12.0),
	('Polideportivo',       'Multiples', 8.0);

-- Periodos de inscripcion
insert into PeriodosInscripcion(nombre, inicio_socios, fin_socios, fin_no_socios) values
	('Temporada 2026', '2026-01-01', '2026-06-30', '2026-07-31');

-- Actividades (asociadas a instalaciones)
insert into Actividades(nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) values
	('Torneo de Badminton',  'Torneo junior de badminton',   6, 20, '2026-02-01', '2026-06-13', 15.0, 25.0, 1),
	('Torneo de Voley',      'Torneo de voley playa',        6, 30, '2026-02-01', '2026-03-29', 10.0, 20.0, 1),
	('Clase de Natacion',    'Clases para adultos',          3, 15, '2026-02-01', '2026-02-11',  8.0, 15.0, 1),
	('Yoga Matinal',         'Sesion de yoga por la mañana', 4, 10, '2026-02-01', '2026-06-13',  5.0, 10.0, 1);

-- Reservas de prueba (usando fecha de hoy +/- dias para que aparezcan en el calendario)
-- Reserva de socio en Pista de Tenis 1 (id_instalacion=1) hoy
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(1, date('now'),        '10:00', '11:00', 1, 10.0, 'Pagado',   'Tarjeta'),
	(1, date('now'),        '12:00', '13:00', 2, 10.0, 'Pendiente','Efectivo'),
	(1, date('now','+1 day'),'08:00','10:00', 1, 10.0, 'Pendiente','Tarjeta'),
	(5, date('now'),        '16:00', '17:00', 3, 12.0, 'Pagado',   'Tarjeta'),
	(5, date('now','+2 day'),'18:00','19:00', 2, 12.0, 'Pendiente','Efectivo');

-- Reservas de actividades en el Polideportivo (id_instalacion=6)
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now'),         '10:00', '11:00', 1, 0.0, 'Cuota'),
	(6, date('now'),         '11:00', '12:00', 2, 0.0, 'Cuota'),
	(6, date('now','+1 day'),'10:00', '11:00', 1, 0.0, 'Cuota'),
	(3, date('now'),         '08:00', '09:00', 3, 0.0, 'Cuota'),
	(4, date('now'),         '09:00', '10:00', 4, 0.0, 'Cuota');