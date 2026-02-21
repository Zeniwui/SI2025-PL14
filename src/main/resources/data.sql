-- Datos iniciales para pruebas

-- Ejemplo original (carreras)
delete from carreras;
insert into carreras(id,inicio,fin,fecha,descr) values
	(100,'2016-10-05','2016-10-25','2016-11-09','finalizada'),
	(101,'2016-10-05','2016-10-25','2016-11-10','en fase 3'),
	(102,'2016-11-05','2016-11-09','2016-11-20','en fase 2'),
	(103,'2016-11-10','2016-11-15','2016-11-21','en fase 1'),
	(104,'2016-11-11','2016-11-15','2016-11-22','antes inscripcion');

-- Limpiar tablas en orden correcto por dependencias
delete from Horarios;
delete from Reservas;
delete from Actividades;
delete from PeriodosInscripcion;
delete from Instalaciones;
delete from Socios;
delete from Usuarios;

-- ─── Usuarios ────────────────────────────────────────────────────────────────
-- 00000000Z = el usuario actual de la aplicacion (id_socio = 0)
insert into Usuarios(dni, nombre, apellidos, telefono, email) values
	('00000000Z', 'Usuario',  'Actual',          600000000, 'yo@email.com'),
	('11111112A', 'Carlos',   'Garcia Lopez',    600111111, 'carlosgarc@email.com'),
	('22222223B', 'Maria',    'Fernandez Ruiz',  600222222, 'mariafr@email.com'),
	('33333334C', 'Pedro',    'Martinez Sanz',   600333333, 'pedropedri@email.com'),
	('44444445D', 'Ana',      'Lopez Morales',   600444444, 'analopez@email.com'),
	('55555556E', 'Luis',     'Gomez Perez',     600555555, 'luisgomez@email.com'),
	('66666667F', 'Sofia',    'Ruiz Navarro',    600666666, 'sofiaruiz@email.com');

-- ─── Socios ───────────────────────────────────────────────────────────────────
-- id_socio = 0 es el usuario que usa la aplicacion
insert into Socios(id_socio, dni, contrasena, estado_pagos) values
	(0, '00000000Z', '1234', 'Al Corriente'),
	(1, '11111112A', '1234', 'Al Corriente'),
	(2, '22222223B', '1234', 'Al Corriente'),
	(3, '33333334C', '1234', 'Pendiente'),
	(4, '44444445D', '1234', 'Al Corriente'),
	(5, '55555556E', '1234', 'Al Corriente'),
	(6, '66666667F', '1234', 'Al Corriente');

-- ─── Instalaciones ───────────────────────────────────────────────────────────
-- id_instalacion por AUTOINCREMENT: 1..6
insert into Instalaciones(nombre, tipo, coste_hora) values
	('Pista de Tenis 1',    'Tenis',     10.0),
	('Pista de Tenis 2',    'Tenis',     10.0),
	('Piscina Cubierta',    'Natacion',   5.0),
	('Gimnasio Principal',  'Fitness',    3.0),
	('Pista de Padel',      'Padel',     12.0),
	('Polideportivo',       'Multiples',  8.0);

-- ─── Periodos de inscripcion ──────────────────────────────────────────────────
insert into PeriodosInscripcion(nombre, inicio_socios, fin_socios, fin_no_socios) values
	('Temporada 2026', '2026-01-01', '2026-06-30', '2026-07-31');

-- ─── Actividades ──────────────────────────────────────────────────────────────
-- Tipo A: actividades regulares (es_evento_social=0)
-- Tipo B: turno manana + turno tarde (es_evento_social=0)
-- Tipo C: eventos sociales +2 horas (es_evento_social=1)
insert into Actividades(nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo, es_evento_social) values
	('Torneo de Badminton',        'Torneo junior de badminton (2h)',           6, 20, '2026-02-01', '2026-06-13', 15.0, 25.0, 1, 0),
	('Natacion Intensiva',         'Entrenamiento intensivo de natacion (2h)',  3, 12, '2026-02-01', '2026-06-30',  9.0, 16.0, 1, 0),
	('Spinning Avanzado',          'Clase de spinning alto rendimiento (2h)',   4, 15, '2026-02-01', '2026-06-30',  8.0, 14.0, 1, 0),
	('Padel Dobles',               'Partido oficial de padel por parejas (2h)', 5,  4, '2026-02-01', '2026-06-30', 12.0, 20.0, 1, 0),
	('Yoga Matinal',               'Sesion de yoga por la manana (1h)',         4, 10, '2026-02-01', '2026-06-13',  5.0, 10.0, 1, 0),
	('Yoga Vespertino',            'Sesion de yoga vespertina (1h)',             4, 10, '2026-02-01', '2026-06-13',  5.0, 10.0, 1, 0),
	('Aquagym Manana',             'Aquagym turno de manana (1h)',              3, 20, '2026-02-01', '2026-06-30',  6.0, 11.0, 1, 0),
	('Aquagym Tarde',              'Aquagym turno de tarde (1h)',               3, 20, '2026-02-01', '2026-06-30',  6.0, 11.0, 1, 0),
	-- Eventos sociales (es_evento_social=1, todos duran mas de 2h)
	('Torneo de Voley Playa',      'Torneo social de voley (4h)',               6, 40, '2026-02-01', '2026-03-29', 10.0, 20.0, 1, 1),
	('Gala de Natacion',           'Exhibicion y competicion natacion (3h)',    3, 60, '2026-02-01', '2026-06-30', 18.0, 30.0, 1, 1),
	('Fiesta de Fin de Temporada', 'Celebracion anual del club (4h)',           6, 80, '2026-02-01', '2026-06-30',  5.0, 15.0, 1, 1),
	('Gran Prix de Natacion',      'Competicion anual por equipos (3h)',        3, 50, '2026-02-01', '2026-06-30', 12.0, 25.0, 1, 1),
	('Torneo Mixto de Padel',      'Torneo social mixto de padel (3h)',         5, 16, '2026-02-01', '2026-06-30', 14.0, 28.0, 1, 1),
	('Olimpiada del Club',         'Competicion multideporte entre socios (5h)',6, 100,'2026-02-01', '2026-06-30',  8.0, 20.0, 1, 1);

-- ─── RESERVAS DE SOCIOS ───────────────────────────────────────────────────────
-- Socio 0 (usuario actual) tiene reservas en TODAS las instalaciones
-- repartidas a lo largo de los 30 dias para que "Mis Reservas" muestre datos reales

-- ── HOY ───────────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	-- Pista Tenis 1: otros socios + socio 0
	(1, date('now'), '10:00', '11:00', 1, 10.0, 'Pagado',    'Tarjeta'),
	(1, date('now'), '12:00', '13:00', 2, 10.0, 'Pendiente', 'Efectivo'),
	(1, date('now'), '15:00', '16:00', 3, 10.0, 'Pagado',    'Tarjeta'),
	(1, date('now'), '18:00', '19:00', 4, 10.0, 'Pendiente', 'Tarjeta'),
	(1, date('now'), '14:00', '15:00', 0, 10.0, 'Pagado',    'Tarjeta'),   -- socio 0
	(1, date('now'), '17:00', '18:00', 0, 10.0, 'Pendiente', 'Efectivo'),  -- socio 0
	-- Pista de Padel
	(5, date('now'), '11:00', '12:00', 5, 12.0, 'Pendiente', 'Efectivo'),
	(5, date('now'), '16:00', '17:00', 6, 12.0, 'Pagado',    'Tarjeta'),
	(5, date('now'), '13:00', '14:00', 0, 12.0, 'Pagado',    'Tarjeta'),   -- socio 0
	-- Piscina Cubierta
	(3, date('now'), '11:00', '12:00', 2,  5.0, 'Pendiente', 'Efectivo'),
	(3, date('now'), '10:00', '11:00', 0,  5.0, 'Pagado',    'Tarjeta'),   -- socio 0
	-- Gimnasio
	(4, date('now'), '08:00', '09:00', 4,  3.0, 'Pagado',    'Tarjeta'),
	(4, date('now'), '10:00', '11:00', 5,  3.0, 'Pagado',    'Tarjeta'),
	(4, date('now'), '12:00', '13:00', 6,  3.0, 'Pendiente', 'Efectivo'),
	(4, date('now'), '16:00', '17:00', 0,  3.0, 'Pagado',    'Tarjeta');   -- socio 0

-- ── HOY +1 ────────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(1, date('now','+1 day'), '08:00', '10:00', 1, 20.0, 'Pendiente', 'Tarjeta'),
	(1, date('now','+1 day'), '12:00', '13:00', 3, 10.0, 'Pendiente', 'Efectivo'),
	(1, date('now','+1 day'), '11:00', '12:00', 0, 10.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(3, date('now','+1 day'), '09:00', '10:00', 0,  5.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(5, date('now','+1 day'), '20:00', '21:00', 4, 12.0, 'Pendiente', 'Tarjeta'),
	(5, date('now','+1 day'), '16:00', '17:00', 5, 12.0, 'Pagado',    'Tarjeta');

-- ── HOY +2 ────────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(1, date('now','+2 day'), '09:00', '10:00', 2, 10.0, 'Pendiente', 'Tarjeta'),
	(1, date('now','+2 day'), '13:00', '14:00', 6, 10.0, 'Pendiente', 'Efectivo'),
	(5, date('now','+2 day'), '18:00', '19:00', 3, 12.0, 'Pendiente', 'Efectivo'),
	(5, date('now','+2 day'), '09:00', '10:00', 0, 12.0, 'Pendiente', 'Efectivo'), -- socio 0
	(4, date('now','+2 day'), '11:00', '12:00', 0,  3.0, 'Pendiente', 'Tarjeta');  -- socio 0

-- ── HOY +3 ────────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(1, date('now','+3 day'), '10:00', '11:00', 1, 10.0, 'Pendiente', 'Tarjeta'),
	(1, date('now','+3 day'), '14:00', '15:00', 0, 10.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(3, date('now','+3 day'), '15:00', '16:00', 0,  5.0, 'Pendiente', 'Tarjeta');  -- socio 0

-- ── HOY +5 ────────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(4, date('now','+5 day'), '09:00', '10:00', 3,  3.0, 'Pendiente', 'Efectivo'),
	(4, date('now','+5 day'), '11:00', '12:00', 1,  3.0, 'Pendiente', 'Tarjeta'),
	(4, date('now','+5 day'), '10:00', '11:00', 0,  3.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(1, date('now','+5 day'), '16:00', '17:00', 0, 10.0, 'Pagado',    'Tarjeta'),  -- socio 0
	(5, date('now','+5 day'), '10:00', '11:00', 0, 12.0, 'Pendiente', 'Efectivo'); -- socio 0

-- ── HOY +7 ────────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(1, date('now','+7 day'), '09:00', '10:00', 2, 10.0, 'Pendiente', 'Tarjeta'),
	(1, date('now','+7 day'), '12:00', '13:00', 0, 10.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(3, date('now','+7 day'), '10:00', '11:00', 0,  5.0, 'Pagado',    'Tarjeta'),  -- socio 0
	(4, date('now','+7 day'), '18:00', '19:00', 0,  3.0, 'Pendiente', 'Efectivo'); -- socio 0

-- ── HOY +10 ───────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(5, date('now','+10 day'), '11:00', '12:00', 1, 12.0, 'Pendiente', 'Tarjeta'),
	(5, date('now','+10 day'), '14:00', '15:00', 0, 12.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(1, date('now','+10 day'), '09:00', '10:00', 0, 10.0, 'Pendiente', 'Efectivo'), -- socio 0
	(3, date('now','+10 day'), '16:00', '17:00', 0,  5.0, 'Pendiente', 'Tarjeta');  -- socio 0

-- ── HOY +14 ───────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(4, date('now','+14 day'), '09:00', '10:00', 3,  3.0, 'Pendiente', 'Efectivo'); -- socio 3

-- ── HOY +18 ───────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(1, date('now','+18 day'), '10:00', '11:00', 0, 10.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(3, date('now','+18 day'), '12:00', '13:00', 0,  5.0, 'Pendiente', 'Efectivo'), -- socio 0
	(5, date('now','+18 day'), '17:00', '18:00', 4, 12.0, 'Pendiente', 'Tarjeta');

-- ── HOY +21 ───────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(5, date('now','+21 day'), '09:00', '10:00', 0, 12.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(4, date('now','+21 day'), '11:00', '12:00', 0,  3.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(1, date('now','+21 day'), '14:00', '15:00', 2, 10.0, 'Pendiente', 'Efectivo');

-- ── HOY +25 ───────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(1, date('now','+25 day'), '11:00', '12:00', 0, 10.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(3, date('now','+25 day'), '10:00', '11:00', 0,  5.0, 'Pendiente', 'Efectivo'), -- socio 0
	(5, date('now','+25 day'), '15:00', '16:00', 0, 12.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(4, date('now','+25 day'), '08:00', '09:00', 1,  3.0, 'Pagado',    'Tarjeta');

-- ── HOY +29 ───────────────────────────────────────────────────────────────────
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	(1, date('now','+29 day'), '09:00', '10:00', 0, 10.0, 'Pendiente', 'Tarjeta'),  -- socio 0
	(4, date('now','+29 day'), '16:00', '17:00', 0,  3.0, 'Pendiente', 'Efectivo'), -- socio 0
	(5, date('now','+29 day'), '13:00', '14:00', 3, 12.0, 'Pendiente', 'Efectivo');

-- ─── RESERVAS DE ACTIVIDADES REGULARES ───────────────────────────────────────

-- Torneo de Badminton (id=1) en Polideportivo (id=6): 10:00-12:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now'),         '10:00', '12:00', 1, 0.0, 'Cuota'),
	(6, date('now','+1 day'),'10:00', '12:00', 1, 0.0, 'Cuota');

-- Natacion Intensiva (id=2) en Piscina (id=3): 13:00-15:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(3, date('now'),         '13:00', '15:00', 2, 0.0, 'Cuota'),
	(3, date('now','+1 day'),'13:00', '15:00', 2, 0.0, 'Cuota');

-- Spinning Avanzado (id=3) en Gimnasio (id=4): 17:00-19:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(4, date('now'),         '17:00', '19:00', 3, 0.0, 'Cuota'),
	(4, date('now','+2 day'),'17:00', '19:00', 3, 0.0, 'Cuota');

-- Padel Dobles (id=4) en Pista de Padel (id=5): 08:00-10:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(5, date('now'),         '08:00', '10:00', 4, 0.0, 'Cuota'),
	(5, date('now','+1 day'),'08:00', '10:00', 4, 0.0, 'Cuota');

-- Yoga Matinal (id=5) en Gimnasio (id=4): 09:00-10:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(4, date('now'),         '09:00', '10:00', 5, 0.0, 'Cuota'),
	(4, date('now','+1 day'),'09:00', '10:00', 5, 0.0, 'Cuota'),
	(4, date('now','+2 day'),'09:00', '10:00', 5, 0.0, 'Cuota');

-- Yoga Vespertino (id=6) en Gimnasio (id=4): 19:00-20:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(4, date('now'),         '19:00', '20:00', 6, 0.0, 'Cuota'),
	(4, date('now','+1 day'),'19:00', '20:00', 6, 0.0, 'Cuota'),
	(4, date('now','+2 day'),'19:00', '20:00', 6, 0.0, 'Cuota');

-- Aquagym Manana (id=7) en Piscina (id=3): 08:00-09:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(3, date('now'),         '08:00', '09:00', 7, 0.0, 'Cuota'),
	(3, date('now','+1 day'),'08:00', '09:00', 7, 0.0, 'Cuota');

-- Aquagym Tarde (id=8) en Piscina (id=3): 18:00-19:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(3, date('now'),         '18:00', '19:00', 8, 0.0, 'Cuota'),
	(3, date('now','+1 day'),'18:00', '19:00', 8, 0.0, 'Cuota');

-- ─── RESERVAS DE EVENTOS SOCIALES ────────────────────────────────────────────

-- Torneo de Voley Playa (id=9) en Polideportivo (id=6): 4h 09:00-13:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now'),          '09:00', '13:00', 9, 0.0, 'Cuota'),
	(6, date('now','+2 day'), '09:00', '13:00', 9, 0.0, 'Cuota'),
	(6, date('now','+7 day'), '09:00', '13:00', 9, 0.0, 'Cuota');

-- Gala de Natacion (id=10) en Piscina (id=3): 3h 16:00-19:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(3, date('now','+1 day'), '16:00', '19:00', 10, 0.0, 'Cuota'),
	(3, date('now','+3 day'), '16:00', '19:00', 10, 0.0, 'Cuota'),
	(3, date('now','+10 day'),'16:00', '19:00', 10, 0.0, 'Cuota');

-- Fiesta de Fin de Temporada (id=11) en Polideportivo (id=6): 4h 17:00-21:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now','+4 day'), '17:00', '21:00', 11, 0.0, 'Cuota'),
	(6, date('now','+14 day'),'17:00', '21:00', 11, 0.0, 'Cuota');

-- Gran Prix de Natacion (id=12) en Piscina (id=3): 3h 10:00-13:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(3, date('now','+5 day'), '10:00', '13:00', 12, 0.0, 'Cuota'),
	(3, date('now','+20 day'),'10:00', '13:00', 12, 0.0, 'Cuota');

-- Torneo Mixto de Padel (id=13) en Pista de Padel (id=5): 3h 10:00-13:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(5, date('now','+3 day'), '10:00', '13:00', 13, 0.0, 'Cuota'),
	(5, date('now','+15 day'),'10:00', '13:00', 13, 0.0, 'Cuota');

-- Olimpiada del Club (id=14) en Polideportivo (id=6): 5h 09:00-14:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now','+6 day'), '09:00', '14:00', 14, 0.0, 'Cuota'),
	(6, date('now','+25 day'),'09:00', '14:00', 14, 0.0, 'Cuota');
-- ═══════════════════════════════════════════════════════════════════════════════
-- BLOQUE +14 DÍAS: 20 NUEVAS ACTIVIDADES + 15 RESERVAS DE SOCIOS
-- ═══════════════════════════════════════════════════════════════════════════════

-- ─── 20 NUEVAS ACTIVIDADES (ids 15-34) ───────────────────────────────────────
-- Regulares (es_evento_social=0): ids 15-26
-- Eventos sociales (es_evento_social=1): ids 27-34
insert into Actividades(nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo, es_evento_social) values
	('Tenis Iniciacion',          'Clase grupal de tenis para principiantes (1h)',     1,  8, '2026-02-01', '2026-06-30',  6.0, 12.0, 1, 0),
	('Tenis Avanzado',            'Entrenamiento de tenis alto nivel (2h)',             2,  6, '2026-02-01', '2026-06-30', 10.0, 18.0, 1, 0),
	('Pilates Matinal',           'Sesion de pilates matutina (1h)',                   4, 12, '2026-02-01', '2026-06-30',  5.0, 10.0, 1, 0),
	('Pilates Vespertino',        'Sesion de pilates vespertina (1h)',                 4, 12, '2026-02-01', '2026-06-30',  5.0, 10.0, 1, 0),
	('Zumba',                     'Clase de baile cardio Zumba (1h)',                  4, 20, '2026-02-01', '2026-06-30',  4.0,  8.0, 1, 0),
	('Natacion Infantil',         'Natacion para ninos de 6 a 12 anos (1h)',           3, 15, '2026-02-01', '2026-06-30',  7.0, 13.0, 1, 0),
	('Natacion Adultos',          'Natacion perfeccionamiento adultos (1h)',            3, 10, '2026-02-01', '2026-06-30',  7.0, 13.0, 1, 0),
	('Crossfit',                  'Entrenamiento funcional alta intensidad (1h)',       4, 15, '2026-02-01', '2026-06-30',  8.0, 15.0, 1, 0),
	('Padel Iniciacion',          'Clase de padel para principiantes (1h)',             5,  4, '2026-02-01', '2026-06-30',  8.0, 15.0, 1, 0),
	('Estiramientos',             'Sesion de estiramientos y movilidad (1h)',           4, 20, '2026-02-01', '2026-06-30',  3.0,  6.0, 1, 0),
	('Boxeo Fitness',             'Clase de boxeo orientada al fitness (1h)',           6, 16, '2026-02-01', '2026-06-30',  9.0, 16.0, 1, 0),
	('Baloncesto 3x3',            'Partido de baloncesto media cancha (2h)',             6, 12, '2026-02-01', '2026-06-30', 11.0, 19.0, 1, 0),
	('Torneo Tenis Dobles',       'Torneo social de tenis por parejas (3h)',            2, 16, '2026-02-01', '2026-06-30', 15.0, 28.0, 1, 1),
	('Noche de Natacion',         'Gala nocturna de natacion sincronizada (3h)',        3, 50, '2026-02-01', '2026-06-30', 20.0, 35.0, 1, 1),
	('Copa Club de Padel',        'Campeonato interno de padel (4h)',                   5, 32, '2026-02-01', '2026-06-30', 18.0, 32.0, 1, 1),
	('Festival Multideporte',     'Festival de actividades fisicas variadas (5h)',      6, 80, '2026-02-01', '2026-06-30',  6.0, 14.0, 1, 1),
	('Maraton de Spinning',       'Sesion maratonica de spinning solidario (3h)',       4, 20, '2026-02-01', '2026-06-30', 10.0, 20.0, 1, 1),
	('Torneo Familiar Tenis',     'Torneo para familias con ninos (3h)',                1, 24, '2026-02-01', '2026-06-30',  8.0, 16.0, 1, 1),
	('Exhibicion de Boxeo',       'Combates amistosos y exhibicion (3h)',               6, 60, '2026-02-01', '2026-06-30', 12.0, 22.0, 1, 1),
	('Gran Gala Deportiva',       'Evento de clausura de temporada (4h)',               6, 120,'2026-02-01', '2026-06-30',  5.0, 12.0, 1, 1);

-- ─── RESERVAS DE SOCIOS EN +14 DÍAS ──────────────────────────────────────────
-- Socio 0: 6 reservas (ya tiene 3 de bloques anteriores: gym 10h, tenis 15h, padel 11h)
-- Socios 1-6: el resto repartido
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago, metodo_pago) values
	-- Socio 0 (6 reservas)
	(1, date('now','+14 day'), '08:00', '09:00', 0, 10.0, 'Pendiente', 'Tarjeta'),
	(2, date('now','+14 day'), '09:00', '10:00', 0, 10.0, 'Pendiente', 'Efectivo'),
	(3, date('now','+14 day'), '08:00', '09:00', 0,  5.0, 'Pagado',    'Tarjeta'),
	(4, date('now','+14 day'), '20:00', '21:00', 0,  3.0, 'Pendiente', 'Tarjeta'),
	(5, date('now','+14 day'), '19:00', '20:00', 0, 12.0, 'Pendiente', 'Efectivo'),
	(6, date('now','+14 day'), '08:00', '09:00', 0,  8.0, 'Pagado',    'Tarjeta'),
	-- Socio 1 — Carlos
	(1, date('now','+14 day'), '10:00', '11:00', 1, 10.0, 'Pagado',    'Tarjeta'),
	(4, date('now','+14 day'), '09:00', '10:00', 1,  3.0, 'Pagado',    'Tarjeta'),
	(5, date('now','+14 day'), '16:00', '17:00', 1, 12.0, 'Pendiente', 'Tarjeta'),
	-- Socio 2 — Maria
	(2, date('now','+14 day'), '10:00', '11:00', 2, 10.0, 'Pagado',    'Tarjeta'),
	(3, date('now','+14 day'), '14:00', '15:00', 2,  5.0, 'Pendiente', 'Efectivo'),
	(6, date('now','+14 day'), '10:00', '11:00', 2,  8.0, 'Pagado',    'Tarjeta'),
	-- Socio 3 — Pedro
	(1, date('now','+14 day'), '12:00', '13:00', 3, 10.0, 'Pendiente', 'Efectivo'),
	(5, date('now','+14 day'), '09:00', '10:00', 3, 12.0, 'Pendiente', 'Efectivo'),
	-- Socio 4 — Ana
	(2, date('now','+14 day'), '14:00', '15:00', 4, 10.0, 'Pagado',    'Tarjeta'),
	(3, date('now','+14 day'), '11:00', '12:00', 4,  5.0, 'Pagado',    'Tarjeta'),
	(4, date('now','+14 day'), '13:00', '14:00', 4,  3.0, 'Pendiente', 'Tarjeta'),
	-- Socio 5 — Luis
	(1, date('now','+14 day'), '16:00', '17:00', 5, 10.0, 'Pendiente', 'Efectivo'),
	(6, date('now','+14 day'), '15:00', '16:00', 5,  8.0, 'Pagado',    'Tarjeta'),
	-- Socio 6 — Sofia
	(2, date('now','+14 day'), '16:00', '17:00', 6, 10.0, 'Pendiente', 'Tarjeta'),
	(5, date('now','+14 day'), '20:00', '21:00', 6, 12.0, 'Pendiente', 'Efectivo'),
	(3, date('now','+14 day'), '20:00', '21:00', 6,  5.0, 'Pendiente', 'Tarjeta');

-- ─── RESERVAS DE LAS 20 NUEVAS ACTIVIDADES EN +14 DÍAS ───────────────────────

-- Tenis Iniciacion (id=15) Tenis 1: 13:00-14:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(1, date('now','+14 day'), '13:00', '14:00', 15, 0.0, 'Cuota'),
	(1, date('now','+16 day'), '13:00', '14:00', 15, 0.0, 'Cuota');

-- Tenis Avanzado (id=16) Tenis 2: 11:00-13:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(2, date('now','+14 day'), '11:00', '13:00', 16, 0.0, 'Cuota'),
	(2, date('now','+17 day'), '11:00', '13:00', 16, 0.0, 'Cuota');

-- Pilates Matinal (id=17) Gimnasio: 07:00-08:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(4, date('now','+14 day'), '07:00', '08:00', 17, 0.0, 'Cuota'),
	(4, date('now','+15 day'), '07:00', '08:00', 17, 0.0, 'Cuota'),
	(4, date('now','+16 day'), '07:00', '08:00', 17, 0.0, 'Cuota');

-- Pilates Vespertino (id=18) Gimnasio: 21:00-22:00 (fuera del horario standard, muestra "cerrado" -> OK para test)
-- Usamos 20:00-21:00 para que quede visible en horario
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(4, date('now','+14 day'), '20:30', '21:30', 18, 0.0, 'Cuota'),
	(4, date('now','+15 day'), '20:30', '21:30', 18, 0.0, 'Cuota');

-- Zumba (id=19) Gimnasio: 15:00-16:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(4, date('now','+14 day'), '15:00', '16:00', 19, 0.0, 'Cuota'),
	(4, date('now','+16 day'), '15:00', '16:00', 19, 0.0, 'Cuota'),
	(4, date('now','+18 day'), '15:00', '16:00', 19, 0.0, 'Cuota');

-- Natacion Infantil (id=20) Piscina: 09:00-10:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(3, date('now','+14 day'), '09:00', '10:00', 20, 0.0, 'Cuota'),
	(3, date('now','+15 day'), '09:00', '10:00', 20, 0.0, 'Cuota'),
	(3, date('now','+16 day'), '09:00', '10:00', 20, 0.0, 'Cuota');

-- Natacion Adultos (id=21) Piscina: 20:00-21:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(3, date('now','+14 day'), '20:00', '21:00', 21, 0.0, 'Cuota'),
	(3, date('now','+16 day'), '20:00', '21:00', 21, 0.0, 'Cuota');

-- Crossfit (id=22) Gimnasio: 18:00-19:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(4, date('now','+14 day'), '18:00', '19:00', 22, 0.0, 'Cuota'),
	(4, date('now','+15 day'), '18:00', '19:00', 22, 0.0, 'Cuota'),
	(4, date('now','+17 day'), '18:00', '19:00', 22, 0.0, 'Cuota');

-- Padel Iniciacion (id=23) Padel: 21:00 fuera horario, usamos dentro apertura
-- Padel abre L-V hasta 21:00, ponemos 13:00-14:00 el dia +15 que no choca
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(5, date('now','+15 day'), '13:00', '14:00', 23, 0.0, 'Cuota'),
	(5, date('now','+17 day'), '13:00', '14:00', 23, 0.0, 'Cuota');

-- Estiramientos (id=24) Gimnasio: 14:00-15:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(4, date('now','+14 day'), '14:00', '15:00', 24, 0.0, 'Cuota'),
	(4, date('now','+15 day'), '14:00', '15:00', 24, 0.0, 'Cuota'),
	(4, date('now','+16 day'), '14:00', '15:00', 24, 0.0, 'Cuota');

-- Boxeo Fitness (id=25) Polideportivo: 19:00-20:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now','+14 day'), '19:00', '20:00', 25, 0.0, 'Cuota'),
	(6, date('now','+17 day'), '19:00', '20:00', 25, 0.0, 'Cuota');

-- Baloncesto 3x3 (id=26) Polideportivo: 16:00-18:00
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now','+14 day'), '16:00', '18:00', 26, 0.0, 'Cuota'),
	(6, date('now','+16 day'), '16:00', '18:00', 26, 0.0, 'Cuota');

-- Torneo Tenis Dobles (id=27) Tenis 2: 3h 08:00-11:00 — EVENTO SOCIAL
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(2, date('now','+14 day'), '08:00', '11:00', 27, 0.0, 'Cuota'),
	(2, date('now','+21 day'), '08:00', '11:00', 27, 0.0, 'Cuota');

-- Noche de Natacion (id=28) Piscina: 3h 17:00-20:00 — EVENTO SOCIAL
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(3, date('now','+14 day'), '17:00', '20:00', 28, 0.0, 'Cuota'),
	(3, date('now','+22 day'), '17:00', '20:00', 28, 0.0, 'Cuota');

-- Copa Club de Padel (id=29) Padel: 4h 08:00-12:00 — EVENTO SOCIAL
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(5, date('now','+15 day'), '08:00', '12:00', 29, 0.0, 'Cuota'),
	(5, date('now','+23 day'), '08:00', '12:00', 29, 0.0, 'Cuota');

-- Festival Multideporte (id=30) Polideportivo: 5h 09:00-14:00 — EVENTO SOCIAL
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now','+14 day'), '09:00', '14:00', 30, 0.0, 'Cuota'),
	(6, date('now','+24 day'), '09:00', '14:00', 30, 0.0, 'Cuota');

-- Maraton de Spinning (id=31) Gimnasio: 3h 10:00-13:00 — EVENTO SOCIAL
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(4, date('now','+14 day'), '10:00', '13:00', 31, 0.0, 'Cuota'),
	(4, date('now','+20 day'), '10:00', '13:00', 31, 0.0, 'Cuota');

-- Torneo Familiar Tenis (id=32) Tenis 1: 3h 14:00-17:00 — EVENTO SOCIAL
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(1, date('now','+14 day'), '14:00', '17:00', 32, 0.0, 'Cuota'),
	(1, date('now','+22 day'), '14:00', '17:00', 32, 0.0, 'Cuota');

-- Exhibicion de Boxeo (id=33) Polideportivo: 3h 17:00-20:00 — EVENTO SOCIAL
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now','+15 day'), '17:00', '20:00', 33, 0.0, 'Cuota'),
	(6, date('now','+23 day'), '17:00', '20:00', 33, 0.0, 'Cuota');

-- Gran Gala Deportiva (id=34) Polideportivo: 4h 16:00-20:00 — EVENTO SOCIAL
insert into Reservas(id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago) values
	(6, date('now','+16 day'), '16:00', '20:00', 34, 0.0, 'Cuota'),
	(6, date('now','+28 day'), '16:00', '20:00', 34, 0.0, 'Cuota');