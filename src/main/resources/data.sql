-- Limpieza de tablas en orden inverso a las FK
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