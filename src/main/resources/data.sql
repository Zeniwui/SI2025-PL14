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

-- Actividad 1: Yoga los Lunes de 09:00 a 10:00 en Sala Polivalente (Duración: 1 hora)
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) VALUES 
(1, 'Yoga Mañanas', 'Clase de Yoga para principiantes', 4, 20, '2026-01-01', '2026-12-31', 20.00, 30.00, 1);
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) VALUES 
(1, 'Lunes', '09:00', '10:00');

-- Actividad 2: Torneo Pádel los Sábados de 10:00 a 12:00 en Pista de Pádel 1 (Duración: 2 horas)
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) VALUES 
(2, 'Torneo Pádel Fin de Semana', 'Liguilla interna de pádel', 2, 16, '2026-01-01', '2026-12-31', 15.00, 25.00, 1);
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) VALUES 
(2, 'Sábado', '10:00', '12:00');

-- Actividad 3: Zumba Masterclass los Jueves de 18:00 a 20:00 en Sala Polivalente (Duración: 2 horas)
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) VALUES 
(3, 'Zumba Masterclass', 'Clase intensiva de Zumba', 4, 30, '2026-01-01', '2026-12-31', 25.00, 35.00, 1);
INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) VALUES 
(3, 'Jueves', '18:00', '20:00');


-- 5. Insertar reservas (Para probar disponibilidad y la UI)

-- --- RESERVAS SEMANA DEL 16 AL 22 DE FEBRERO 2026 (Semana anterior para probar retroceso) ---
-- Socio 2 (María López) reservó Tenis el 20/02/2026 (Viernes) de 10:00 a 11:00 (1 hora)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES 
(1, '2026-02-20', '10:00', '11:00', 2, NULL, 10.00, 'Pagado', 'Tarjeta');

-- Socio 2 (María López) reservó Pádel 1 el 20/02/2026 (Viernes) de 18:00 a 20:00 (2 horas)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES 
(2, '2026-02-20', '18:00', '20:00', 2, NULL, 24.00, 'Pendiente', 'Cuota_Mensual');


-- --- RESERVAS SEMANA DEL 23 DE FEBRERO AL 1 DE MARZO 2026 (Semana actual principal) ---

-- Reservas Automáticas de Actividades (El sistema las genera basándose en la tabla Horarios)
-- Yoga en Sala Polivalente (Lunes 23/02/2026 de 09:00 a 10:00 - 1 hora)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, estado_pago, metodo_pago) VALUES 
(4, '2026-02-23', '09:00', '10:00', NULL, 1, NULL, NULL);

-- Zumba en Sala Polivalente (Jueves 26/02/2026 de 18:00 a 20:00 - 2 horas)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, estado_pago, metodo_pago) VALUES 
(4, '2026-02-26', '18:00', '20:00', NULL, 3, NULL, NULL);

-- Torneo Pádel en Pista Pádel 1 (Sábado 28/02/2026 de 10:00 a 12:00 - 2 horas)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, estado_pago, metodo_pago) VALUES 
(2, '2026-02-28', '10:00', '12:00', NULL, 2, NULL, NULL);

-- Reservas de Socios en esa misma semana
-- Socio 1 (Juan Pérez) reserva Tenis el Martes 24/02/2026 de 17:00 a 19:00 (2 horas)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES 
(1, '2026-02-24', '17:00', '19:00', 1, NULL, 20.00, 'Pagado', 'Efectivo');

-- Socio 2 (María López) reserva Pádel 1 el Miércoles 25/02/2026 de 11:00 a 12:00 (1 hora)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES 
(2, '2026-02-25', '11:00', '12:00', 2, NULL, 12.00, 'Pendiente', 'Cuota_Mensual');

-- Socio 1 (Juan Pérez) reserva Sala Polivalente el Viernes 27/02/2026 de 14:00 a 15:00 (1 hora)
-- (Justo a mediodía cuando no hay actividades)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES 
(4, '2026-02-27', '14:00', '15:00', 1, NULL, 15.00, 'Pagado', 'Tarjeta');

-- Socio 1 (Juan Pérez) reserva Pádel 2 el Domingo 01/03/2026 de 20:00 a 22:00 (2 horas final de día)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES 
(3, '2026-03-01', '20:00', '22:00', 1, NULL, 20.00, 'Pagado', 'Tarjeta');