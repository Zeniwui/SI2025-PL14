
-- Limpieza de tablas en orden inverso a las FK
-- 1. LIMPIEZA DE TABLAS
DELETE FROM Horarios;
DELETE FROM Reservas;
DELETE FROM Actividades;
DELETE FROM Instalaciones;
DELETE FROM PeriodosInscripcion;
DELETE FROM Socios;
DELETE FROM Usuarios;

INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) VALUES (1, 'Pista de Tenis 1', 'Exterior', 10.0);
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) VALUES (2, 'Piscina Climatizada', 'Interior', 15.5);
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) VALUES (3, 'Sala de Musculación', 'Interior', 5.0);

INSERT INTO Actividades (id_actividad, nombre, id_instalacion, aforo, fecha_inicio, fecha_fin) 
VALUES (1, 'Cursillo Verano', 1, 20, '2026-06-01', '2026-08-31');

INSERT INTO Actividades (id_actividad, nombre, id_instalacion, aforo, fecha_inicio, fecha_fin) 
VALUES (2, 'Natación Avanzada', 2, 25, '2026-07-30', '2026-09-30');

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
VALUES (1, 'LUNES', '10:00', '11:30');
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
VALUES (1, 'MIERCOLES', '10:00', '11:30');

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
VALUES (2, 'MARTES', '19:00', '20:30');
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
VALUES (2, 'JUEVES', '19:00', '20:30');

INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('12345678A', 'Juan', 'Pérez', 600123456, 'juan@email.com');

INSERT INTO Socios (id_socio, dni, contrasena) VALUES (1, '12345678A', '123');

INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio)
VALUES (1, '2026-06-01', '10:30', '11:30', 1);

INSERT INTO PeriodosInscripcion (nombre, descripcion,inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Campaña Verano 2026','periodo de mayo en el año 2026' ,'2026-05-01', '2026-05-15', '2026-05-30');
INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Curso Escolar 2026-27', '2026-09-01', '2026-09-10', '2026-09-25');
INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Intensivo Pascua 2026', '2026-03-01', '2026-03-07', '2026-03-15');

-- 2. INSTALACIONES
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Pista de Tenis 1', 'Exterior', 10.0);
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Piscina Climatizada', 'Interior', 15.5);
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Sala de Musculación', 'Interior', 5.0);
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Cancha de Baloncesto', 'Exterior', 8.0);

-- 3. PERIODOS DE INSCRIPCIÓN
INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Temporada Verano', '2026-06-01', '2026-06-15', '2026-08-30');

INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Temporada Invierno', '2026-10-01', '2026-10-15', '2026-12-20');

INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Temporada Otoño', '2026-09-01', '2026-09-15', '2026-11-30');

INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Temporada Primavera', '2026-03-01', '2026-03-15', '2026-05-30');

-- 4. USUARIOS Y SOCIOS


-- 5. ACTIVIDADES (Incluyendo columna 'tipo')
INSERT INTO Actividades (nombre, tipo, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) 
VALUES ('Cursillo Natación Verano', 'Deporte', 'Clases intensivas para todos los niveles', 2, 15, '2026-07-01', '2026-07-31', 30.00, 50.00, 
(SELECT id_periodo FROM PeriodosInscripcion WHERE nombre = 'Temporada Verano'));

INSERT INTO Actividades (nombre, tipo, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) 
VALUES ('Torneo Tenis Juvenil', 'Torneo', 'Competición sub-18', 1, 32, '2026-08-05', '2026-08-07', 10.00, 20.00, 
(SELECT id_periodo FROM PeriodosInscripcion WHERE nombre = 'Temporada Verano'));

INSERT INTO Actividades (nombre, tipo, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) 
VALUES ('Yoga Relax', 'Deporte', 'Sesiones de meditación y estiramiento', 3, 20, '2026-11-01', '2026-12-15', 25.00, 40.00, 
(SELECT id_periodo FROM PeriodosInscripcion WHERE nombre = 'Temporada Invierno'));

INSERT INTO Actividades (nombre, tipo, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) 
VALUES ('Liga Baloncesto Senior', 'Torneo', 'Competición oficial de invierno', 4, 50, '2026-10-15', '2026-12-20', 15.00, 30.00, 
(SELECT id_periodo FROM PeriodosInscripcion WHERE nombre = 'Temporada Invierno'));

INSERT INTO Actividades (nombre, tipo, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) 
VALUES ('Charla Nutrición', 'Conferencia', 'Aprende a comer sano', 3, 50, '2026-10-05', '2026-10-05', 0.00, 10.00, 
(SELECT id_periodo FROM PeriodosInscripcion WHERE nombre = 'Temporada Invierno'));

INSERT INTO Actividades (nombre, tipo, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) 
VALUES ('Escuela de Pádel', 'Deporte', 'Nivel iniciación y medio', 1, 12, '2026-09-20', '2026-11-20', 35.00, 55.00, 
(SELECT id_periodo FROM PeriodosInscripcion WHERE nombre = 'Temporada Otoño'));

INSERT INTO Actividades (nombre, tipo, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) 
VALUES ('Pilates Avanzado', 'Deporte', 'Control corporal y respiración', 3, 15, '2026-10-01', '2026-12-15', 20.00, 35.00, 
(SELECT id_periodo FROM PeriodosInscripcion WHERE nombre = 'Temporada Otoño'));

INSERT INTO Actividades (nombre, tipo, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) 
VALUES ('Aquagym Senior', 'Deporte', 'Gimnasia en el agua para mayores', 2, 25, '2026-03-15', '2026-06-15', 15.00, 25.00, 
(SELECT id_periodo FROM PeriodosInscripcion WHERE nombre = 'Temporada Primavera'));

INSERT INTO Actividades (nombre, tipo, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) 
VALUES ('Entrenamiento Funcional', 'Deporte', 'Circuitos de alta intensidad', 4, 20, '2026-04-01', '2026-05-30', 22.50, 40.00, 
(SELECT id_periodo FROM PeriodosInscripcion WHERE nombre = 'Temporada Primavera'));

-- 6. HORARIOS
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Lunes', '10:00', '11:30' FROM Actividades WHERE nombre = 'Cursillo Natación Verano';
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Miércoles', '10:00', '11:30' FROM Actividades WHERE nombre = 'Cursillo Natación Verano';
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Viernes', '10:00', '11:30' FROM Actividades WHERE nombre = 'Cursillo Natación Verano';

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Sábado', '09:00', '20:00' FROM Actividades WHERE nombre = 'Torneo Tenis Juvenil';

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Martes', '18:00', '19:30' FROM Actividades WHERE nombre = 'Yoga Relax';
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Jueves', '18:00', '19:30' FROM Actividades WHERE nombre = 'Yoga Relax';

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Domingo', '10:00', '14:00' FROM Actividades WHERE nombre = 'Liga Baloncesto Senior';

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Lunes', '17:00', '18:30' FROM Actividades WHERE nombre = 'Escuela de Pádel';
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Miércoles', '17:00', '18:30' FROM Actividades WHERE nombre = 'Escuela de Pádel';

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Martes', '09:30', '10:30' FROM Actividades WHERE nombre = 'Pilates Avanzado';
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Jueves', '09:30', '10:30' FROM Actividades WHERE nombre = 'Pilates Avanzado';

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Viernes', '11:00', '11:45' FROM Actividades WHERE nombre = 'Aquagym Senior';

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Lunes', '19:00', '20:00' FROM Actividades WHERE nombre = 'Entrenamiento Funcional';
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Miércoles', '19:00', '20:00' FROM Actividades WHERE nombre = 'Entrenamiento Funcional';
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
SELECT id_actividad, 'Viernes', '19:00', '20:00' FROM Actividades WHERE nombre = 'Entrenamiento Funcional';
