-- Aquí añadiremos datos iniciales a la base de datos

-- 1. Insertar Usuarios
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES 
('12345678A', 'Juan', 'Pérez García', 600111222, 'juan.perez@email.com'),
('87654321B', 'María', 'López Martínez', 600333444, 'maria.lopez@email.com'),
('11223344C', 'Carlos', 'Ruiz Sánchez', 600555666, 'carlos.ruiz@email.com');

-- 2. Insertar Socios
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES 
(1, '12345678A', 'pass123', 'Al Corriente'),
(2, '87654321B', 'securePass', 'Pendiente');

-- 3. Insertar Instalaciones
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) VALUES 
(1, 'Pista de Tenis 1', 'Exterior', 10.00),
(2, 'Pista de Pádel 1', 'Cristal', 12.00),
(3, 'Pista de Pádel 2', 'Muro', 10.00),
(4, 'Sala Polivalente', 'Interior', 15.00);

-- 4. Periodos y Actividades
INSERT INTO PeriodosInscripcion (id_periodo, nombre, inicio_socios, fin_socios, fin_no_socios) VALUES 
(1, 'Temporada 2026', '2026-01-01', '2026-12-31', '2026-12-31');

-- Actividad: Yoga los Lunes de 09:00 a 10:00 en Sala Polivalente
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) VALUES 
(1, 'Yoga Mañanas', 'Clase de Yoga', 4, 20, '2026-01-01', '2026-12-31', 20.00, 30.00, 1);

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) VALUES 
(1, 'Lunes', '09:00', '10:00');

-- 5. Insertar reservas (Para probar disponibilidad)
-- María ha reservado la Pista de Tenis 1 de 10:00 a 11:00
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES 
(1, '2026-02-20', '10:00', '11:00', 2, NULL, 10.00, 'Pagado', 'Tarjeta');

-- María ha reservado Pista de Pádel 1 de 18:00 a 19:30
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES 
(2, '2026-02-20', '18:00', '19:30', 2, NULL, 18.00, 'Pendiente', 'Cuota_Mensual');

-- Reserva automática por la clase de Yoga el Lunes 23/02/2026 de 09:00 a 10:00
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, estado_pago, metodo_pago) VALUES 
(4, '2026-02-23', '09:00', '10:00', NULL, 1, NULL, NULL);

-- Mas reservas
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES 
(1, '2026-02-26', '10:00', '12:00', 1, NULL, 20.00, 'Pagado', 'Tarjeta'),
(2, '2026-02-27', '17:00', '18:00', 1, NULL, 12.00, 'Pendiente', 'Cuota_Mensual'),
(3, '2026-02-28', '11:00', '13:00', 2, NULL, 20.00, 'Pagado', 'Efectivo'),
(4, '2026-03-02', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
(1, '2026-03-05', '18:00', '19:00', 1, NULL, 10.00, 'Pagado', 'Tarjeta'),
(2, '2026-03-08', '19:00', '21:00', 2, NULL, 24.00, 'Pendiente', 'Cuota_Mensual'),
(4, '2026-03-09', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
(3, '2026-03-15', '10:00', '11:00', 1, NULL, 10.00, 'Pagado', 'Tarjeta'),
(1, '2026-03-18', '16:00', '18:00', 1, NULL, 20.00, 'Pagado', 'Tarjeta'),
(2, '2026-03-22', '12:00', '13:00', 2, NULL, 12.00, 'Pendiente', 'Cuota_Mensual');