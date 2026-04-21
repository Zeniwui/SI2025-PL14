-- ══════════════════════════════════════════════════════════════════════════
-- data.sql – Datos de prueba para la HU "Generar Informe Detallado de Socios"
--
-- Casos cubiertos en el rango 01/02/2026 – 31/03/2026:
--   · Fila ROJA    (deuda > 100): María López         → 104 € pendientes
--   · Fila AMARILLA (0 < d ≤ 100): Juan Pérez         →  24 €
--                                   Ana Ruiz Torres    →  33 €
--                                   Laura Fernández    →  54 €
--   · Fila VERDE   (deuda = 0):   Carlos Ruiz Sánchez →   0 € (todo pagado)
--                                  Pedro Gómez Vidal   →   0 € (solo marzo)
--   · Socio activo solo en Enero → no aparece en rango Feb-Mar
--   · Actividades: Yoga (desde Ene) y Pilates (desde Feb), con inscripciones
--     variadas para testear la columna num_actividades
--   · Instalación favorita distinta por socio para verificar la subquery
-- ══════════════════════════════════════════════════════════════════════════

-- ── 1. Usuarios ───────────────────────────────────────────────────────────
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES
    ('12345678A', 'Juan',   'Pérez García',     600111222, 'juan.perez@email.com'),
    ('87654321B', 'María',  'López Martínez',   600333444, 'maria.lopez@email.com'),
    ('11223344C', 'Carlos', 'Ruiz Sánchez',     600555666, 'carlos.ruiz@email.com'),
    ('44556677D', 'Ana',    'Ruiz Torres',      600777888, 'ana.ruiz@email.com'),
    ('55667788E', 'Pedro',  'Gómez Vidal',      600999000, 'pedro.gomez@email.com'),
    ('66778899F', 'Laura',  'Fernández Díaz',   601111222, 'laura.fernandez@email.com');

-- ── 2. Socios ─────────────────────────────────────────────────────────────
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES
    (1, '12345678A', 'pass123',    'Al Corriente'),
    (2, '87654321B', 'securePass', 'Pendiente'),     -- deuda alta → fila ROJA
    (3, '11223344C', 'abc456',     'Al Corriente'),  -- todo pagado → fila VERDE
    (4, '44556677D', 'xyz789',     'Pendiente'),     -- deuda baja → fila AMARILLA
    (5, '55667788E', 'pass555',    'Al Corriente'),  -- solo Marzo, sin deuda → fila VERDE
    (6, '66778899F', 'pass666',    'Pendiente');     -- deuda media → fila AMARILLA

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
-- Yoga (id 1): fecha_inicio 2026-01-05 → se filtra con rango desde Enero
-- Pilates (id 2): fecha_inicio 2026-02-02 → se filtra con rango desde Febrero
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo,
                          fecha_inicio, fecha_fin, precio_socio, precio_no_socio, id_periodo) VALUES
    (1, 'Yoga Mañanas',  'Clase de Yoga suave',      4, 20,
        '2026-01-05', '2026-12-31', 20.00, 30.00, 1),
    (2, 'Pilates Tarde', 'Pilates de nivel medio',   4, 15,
        '2026-02-02', '2026-06-30', 25.00, 38.00, 1);

INSERT INTO Horarios (id_actividad, dia_semana, hora_inicio, hora_fin) VALUES
    (1, 'Lunes',      '09:00', '10:00'),
    (2, 'Miércoles',  '17:00', '18:00');

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

-- ── JUAN PÉREZ (socio 1) ──────────────────────────────────────────────────
-- Enero (fuera del rango Feb-Mar, para probar que no aparece en ese filtro)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-01-10', '10:00', '11:00', 1, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (1, '2026-01-17', '10:00', '12:00', 1, NULL, 20.00, 'Pendiente', 'Cuota_Mensual');

-- Febrero y Marzo (instalación favorita: Tenis 1 × 4 vs Pádel 1 × 2)
-- Deuda pendiente: 12 + 12 = 24 € → fila AMARILLA
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-12', '10:00', '11:00', 1, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (2, '2026-02-19', '16:00', '17:00', 1, NULL, 12.00, 'Pendiente', 'Cuota_Mensual'),
    (1, '2026-03-05', '18:00', '19:00', 1, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (1, '2026-03-12', '10:00', '11:00', 1, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (1, '2026-03-18', '16:00', '18:00', 1, NULL, 20.00, 'Pagado',    'Tarjeta'),
    (2, '2026-03-26', '11:00', '12:00', 1, NULL, 12.00, 'Pendiente', 'Cuota_Mensual');

-- ── MARÍA LÓPEZ (socio 2) ─────────────────────────────────────────────────
-- Instalación favorita: Pádel 1 × 4 vs Tenis 1 × 1 vs Sala × 1
-- Deuda pendiente: 18+20+24+12+30 = 104 € → fila ROJA (> 100)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-20', '10:00', '11:00', 2, NULL, 10.00, 'Pagado',    'Tarjeta'),
    (2, '2026-02-20', '18:00', '19:30', 2, NULL, 18.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-02-27', '17:00', '18:40', 2, NULL, 20.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-08', '19:00', '21:00', 2, NULL, 24.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-03-22', '12:00', '13:00', 2, NULL, 12.00, 'Pendiente', 'Cuota_Mensual'),
    (4, '2026-03-29', '10:00', '12:00', 2, NULL, 30.00, 'Pendiente', 'Cuota_Mensual');

-- ── CARLOS RUIZ (socio 3) ─────────────────────────────────────────────────
-- Instalación favorita: Tenis 1 × 2 vs Sala × 1
-- Deuda pendiente: 0 € → fila VERDE (todo pagado)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-14', '10:00', '11:00', 3, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (4, '2026-02-21', '11:00', '12:00', 3, NULL, 15.00, 'Pagado', 'Efectivo'),
    (1, '2026-03-07', '09:00', '10:00', 3, NULL, 10.00, 'Pagado', 'Tarjeta');

-- ── ANA RUIZ TORRES (socio 4) ─────────────────────────────────────────────
-- Instalación favorita: Tenis 1 × 1 = Pádel 1 × 1 = Pádel 2 × 1 = Sala × 1 (empate → primero hallado)
-- Deuda pendiente: 18 + 15 = 33 € → fila AMARILLA
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-26', '10:00', '12:00', 4, NULL, 20.00, 'Pagado',    'Tarjeta'),
    (2, '2026-02-28', '17:00', '18:30', 4, NULL, 18.00, 'Pendiente', 'Cuota_Mensual'),
    (3, '2026-03-14', '11:00', '13:00', 4, NULL, 20.00, 'Pagado',    'Efectivo'),
    (4, '2026-03-21', '10:00', '11:00', 4, NULL, 15.00, 'Pendiente', 'Cuota_Mensual');

-- ── PEDRO GÓMEZ (socio 5) ─────────────────────────────────────────────────
-- Solo reservas en Marzo, instalación favorita: Tenis 1
-- Deuda pendiente: 0 € → fila VERDE
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-03-15', '10:00', '11:00', 5, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (3, '2026-03-28', '16:00', '17:00', 5, NULL, 10.00, 'Pagado', 'Efectivo');

-- ── LAURA FERNÁNDEZ (socio 6) ─────────────────────────────────────────────
-- Instalación favorita: Pádel 1 × 1 vs Sala × 1 vs Tenis 1 × 1 (primera hallada)
-- Deuda pendiente: 24 + 30 = 54 € → fila AMARILLA
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (2, '2026-02-24', '18:00', '20:00', 6, NULL, 24.00, 'Pendiente', 'Cuota_Mensual'),
    (4, '2026-03-03', '10:00', '12:00', 6, NULL, 30.00, 'Pendiente', 'Cuota_Mensual'),
    (1, '2026-03-17', '10:00', '11:00', 6, NULL, 10.00, 'Pagado',    'Tarjeta');

-- ── 7. Inscripciones ──────────────────────────────────────────────────────
-- La query del Model compara a.fecha_inicio BETWEEN fechaDesde AND fechaHasta.
-- Para rango 01/02/2026-31/03/2026:
--   · Yoga   (fecha_inicio 2026-01-05) → fuera del rango, NO cuenta en Feb-Mar
--   · Pilates (fecha_inicio 2026-02-02) → dentro, SÍ cuenta en Feb-Mar
-- Para rango 01/01/2026-31/03/2026:
--   · Yoga cuenta también → num_actividades sube
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (1, 1, '2026-01-10'),   -- Juan:   Yoga  (cuenta solo en rango desde Enero)
    (1, 2, '2026-02-03'),   -- Juan:   Pilates → 1 actividad en Feb-Mar
    (2, 1, '2026-01-12'),   -- María:  Yoga  (solo rango desde Enero)
    (2, 2, '2026-02-06'),   -- María:  Pilates → 1 actividad en Feb-Mar
    (3, 2, '2026-02-05'),   -- Carlos: Pilates → 1 actividad en Feb-Mar
    (4, 1, '2026-01-15'),   -- Ana:    Yoga  (solo rango desde Enero)
    (5, 2, '2026-03-01'),   -- Pedro:  Pilates → 1 actividad en rango desde Mar
    (6, 1, '2026-01-18'),   -- Laura:  Yoga  (solo rango desde Enero)
    (6, 2, '2026-02-10');   -- Laura:  Pilates → 1 actividad en Feb-Mar