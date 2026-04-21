-- ══════════════════════════════════════════════════════════════════════════
-- data.sql  –  Datos de prueba para la HU "Calcular contabilidad de socios"
--
-- Casos cubiertos:
--   · Febrero 2026   → socios con SOLO reservas, con SOLO actividades y con ambas
--   · Marzo    2026  → mes con datos (reservas + actividades)
--   · Abril    2026  → mes futuro restringido por validación del Model
--   · Enero    2026  → mes sin ningún dato (lista vacía, no genera fichero)
--   · Diciembre 2025 → mes pasado dentro del año permitido (solo reservas)
--   · Socio con varias actividades inscritas en el mismo mes
--   · Socio sin ninguna actividad ni reserva ese mes (no debe aparecer)
--   · Socio 6 (Marta): reserva + actividad en Marzo; solo actividad en Febrero
-- ══════════════════════════════════════════════════════════════════════════

-- ── Usuarios ──────────────────────────────────────────────────────────────
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES
    ('52664444B', 'Pablo',  'García López',    612345678, 'pablo@ejemplo.com'),
    ('61014052S', 'Irene',  'Martínez Ruiz',   698765432, 'irene@ejemplo.com'),
    ('33221100C', 'Carlos', 'Fernández Díaz',  666111222, 'carlos@ejemplo.com'),
    ('77889900D', 'Ana',    'Sánchez Mora',    655444333, 'ana@ejemplo.com'),
    ('11223344E', 'Luis',   'Torres Blanco',   611222333, 'luis@ejemplo.com'),
    ('99887766F', 'Marta',  'Vega Ruiz',       677888999, 'marta@ejemplo.com');

-- ── Socios ────────────────────────────────────────────────────────────────
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES
    (1, '52664444B', 'pass1', 'Al Corriente'),
    (2, '61014052S', 'pass2', 'Al Corriente'),
    (3, '33221100C', 'pass3', 'Deudor'),
    (4, '77889900D', 'pass4', 'Al Corriente'),   -- solo actividades, sin reservas directas en Feb
    (5, '11223344E', 'pass5', 'Al Corriente'),   -- sin actividad ni reserva en Febrero (no aparece)
    (6, '99887766F', 'pass6', 'Al Corriente');   -- reserva+actividad en Marzo; solo actividad en Feb

-- ── Instalaciones ─────────────────────────────────────────────────────────
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES
    ('Pista Padel 1', 'Padel',   20.0),
    ('Pista Tenis 1', 'Tenis',   15.0),
    ('Sala Fitness',  'Fitness', 10.0);

-- ── Periodos de inscripcion ───────────────────────────────────────────────
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