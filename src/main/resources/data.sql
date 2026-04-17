-- ══════════════════════════════════════════════════════════════════════════
-- data.sql  –  Datos de prueba para la HU "Calcular contabilidad de socios"
-- Casos: Febrero 2026 con datos, Enero 2026 sin datos (no genera fichero)
-- ══════════════════════════════════════════════════════════════════════════

-- ── Usuarios ──────────────────────────────────────────────────────────────
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES
    ('52664444B', 'Pablo',  'García López',    612345678, 'pablo@ejemplo.com'),
    ('61014052S', 'Irene',  'Martínez Ruiz',   698765432, 'irene@ejemplo.com'),
    ('33221100C', 'Carlos', 'Fernández Díaz',  666111222, 'carlos@ejemplo.com');

-- ── Socios ────────────────────────────────────────────────────────────────
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES
    (1, '52664444B', 'pass1', 'Al Corriente'),
    (2, '61014052S', 'pass2', 'Al Corriente'),
    (3, '33221100C', 'pass3', 'Deudor');

-- ── Instalaciones ─────────────────────────────────────────────────────────
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES
    ('Pista Padel 1', 'Padel',   20.0),
    ('Pista Tenis 1', 'Tenis',   15.0),
    ('Sala Fitness',  'Fitness', 10.0);

-- ── Periodos de inscripcion ───────────────────────────────────────────────
INSERT INTO PeriodosInscripcion (nombre, descripcion, inicio_socios, fin_socios, fin_no_socios) VALUES
    ('Invierno 2026',  'Periodo invernal',   '2026-01-01', '2026-01-15', '2026-01-22'),
    ('Primavera 2026', 'Periodo primaveral', '2026-03-01', '2026-03-10', '2026-03-20');

-- ── Actividades ───────────────────────────────────────────────────────────
INSERT INTO Actividades (nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, es_evento_social, id_periodo) VALUES
    ('Yoga matutino',     'Clases de yoga',         3, 15, '2026-02-02', '2026-02-28',  60.0,  90.0, 0, 1),
    ('Pilates',           'Clases de pilates',       3, 12, '2026-02-02', '2026-02-28',  50.0,  75.0, 0, 1),
    ('Spinning avanzado', 'Alta intensidad en bici', 3, 10, '2026-04-01', '2026-04-30',  40.0,  65.0, 0, 2);

-- ── Reservas directas de instalaciones ───────────────────────────────────
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-02-05', '10:00', '11:00', 1, NULL, 20.0, 'Pagado',    'Tarjeta'),
    (1, '2026-02-12', '10:00', '11:00', 1, NULL, 20.0, 'Pagado',    'Efectivo'),
    (2, '2026-02-10', '17:00', '18:30', 1, NULL, 22.5, 'Pendiente', NULL),
    (1, '2026-02-07', '12:00', '13:00', 2, NULL, 20.0, 'Pagado',    'Tarjeta'),
    (2, '2026-02-14', '09:00', '10:30', 2, NULL, 22.5, 'Pagado',    'Tarjeta'),
    (2, '2026-02-20', '16:00', '17:00', 3, NULL, 15.0, 'Pendiente', NULL),
    (1, '2026-03-03', '10:00', '11:00', 1, NULL, 20.0, 'Pagado',    'Tarjeta'),
    (2, '2026-03-10', '17:00', '18:30', 2, NULL, 22.5, 'Pagado',    'Efectivo');

-- ── Inscripciones a actividades ───────────────────────────────────────────
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (1, 1, '2026-02-01'),
    (2, 1, '2026-02-01'),
    (2, 2, '2026-02-03'),
    (3, 1, '2026-02-02'),
    (1, 3, '2026-04-01');