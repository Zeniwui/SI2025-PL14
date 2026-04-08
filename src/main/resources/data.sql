DELETE FROM PagosNoSocios;
DELETE FROM Pagos;
DELETE FROM Horarios;
DELETE FROM Reservas;
DELETE FROM Inscripciones;
DELETE FROM Actividades;
DELETE FROM Instalaciones;
DELETE FROM PeriodosInscripcion;
DELETE FROM NoSocios;
DELETE FROM Socios;
DELETE FROM Usuarios;

INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES 
('12345678A', 'Carlos', 'Gomez', 600111222, 'carlos@email.com'),
('87654321B', 'Maria', 'Lopez', 600333444, 'maria@email.com');

INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES 
(1, '12345678A', 'pass123', 'Al Corriente'),
(2, '87654321B', 'pass456', 'Al Corriente');

INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) VALUES 
(1, 'Pista de Tenis 1', 'Exterior', 10.00),
(2, 'Piscina Climatizada', 'Interior', 15.50),
(3, 'Sala de Musculación', 'Interior', 5.00),
(4, 'Cancha de Baloncesto', 'Exterior', 8.00);

INSERT INTO PeriodosInscripcion (id_periodo, nombre, descripcion, inicio_socios, fin_socios, fin_no_socios) VALUES 
(1, 'Primavera 2026', 'Periodo de primavera', '2026-02-01', '2026-02-15', '2026-04-30'),
(2, 'Temporada Verano', 'Periodo de verano', '2026-06-01', '2026-06-15', '2026-08-30');

INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) VALUES 
(1, 'Ciclo Indoor', 'Clases de spinning', 4, 20, '2026-03-01', '2026-03-31', 20.00, 30.00, 1),
(2, 'Aquagym', 'Gimnasia en el agua', 3, 15, '2026-03-01', '2026-03-31', 15.00, 25.00, 1),
(3, 'Clases de Tenis', 'Clases grupales de tenis', 1, 4, '2026-03-01', '2026-03-31', 20.00, 30.00, 1),
(4, 'Cursillo Natación Verano', 'Clases intensivas para todos', 2, 15, '2026-07-01', '2026-07-31', 30.00, 50.00, 2);

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) VALUES
(4, 'Lunes', '10:00', '11:30'),
(4, 'Miércoles', '10:00', '11:30'),
(1, 'Martes', '18:00', '19:30');

INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion, precio_inscripcion) VALUES
(1, 1, '2026-02-10', 20.00),
(1, 2, '2026-02-10', 15.00);

INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
(4, '2026-03-04', '10:00:00', '11:00:00', NULL, 1, 12.00, 'Pagado', 'Efectivo'),
(1, '2026-02-27', '09:00:00', '10:00:00', 1, NULL, 12.00, 'Pagado', 'Tarjeta');