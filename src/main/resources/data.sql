-- ══════════════════════════════════════════════════════════════════════════
-- data.sql  –  Datos de prueba combinados
--
--   SECCIÓN A: HU7 – Generar Informe Detallado de Socios
--   SECCIÓN B: HU8 – Calcular Contabilidad Mensual de Socios
-- ══════════════════════════════════════════════════════════════════════════


-- ════════════════════════════════════════════════════════════════════════════
-- SECCIÓN A: HU7 – Generar Informe Detallado de Socios
-- ════════════════════════════════════════════════════════════════════════════

-- ── A.1. Usuarios ─────────────────────────────────────────────────────────
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

-- ── A.2. Socios ───────────────────────────────────────────────────────────
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

-- ── A.3. Instalaciones ────────────────────────────────────────────────────
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) VALUES
    (1, 'Pista de Tenis 1', 'Exterior',  10.00),
    (2, 'Pista de Pádel 1', 'Cristal',   12.00),
    (3, 'Pista de Pádel 2', 'Muro',      10.00),
    (4, 'Sala Polivalente',  'Interior', 15.00);

-- ── A.4. Periodo de inscripción ───────────────────────────────────────────
INSERT INTO PeriodosInscripcion (id_periodo, nombre, descripcion,
                                  inicio_socios, fin_socios, fin_no_socios) VALUES
    (1, 'Temporada 2026', 'Temporada anual 2026',
        '2026-01-01', '2026-12-31', '2026-12-31');

-- ── A.5. Actividades ──────────────────────────────────────────────────────
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

-- ── A.6. Reservas automáticas de actividades (sin socio) ─────────────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (4, '2026-02-02', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-02-09', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-02-23', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-03-02', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-03-09', '09:00', '10:00', NULL, 1, NULL, NULL, NULL),
    (4, '2026-03-23', '17:00', '18:00', NULL, 2, NULL, NULL, NULL);

-- ── JUAN PÉREZ (socio 1) — fav: Tenis, deuda 24 € → AMARILLO ─────────────
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

-- ── MARÍA LÓPEZ (socio 2) — fav: Pádel 1, deuda 104 € → ROJO ────────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-20', '10:00', '11:00', 2, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (2, '2026-02-20', '18:00', '19:30', 2, NULL, 18.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-02-27', '17:00', '18:40', 2, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-08', '19:00', '21:00', 2, NULL, 24.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-22', '12:00', '13:00', 2, NULL, 12.00, 'Pendiente', 'Cuota_Mensual'),
    (4, '2026-03-29', '10:00', '12:00', 2, NULL, 30.00, 'Pendiente', 'Cuota_Mensual');

-- ── CARLOS RUIZ (socio 3) — deuda 0 € → VERDE ────────────────────────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-14', '10:00', '11:00', 3, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (4, '2026-02-21', '11:00', '12:00', 3, NULL, 15.00, 'Pagado', 'Efectivo'),
    (1, '2026-03-07', '09:00', '10:00', 3, NULL, 10.00, 'Pagado', 'Tarjeta');

-- ── ANA RUIZ TORRES (socio 4) — deuda 33 € → AMARILLO ───────────────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-26', '10:00', '12:00', 4, NULL, 20.00, 'Pagado',    'Tarjeta'),
    (2, '2026-02-28', '17:00', '18:30', 4, NULL, 18.00, 'Pendiente', 'Cuota_Mensual'),
    (3, '2026-03-14', '11:00', '13:00', 4, NULL, 20.00, 'Pagado',    'Efectivo'),
    (4, '2026-03-21', '10:00', '11:00', 4, NULL, 15.00, 'Pendiente', 'Cuota_Mensual');

-- ── PEDRO GÓMEZ (socio 5) — deuda 0 € → VERDE ────────────────────────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-03-15', '10:00', '11:00', 5, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (3, '2026-03-28', '16:00', '17:00', 5, NULL, 10.00, 'Pagado', 'Efectivo');

-- ── LAURA FERNÁNDEZ (socio 6) — deuda 54 € → AMARILLO ───────────────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (2, '2026-02-24', '18:00', '20:00', 6, NULL, 24.00, 'Pendiente', 'Cuota_Mensual'),
    (4, '2026-03-03', '10:00', '12:00', 6, NULL, 30.00, 'Pendiente', 'Cuota_Mensual'),
    (1, '2026-03-17', '10:00', '11:00', 6, NULL, 10.00, 'Pagado',    'Tarjeta');

-- ── SOFÍA MARTÍNEZ (socio 7) — deuda 0 € → VERDE, fav: Sala Polivalente ──
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (4, '2026-02-05', '10:00', '11:00', 7, NULL, 15.00, 'Pagado', 'Tarjeta'),
    (4, '2026-02-19', '11:00', '12:00', 7, NULL, 15.00, 'Pagado', 'Tarjeta'),
    (4, '2026-03-11', '09:00', '10:00', 7, NULL, 15.00, 'Pagado', 'Efectivo'),
    (1, '2026-03-25', '10:00', '11:00', 7, NULL, 10.00, 'Pagado', 'Tarjeta');

-- ── ÁLVARO CASTILLO (socio 8) — deuda 50 € → AMARILLO, fav: Tenis ────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-10', '10:00', '12:00', 8, NULL, 20.00, 'Pagado',    'Tarjeta'),
    (1, '2026-02-18', '15:00', '17:00', 8, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-04', '18:00', '20:00', 8, NULL, 24.00, 'Pagado',    'Efectivo'),
    (1, '2026-03-19', '10:00', '12:00', 8, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (3, '2026-03-26', '11:00', '12:00', 8, NULL, 10.00, 'Pendiente', 'Cuota_Mensual');

-- ── ELENA VEGA (socio 9) — deuda 0 € → VERDE, fav: Pádel 2 ──────────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (3, '2026-02-13', '16:00', '17:00', 9, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (3, '2026-03-20', '16:00', '17:00', 9, NULL, 10.00, 'Pagado', 'Tarjeta');

-- ── ROBERTO HERRERA (socio 10) — deuda 132 € → ROJO, fav: Pádel 1 ────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (2, '2026-02-06', '18:00', '19:30', 10, NULL, 18.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-02-13', '17:00', '18:40', 10, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-06', '19:00', '21:20', 10, NULL, 28.00, 'Pendiente', 'Cuota_Mensual'),
    (1, '2026-03-13', '10:00', '12:00', 10, NULL, 20.00, 'Pagado',    'Tarjeta'),
    (2, '2026-03-20', '18:00', '21:00', 10, NULL, 36.00, 'Pendiente', 'Cuota_Mensual'),
    (4, '2026-03-27', '10:00', '12:00', 10, NULL, 30.00, 'Pendiente', 'Cuota_Mensual');

-- ── LUCÍA NAVARRO (socio 11) — deuda 0 € → VERDE, fav: Sala Polivalente ──
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (4, '2026-02-17', '11:00', '12:00', 11, NULL, 15.00, 'Pagado', 'Efectivo'),
    (1, '2026-03-10', '10:00', '11:00', 11, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (4, '2026-03-24', '09:00', '10:00', 11, NULL, 15.00, 'Pagado', 'Efectivo');

-- ── A.7. Inscripciones ────────────────────────────────────────────────────
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (1, 1, '2026-01-10'),   -- Juan:    Yoga
    (1, 2, '2026-02-03'),   -- Juan:    Pilates
    (1, 4, '2026-01-13'),   -- Juan:    Tenis Iniciación
    (2, 1, '2026-01-12'),   -- María:   Yoga
    (2, 2, '2026-02-06'),   -- María:   Pilates
    (2, 3, '2026-02-10'),   -- María:   Zumba Fitness
    (3, 2, '2026-02-05'),   -- Carlos:  Pilates
    (4, 1, '2026-01-15'),   -- Ana:     Yoga
    (4, 3, '2026-02-12'),   -- Ana:     Zumba Fitness
    (5, 2, '2026-03-01'),   -- Pedro:   Pilates
    (6, 1, '2026-01-18'),   -- Laura:   Yoga
    (6, 2, '2026-02-10'),   -- Laura:   Pilates
    (7, 1, '2026-01-20'),   -- Sofía:   Yoga
    (7, 3, '2026-02-11'),   -- Sofía:   Zumba Fitness
    (7, 2, '2026-02-14'),   -- Sofía:   Pilates
    (8, 4, '2026-01-14'),   -- Álvaro:  Tenis Iniciación
    (8, 5, '2026-03-02'),   -- Álvaro:  Pádel Avanzado
    (9, 3, '2026-02-10'),   -- Elena:   Zumba Fitness
    (9, 1, '2026-01-22'),   -- Elena:   Yoga
    (10, 2, '2026-02-08'),  -- Roberto: Pilates
    (10, 5, '2026-03-03'),  -- Roberto: Pádel Avanzado
    (11, 1, '2026-01-25'),  -- Lucía:   Yoga
    (11, 2, '2026-02-15'),  -- Lucía:   Pilates
    (11, 3, '2026-02-16');  -- Lucía:   Zumba Fitness

-- ── Socios activos en ABRIL 2026 ──────────────────────────────────────────
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES
    ('30313233L', 'Marta',   'Serrano Fuentes',  602111222, 'marta.serrano@email.com'),
    ('40414243M', 'Diego',   'Molina Crespo',    602333444, 'diego.molina@email.com'),
    ('50515253N', 'Valeria', 'Ortega Campos',    602555666, 'valeria.ortega@email.com');

INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES
    (12, '30313233L', 'pass012', 'Al Corriente'),
    (13, '40414243M', 'pass013', 'Pendiente'),
    (14, '50515253N', 'pass014', 'Al Corriente');

-- MARTA SERRANO (socio 12) — 2 reservas, 2 actividades, deuda 0 € → VERDE
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (4, '2026-04-02', '10:00', '11:00', 12, NULL, 15.00, 'Pagado', 'Tarjeta'),
    (1, '2026-04-14', '10:00', '11:00', 12, NULL, 10.00, 'Pagado', 'Efectivo');

INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (12, 1, '2026-04-01'),   -- Marta: Yoga Mañanas
    (12, 3, '2026-04-01');   -- Marta: Zumba Fitness

-- DIEGO MOLINA (socio 13) — 3 reservas, 2 actividades, deuda 24 € → AMARILLO
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (2, '2026-04-03', '18:00', '19:00', 13, NULL, 12.00, 'Pagado',    'Tarjeta'),
    (2, '2026-04-10', '18:00', '19:00', 13, NULL, 12.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-04-17', '18:00', '19:00', 13, NULL, 12.00, 'Pendiente', 'Cuota_Mensual');

INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (13, 5, '2026-04-02'),   -- Diego: Pádel Avanzado
    (13, 1, '2026-04-03');   -- Diego: Yoga Mañanas

-- VALERIA ORTEGA (socio 14) — 2 reservas, 3 actividades, deuda 0 € → VERDE
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-04-07', '11:00', '12:00', 14, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (3, '2026-04-16', '16:00', '17:00', 14, NULL, 10.00, 'Pagado', 'Efectivo');

INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (14, 2, '2026-04-05'),   -- Valeria: Pilates Tarde
    (14, 4, '2026-04-05'),   -- Valeria: Tenis Iniciación
    (14, 3, '2026-04-06');   -- Valeria: Zumba Fitness

-- PEDRO GÓMEZ (socio 5) — 0 reservas en abril, 5 actividades → VERDE
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (5, 1, '2026-04-01'),   -- Pedro: Yoga Mañanas
    (5, 3, '2026-04-01'),   -- Pedro: Zumba Fitness
    (5, 4, '2026-04-02'),   -- Pedro: Tenis Iniciación
    (5, 5, '2026-04-03'),   -- Pedro: Pádel Avanzado
    (5, 2, '2026-04-04');   -- Pedro: Pilates Tarde

-- MIGUEL TORRES (socio 15) — activo en febrero, marzo y abril
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES
    ('60616263O', 'Miguel', 'Torres Aguilar', 603111222, 'miguel.torres@email.com');

INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES
    (15, '60616263O', 'pass015', 'Al Corriente');

INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (3, '2026-02-11', '16:00', '17:00', 15, NULL, 10.00, 'Pagado', 'Tarjeta'),   -- Feb: Pádel 2
    (4, '2026-03-18', '11:00', '12:00', 15, NULL, 15.00, 'Pagado', 'Efectivo'),  -- Mar: Sala Polivalente
    (3, '2026-04-09', '16:00', '17:00', 15, NULL, 10.00, 'Pagado', 'Tarjeta');   -- Abr: Pádel 2

INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (15, 3, '2026-02-11'),   -- Miguel: Zumba Fitness
    (15, 5, '2026-04-08');   -- Miguel: Pádel Avanzado


-- ════════════════════════════════════════════════════════════════════════════
-- SECCIÓN B: HU8 – Calcular Contabilidad Mensual de Socios
--
-- Casos cubiertos:
--   · Febrero 2026   → socios con SOLO reservas, con SOLO actividades y con ambas
--   · Marzo    2026  → mes con datos (reservas + actividades)
--   · Abril    2026  → mes futuro restringido por validación del Model
--   · Enero    2026  → mes sin ningún dato (lista vacía, no genera fichero)
--   · Diciembre 2025 → mes pasado dentro del año permitido (solo reservas)
--   · Socio con varias actividades inscritas en el mismo mes
--   · Socio sin ninguna actividad ni reserva ese mes (no debe aparecer)
-- ════════════════════════════════════════════════════════════════════════════

-- ── B.1. Usuarios ─────────────────────────────────────────────────────────
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES
    ('52664444B', 'Pablo',  'García López',    612345678, 'pablo@ejemplo.com'),
    ('61014052S', 'Irene',  'Martínez Ruiz',   698765432, 'irene@ejemplo.com'),
    ('33221100C', 'Carlos', 'Fernández Díaz',  666111222, 'carlos@ejemplo.com'),
    ('77889900D', 'Ana',    'Sánchez Mora',    655444333, 'ana@ejemplo.com'),
    ('11223344E', 'Luis',   'Torres Blanco',   611222333, 'luis@ejemplo.com'),
    ('99887766F', 'Marta',  'Vega Ruiz',       677888999, 'marta@ejemplo.com');

-- ── B.2. Socios ───────────────────────────────────────────────────────────
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES
    (16, '52664444B', 'pass1', 'Al Corriente'),
    (17, '61014052S', 'pass2', 'Al Corriente'),
    (18, '33221100C', 'pass3', 'Deudor'),
    (19, '77889900D', 'pass4', 'Al Corriente'),   -- solo actividades, sin reservas directas en Feb
    (20, '11223344E', 'pass5', 'Al Corriente'),   -- sin actividad ni reserva en Febrero (no aparece)
    (21, '99887766F', 'pass6', 'Al Corriente');   -- reserva+actividad en Marzo; solo actividad en Feb

-- ── B.3. Instalaciones ────────────────────────────────────────────────────
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES
    ('Pista Padel 1', 'Padel',   20.0),
    ('Pista Tenis 1', 'Tenis',   15.0),
    ('Sala Fitness',  'Fitness', 10.0);

-- ── B.4. Periodos de inscripcion ──────────────────────────────────────────
INSERT INTO PeriodosInscripcion (nombre, descripcion, inicio_socios, fin_socios, fin_no_socios) VALUES
    ('Invierno 2025',  'Periodo invernal 2025',   '2025-11-15', '2025-11-30', '2025-12-07'),
    ('Invierno 2026',  'Periodo invernal 2026',   '2026-01-01', '2026-01-15', '2026-01-22'),
    ('Primavera 2026', 'Periodo primaveral 2026', '2026-03-01', '2026-03-10', '2026-03-20');

-- ── Actividades ───────────────────────────────────────────────────────────
INSERT INTO Actividades (nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin,
                         precio_socio, precio_no_socio, es_evento_social, id_periodo) VALUES
    -- Febrero 2026 (id 1, 2)
    ('Yoga matutino',       'Clases de yoga',         3, 15, '2026-02-02', '2026-02-28',  60.0,  90.0, 0, 2),
    ('Pilates',             'Clases de pilates',       3, 12, '2026-02-02', '2026-02-28',  50.0,  75.0, 0, 2),
    -- Marzo 2026 (id 3, 4)
    ('Spinning avanzado',   'Alta intensidad en bici', 3, 10, '2026-03-05', '2026-03-31',  40.0,  65.0, 0, 3),
    ('Aquagym',             'Aerobic en piscina',      3,  8, '2026-03-08', '2026-03-31',  45.0,  70.0, 0, 3),
    -- Diciembre 2025 (id 5)
    ('Zumba navideña',      'Baile festivo',           3, 20, '2025-12-01', '2025-12-31',  35.0,  55.0, 0, 1);

-- ── Reservas directas de instalaciones ───────────────────────────────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    -- Febrero 2026
    (1, '2026-02-05', '10:00', '11:00', 1, NULL, 20.0, 'Pagado',    'Tarjeta'),
    (1, '2026-02-12', '10:00', '11:00', 1, NULL, 20.0, 'Pagado',    'Efectivo'),
    (2, '2026-02-10', '17:00', '18:30', 1, NULL, 22.5, 'Pendiente', NULL),
    (1, '2026-02-07', '12:00', '13:00', 2, NULL, 20.0, 'Pagado',    'Tarjeta'),
    (2, '2026-02-14', '09:00', '10:30', 2, NULL, 22.5, 'Pagado',    'Tarjeta'),
    (2, '2026-02-20', '16:00', '17:00', 3, NULL, 15.0, 'Pendiente', NULL),

    -- Marzo 2026
    (1, '2026-03-03', '10:00', '11:00', 1, NULL, 20.0, 'Pagado',    'Tarjeta'),
    (2, '2026-03-10', '17:00', '18:30', 2, NULL, 22.5, 'Pagado',    'Efectivo'),
    (1, '2026-03-15', '11:00', '12:00', 3, NULL, 20.0, 'Pendiente', NULL),
    (2, '2026-03-22', '16:00', '17:30', 5, NULL, 22.5, 'Pagado',    'Tarjeta'),
    -- Marta (6): una reserva directa en Marzo
    (1, '2026-03-18', '10:00', '11:00', 6, NULL, 20.0, 'Pagado',    'Tarjeta'),

    -- Diciembre 2025 (mes pasado dentro del año permitido)
    (1, '2025-12-05', '09:00', '10:00', 1, NULL, 20.0, 'Pagado',    'Tarjeta'),
    (2, '2025-12-18', '17:00', '18:00', 2, NULL, 15.0, 'Pagado',    'Efectivo'),

    -- Reserva de Luis (5) solo en Marzo → no aparece en Febrero
    (1, '2026-03-28', '10:00', '11:00', 5, NULL, 20.0, 'Pagado',    'Tarjeta');

-- ── Inscripciones a actividades ───────────────────────────────────────────
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    -- Febrero 2026
    (1, 1, '2026-02-01'),   -- Pablo:  Yoga
    (2, 1, '2026-02-01'),   -- Irene:  Yoga
    (2, 2, '2026-02-03'),   -- Irene:  Pilates
    (3, 1, '2026-02-02'),   -- Carlos: Yoga
    (4, 1, '2026-02-04'),   -- Ana:    Yoga  (sin reservas directas ese mes)
    (4, 2, '2026-02-05'),   -- Ana:    Pilates
    (6, 1, '2026-02-10'),   -- Marta:  solo actividad en Febrero (sin reserva directa ese mes)

    -- Marzo 2026
    (1, 3, '2026-03-02'),   -- Pablo:  Spinning
    (2, 3, '2026-03-02'),   -- Irene:  Spinning
    (3, 4, '2026-03-05'),   -- Carlos: Aquagym
    (4, 3, '2026-03-03'),   -- Ana:    Spinning
    (4, 4, '2026-03-06'),   -- Ana:    Aquagym
    (6, 3, '2026-03-04'),   -- Marta:  Spinning (+ reserva directa ese mes)

    -- Diciembre 2025
    (1, 5, '2025-12-01'),
    (3, 5, '2025-12-03');
