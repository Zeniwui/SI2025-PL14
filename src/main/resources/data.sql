-- ── 1. Usuarios ───────────────────────────────────────────────────────────
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES
    ('12345678A', 'Juan',    'Pérez García',       600111222, 'juan.perez@email.com'),
    ('87654321B', 'María',   'López Martínez',     600333444, 'maria.lopez@email.com'),
    ('11223344C', 'Carlos',  'Ruiz Sánchez',       600555666, 'carlos.ruiz@email.com'),
    ('44556677D', 'Ana',     'Ruiz Torres',        600777888, 'ana.ruiz@email.com'),
    ('55667788E', 'Pedro',   'Gómez Vidal',        600999000, 'pedro.gomez@email.com'),
    ('66778899F', 'Laura',   'Fernández Díaz',     601111222, 'laura.fernandez@email.com'),
    ('77889900G', 'Sofía',   'Martínez Iglesias',  601222333, 'sofia.martinez@email.com'),
    ('88990011H', 'Álvaro',  'Castillo Romero',    601333444, 'alvaro.castillo@email.com'),
    ('99001122I', 'Elena',   'Vega Morales',       601444555, 'elena.vega@email.com'),
    ('10111213J', 'Roberto', 'Herrera Blanco',     601555666, 'roberto.herrera@email.com'),
    ('20212223K', 'Lucía',   'Navarro Prieto',     601666777, 'lucia.navarro@email.com');

-- ── 2. Socios ─────────────────────────────────────────────────────────────
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES
    (1,  '12345678A', 'pass123',    'Al Corriente'),
    (2,  '87654321B', 'securePass', 'Pendiente'),
    (3,  '11223344C', 'abc456',     'Al Corriente'),
    (4,  '44556677D', 'xyz789',     'Pendiente'),
    (5,  '55667788E', 'pass555',    'Al Corriente'),
    (6,  '66778899F', 'pass666',    'Pendiente'),
    (7,  '77889900G', 'pass777',    'Al Corriente'),
    (8,  '88990011H', 'pass888',    'Pendiente'),
    (9,  '99001122I', 'pass999',    'Al Corriente'),
    (10, '10111213J', 'pass010',    'Pendiente'),
    (11, '20212223K', 'pass011',    'Al Corriente');

-- ── 3. Instalaciones ──────────────────────────────────────────────────────
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) VALUES
    (1, 'Pista de Tenis 1', 'Exterior',  10.00),
    (2, 'Pista de Pádel 1', 'Cristal',   12.00),
    (3, 'Pista de Pádel 2', 'Muro',      10.00),
    (4, 'Sala Polivalente',  'Interior', 15.00);

-- ── 4. Periodo de inscripción ─────────────────────────────────────────────
INSERT INTO PeriodosInscripcion (id_periodo, nombre, descripcion,
                                  inicio_socios, fin_socios, fin_no_socios) VALUES
    (1, 'Temporada 2026', 'Temporada anual 2026',
        '2026-01-01', '2026-12-31', '2026-12-31');

-- ── 5. Actividades ────────────────────────────────────────────────────────
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo,
                          fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) VALUES
    (1, 'Yoga Mañanas',     'Clase de Yoga suave',         4, 20, '2026-01-05', '2026-12-31', 20.00, 30.00, 1),
    (2, 'Pilates Tarde',    'Pilates de nivel medio',      4, 15, '2026-02-02', '2026-06-30', 25.00, 38.00, 1),
    (3, 'Zumba Fitness',    'Baile aeróbico con ritmo',    4, 25, '2026-02-09', '2026-12-31', 18.00, 28.00, 1),
    (4, 'Tenis Iniciación', 'Tenis para principiantes',    1, 10, '2026-01-12', '2026-12-31', 22.00, 35.00, 1),
    (5, 'Pádel Avanzado',   'Pádel para nivel medio-alto', 2,  8, '2026-03-01', '2026-12-31', 28.00, 42.00, 1);

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) VALUES
    (1, 'Lunes',     '09:00', '10:00'),
    (2, 'Miércoles', '17:00', '18:00'),
    (3, 'Viernes',   '18:00', '19:00'),
    (4, 'Martes',    '10:00', '11:00'),
    (5, 'Jueves',    '19:00', '20:00');

-- ── 6. Reservas ───────────────────────────────────────────────────────────

-- Reservas automáticas de actividades (sin socio)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (4, '2026-02-02', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-02-09', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-02-23', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-03-02', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-03-09', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-03-23', '17:00', '18:00', NULL, 2, NULL, NULL, NULL);

-- JUAN PÉREZ (socio 1) — fav: Tenis, deuda 24 € → AMARILLO
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-01-10', '10:00', '11:00', 1, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (1, '2026-01-17', '10:00', '12:00', 1, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (1, '2026-02-12', '10:00', '11:00', 1, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (2, '2026-02-19', '16:00', '17:00', 1, NULL, 12.00, 'Pendiente', 'Cuota_Mensual'),
    (1, '2026-03-05', '18:00', '19:00', 1, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (1, '2026-03-12', '10:00', '11:00', 1, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (1, '2026-03-18', '16:00', '18:00', 1, NULL, 20.00, 'Pagado',    'Tarjeta'),
    (2, '2026-03-26', '11:00', '12:00', 1, NULL, 12.00, 'Pendiente', 'Cuota_Mensual');

-- MARÍA LÓPEZ (socio 2) — fav: Pádel 1, deuda 104 € → ROJO
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-20', '10:00', '11:00', 2, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (2, '2026-02-20', '18:00', '19:30', 2, NULL, 18.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-02-27', '17:00', '18:40', 2, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-08', '19:00', '21:00', 2, NULL, 24.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-22', '12:00', '13:00', 2, NULL, 12.00, 'Pendiente', 'Cuota_Mensual'),
    (4, '2026-03-29', '10:00', '12:00', 2, NULL, 30.00, 'Pendiente', 'Cuota_Mensual');

-- CARLOS RUIZ (socio 3) — deuda 0 € → VERDE
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-14', '10:00', '11:00', 3, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (4, '2026-02-21', '11:00', '12:00', 3, NULL, 15.00, 'Pagado', 'Efectivo'),
    (1, '2026-03-07', '09:00', '10:00', 3, NULL, 10.00, 'Pagado', 'Tarjeta');

-- ANA RUIZ TORRES (socio 4) — deuda 33 € → AMARILLO
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-26', '10:00', '12:00', 4, NULL, 20.00, 'Pagado',    'Tarjeta'),
    (2, '2026-02-28', '17:00', '18:30', 4, NULL, 18.00, 'Pendiente', 'Cuota_Mensual'),
    (3, '2026-03-14', '11:00', '13:00', 4, NULL, 20.00, 'Pagado',    'Efectivo'),
    (4, '2026-03-21', '10:00', '11:00', 4, NULL, 15.00, 'Pendiente', 'Cuota_Mensual');

-- PEDRO GÓMEZ (socio 5) — deuda 0 € → VERDE
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-03-15', '10:00', '11:00', 5, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (3, '2026-03-28', '16:00', '17:00', 5, NULL, 10.00, 'Pagado', 'Efectivo');

-- LAURA FERNÁNDEZ (socio 6) — deuda 54 € → AMARILLO
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (2, '2026-02-24', '18:00', '20:00', 6, NULL, 24.00, 'Pendiente', 'Cuota_Mensual'),
    (4, '2026-03-03', '10:00', '12:00', 6, NULL, 30.00, 'Pendiente', 'Cuota_Mensual'),
    (1, '2026-03-17', '10:00', '11:00', 6, NULL, 10.00, 'Pagado',    'Tarjeta');

-- SOFÍA MARTÍNEZ (socio 7) — deuda 0 € → VERDE, fav: Sala Polivalente
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (4, '2026-02-05', '10:00', '11:00', 7, NULL, 15.00, 'Pagado', 'Tarjeta'),
    (4, '2026-02-19', '11:00', '12:00', 7, NULL, 15.00, 'Pagado', 'Tarjeta'),
    (4, '2026-03-11', '09:00', '10:00', 7, NULL, 15.00, 'Pagado', 'Efectivo'),
    (1, '2026-03-25', '10:00', '11:00', 7, NULL, 10.00, 'Pagado', 'Tarjeta');

-- ÁLVARO CASTILLO (socio 8) — deuda 50 € → AMARILLO, fav: Tenis
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-10', '10:00', '12:00', 8, NULL, 20.00, 'Pagado',    'Tarjeta'),
    (1, '2026-02-18', '15:00', '17:00', 8, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-04', '18:00', '20:00', 8, NULL, 24.00, 'Pagado',    'Efectivo'),
    (1, '2026-03-19', '10:00', '12:00', 8, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (3, '2026-03-26', '11:00', '12:00', 8, NULL, 10.00, 'Pendiente', 'Cuota_Mensual');

-- ELENA VEGA (socio 9) — deuda 0 € → VERDE, fav: Pádel 2
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (3, '2026-02-13', '16:00', '17:00', 9, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (3, '2026-03-20', '16:00', '17:00', 9, NULL, 10.00, 'Pagado', 'Tarjeta');

-- ROBERTO HERRERA (socio 10) — deuda 132 € → ROJO, fav: Pádel 1
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (2, '2026-02-06', '18:00', '19:30', 10, NULL, 18.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-02-13', '17:00', '18:40', 10, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-06', '19:00', '21:20', 10, NULL, 28.00, 'Pendiente', 'Cuota_Mensual'),
    (1, '2026-03-13', '10:00', '12:00', 10, NULL, 20.00, 'Pagado',    'Tarjeta'),
    (2, '2026-03-20', '18:00', '21:00', 10, NULL, 36.00, 'Pendiente', 'Cuota_Mensual'),
    (4, '2026-03-27', '10:00', '12:00', 10, NULL, 30.00, 'Pendiente', 'Cuota_Mensual');

-- LUCÍA NAVARRO (socio 11) — deuda 0 € → VERDE, fav: Sala Polivalente
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (4, '2026-02-17', '11:00', '12:00', 11, NULL, 15.00, 'Pagado', 'Efectivo'),
    (1, '2026-03-10', '10:00', '11:00', 11, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (4, '2026-03-24', '09:00', '10:00', 11, NULL, 15.00, 'Pagado', 'Efectivo');

-- ── 7. Inscripciones ──────────────────────────────────────────────────────
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    -- Socios originales
    (1, 1, '2026-01-10'),   -- Juan:   Yoga
    (1, 2, '2026-02-03'),   -- Juan:   Pilates
    (1, 4, '2026-01-13'),   -- Juan:   Tenis Iniciación
    (2, 1, '2026-01-12'),   -- María:  Yoga
    (2, 2, '2026-02-06'),   -- María:  Pilates
    (2, 3, '2026-02-10'),   -- María:  Zumba Fitness
    (3, 2, '2026-02-05'),   -- Carlos: Pilates
    (4, 1, '2026-01-15'),   -- Ana:    Yoga
    (4, 3, '2026-02-12'),   -- Ana:    Zumba Fitness
    (5, 2, '2026-03-01'),   -- Pedro:  Pilates
    (6, 1, '2026-01-18'),   -- Laura:  Yoga
    (6, 2, '2026-02-10'),   -- Laura:  Pilates
    -- Nuevos socios
    (7, 1, '2026-01-20'),   -- Sofía:  Yoga
    (7, 3, '2026-02-11'),   -- Sofía:  Zumba Fitness
    (7, 2, '2026-02-14'),   -- Sofía:  Pilates
    (8, 4, '2026-01-14'),   -- Álvaro: Tenis Iniciación
    (8, 5, '2026-03-02'),   -- Álvaro: Pádel Avanzado
    (9, 3, '2026-02-10'),   -- Elena:  Zumba Fitness
    (9, 1, '2026-01-22'),   -- Elena:  Yoga
    (10, 2, '2026-02-08'),  -- Roberto: Pilates
    (10, 5, '2026-03-03'),  -- Roberto: Pádel Avanzado
    (11, 1, '2026-01-25'),  -- Lucía:  Yoga
    (11, 2, '2026-02-15'),  -- Lucía:  Pilates
    (11, 3, '2026-02-16');  -- Lucía:  Zumba Fitness