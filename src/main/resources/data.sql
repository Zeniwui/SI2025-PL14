DELETE FROM Horarios;
DELETE FROM Reservas;
DELETE FROM Actividades;
DELETE FROM Instalaciones;
DELETE FROM PeriodosInscripcion;
DELETE FROM Socios;
DELETE FROM Usuarios;
-- Aquí añadiremos datos iniciales a la base de datos

-- Datos iniciales para Instalaciones
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Pista de Tenis 1', 'Exterior', 10.0);
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Piscina Climatizada', 'Interior', 15.5);
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Sala de Musculación', 'Interior', 5.0);
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Cancha de Baloncesto', 'Exterior', 8.0);

-- Datos iniciales para Periodos de Inscripción
-- Formato de fecha estándar: YYYY-MM-DD
INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Temporada Verano', '2026-06-01', '2026-06-15', '2026-08-30');

INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Temporada Invierno', '2026-10-01', '2026-10-15', '2026-12-20');

-- Datos para Usuarios y Socios
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('12345678A', 'Juan', 'Pérez García', 600123456, 'juan.perez@email.com');

INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) 
VALUES (1, '12345678A', 'password123', 'Al Corriente');

INSERT INTO Actividades (id_actividad, nombre, id_instalacion, aforo, fecha_inicio, fecha_fin) 
VALUES (1, 'Cursillo Verano', 1, 20, '2026-06-01', '2026-08-31');

INSERT INTO Actividades (id_actividad, nombre, id_instalacion, aforo, fecha_inicio, fecha_fin) 
VALUES (2, 'Natación Avanzada', 2, 25, '2026-07-30', '2026-09-30');

INSERT INTO Actividades (id_actividad, nombre, id_instalacion, aforo, fecha_inicio, fecha_fin) 
VALUES (3, 'Conlfictiva', 2, 30, '2026-07-20', '2026-09-20');

-- 2. HORARIOS PARA LA ACTIVIDAD 2 (Natación Avanzada - Piscina Climatizada)
-- Lunes de 10:00 a 12:00
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
VALUES (2, 'LUNES', '10:00', '12:00');

-- 3. HORARIOS PARA LA ACTIVIDAD 3 (Conflictiva - Piscina Climatizada)
-- Choca directamente con la Natación Avanzada el mismo Lunes a la misma hora
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) 
VALUES (3, 'LUNES', '10:00', '12:00'); 

-- 4. RESERVAS MANUALES (Simulamos que un Socio ya reservó la pista de Tenis)
-- Esto generará un conflicto si intentas programar el 'Cursillo Verano' el 1 de Junio
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio)
VALUES (1, '2026-06-01', '09:00', '11:00', 1);

-- 5. MATERIALIZAMOS ALGUNAS RESERVAS DE LA ACTIVIDAD 2 
-- (Para que cuando el administrador mire la Actividad 3, vea que la 2 le estorba)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_actividad)
VALUES (2, '2026-08-03', '10:00', '12:00', 2); -- Es un Lunes
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_actividad)
VALUES (2, '2026-08-10', '10:00', '12:00', 2); -- Es otro Lunes