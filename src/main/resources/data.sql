INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES 
('Pista de Tenis 1', 'Pista', 12.00),
('Pista de Tenis 2', 'Pista', 12.00),
('Piscina Climatizada', 'Piscina', 15.00),
('Sala de Ciclo', 'Sala', 10.00);

INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES 
('12345678A', 'Carlos', 'Gomez', 600111222, 'carlos@email.com'),
('87654321B', 'Maria', 'Lopez', 600333444, 'maria@email.com');

INSERT INTO Socios (dni, contrasena, estado_pagos) VALUES 
('12345678A', 'pass123', 'Al Corriente'),
('87654321B', 'pass456', 'Al Corriente');

INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) VALUES
('Primavera 2026', '2026-02-01', '2026-02-15', '2026-02-28');

INSERT INTO Actividades (nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) VALUES 
('Ciclo Indoor', 'Clases de spinning', 4, 20, '2026-03-01', '2026-03-31', 20.00, 30.00, 1),
('Aquagym', 'Gimnasia en el agua', 3, 15, '2026-03-01', '2026-03-31', 15.00, 25.00, 1),
('Clases de Tenis', 'Clases grupales de tenis', 1, 4, '2026-03-01', '2026-03-31', 20.00, 30.00, 1);;

INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
(1, '2026-02-27', '09:00:00', '10:00:00', 1, NULL, 12.00, 'Pagado', 'Tarjeta'),
(1, '2026-02-27', '17:00:00', '19:00:00', 2, NULL, 24.00, 'Pendiente', NULL),
(1, '2026-02-28', '10:00:00', '11:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-02-28', '18:00:00', '20:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-01', '09:00:00', '11:00:00', 1, NULL, 24.00, 'Pagado', 'Efectivo'),
(1, '2026-03-01', '16:00:00', '17:00:00', 2, NULL, 12.00, 'Pagado', 'Tarjeta'),
(1, '2026-03-02', '09:00:00', '10:00:00', 1, NULL, 12.00, 'Pagado', 'Tarjeta'),
(1, '2026-03-02', '17:00:00', '19:00:00', 2, NULL, 24.00, 'Pendiente', NULL),
(1, '2026-03-03', '10:00:00', '11:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-03', '18:00:00', '20:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-04', '09:00:00', '11:00:00', 1, NULL, 24.00, 'Pagado', 'Efectivo'),
(1, '2026-03-04', '16:00:00', '17:00:00', 2, NULL, 12.00, 'Pagado', 'Tarjeta'),
(1, '2026-03-05', '11:00:00', '12:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-05', '17:00:00', '19:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-06', '09:00:00', '10:00:00', 1, NULL, 12.00, 'Pendiente', NULL),
(1, '2026-03-06', '18:00:00', '20:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-07', '10:00:00', '12:00:00', 2, NULL, 24.00, 'Pagado', 'Efectivo'),
(1, '2026-03-07', '16:00:00', '17:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-09', '09:00:00', '11:00:00', 1, NULL, 24.00, 'Pagado', 'Tarjeta'),
(1, '2026-03-09', '17:00:00', '18:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-10', '10:00:00', '11:00:00', 2, NULL, 12.00, 'Pendiente', NULL),
(1, '2026-03-10', '18:00:00', '20:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-11', '09:00:00', '10:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-11', '16:00:00', '18:00:00', 1, NULL, 24.00, 'Pagado', 'Efectivo'),
(1, '2026-03-12', '10:00:00', '12:00:00', NULL, 3, 0.00, 'Cuota', 'Cuota_Mensual'),
(1, '2026-03-12', '18:00:00', '19:00:00', 2, NULL, 12.00, 'Pagado', 'Tarjeta'),
(2, '2026-03-02', '10:00:00', '12:00:00', 1, NULL, 24.00, 'Pagado', 'Tarjeta'),
(2, '2026-03-03', '17:00:00', '18:00:00', 2, NULL, 12.00, 'Pendiente', NULL),
(2, '2026-03-05', '18:00:00', '20:00:00', 1, NULL, 24.00, 'Pagado', 'Efectivo'),
(2, '2026-03-07', '09:00:00', '10:00:00', 2, NULL, 12.00, 'Pagado', 'Tarjeta'),
(2, '2026-03-10', '16:00:00', '18:00:00', 1, NULL, 24.00, 'Pendiente', NULL),
(2, '2026-03-13', '10:00:00', '11:00:00', 2, NULL, 12.00, 'Pagado', 'Efectivo'),
(2, '2026-03-16', '18:00:00', '20:00:00', 1, NULL, 24.00, 'Pagado', 'Tarjeta'),
(3, '2026-03-02', '09:00:00', '10:00:00', NULL, 2, 0.00, 'Cuota', 'Cuota_Mensual'),
(3, '2026-03-03', '11:00:00', '13:00:00', 1, NULL, 30.00, 'Pagado', 'Tarjeta'),
(3, '2026-03-05', '10:00:00', '11:00:00', NULL, 2, 0.00, 'Cuota', 'Cuota_Mensual'),
(3, '2026-03-08', '12:00:00', '14:00:00', 1, NULL, 30.00, 'Pendiente', NULL),
(3, '2026-03-11', '09:00:00', '10:00:00', NULL, 2, 0.00, 'Cuota', 'Cuota_Mensual'),
(3, '2026-03-14', '16:00:00', '18:00:00', 1, NULL, 30.00, 'Pagado', 'Efectivo'),
(3, '2026-03-17', '10:00:00', '11:00:00', NULL, 2, 0.00, 'Cuota', 'Cuota_Mensual'),
(4, '2026-03-02', '18:00:00', '19:30:00', NULL, 1, 0.00, 'Cuota', 'Cuota_Mensual'),
(4, '2026-03-04', '09:00:00', '10:00:00', 2, NULL, 10.00, 'Pagado', 'Efectivo'),
(4, '2026-03-06', '19:00:00', '20:00:00', NULL, 1, 0.00, 'Cuota', 'Cuota_Mensual'),
(4, '2026-03-09', '17:00:00', '18:00:00', 2, NULL, 10.00, 'Pendiente', NULL),
(4, '2026-03-12', '18:00:00', '19:00:00', NULL, 1, 0.00, 'Cuota', 'Cuota_Mensual'),
(4, '2026-03-15', '10:00:00', '11:30:00', 2, NULL, 15.00, 'Pagado', 'Tarjeta');