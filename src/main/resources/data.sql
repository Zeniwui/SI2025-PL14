-- ════════════════════════════════════════════════════════════════════════════
-- Usuarios/Socios nuevos — activos en ABRIL 2026 (hasta el día 21 XD)
-- ════════════════════════════════════════════════════════════════════════════

-- ── Usuarios ──────────────────────────────────────────────────────────────
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES
    ('30313233L', 'Marta',   'Serrano Fuentes',  602111222, 'marta.serrano@email.com'),
    ('40414243M', 'Diego',   'Molina Crespo',    602333444, 'diego.molina@email.com'),
    ('50515253N', 'Valeria', 'Ortega Campos',    602555666, 'valeria.ortega@email.com');

-- ── Socios ────────────────────────────────────────────────────────────────
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES
    (12, '30313233L', 'pass012', 'Al Corriente'),
    (13, '40414243M', 'pass013', 'Pendiente'),
    (14, '50515253N', 'pass014', 'Al Corriente');

-- ── MARTA SERRANO (socio 12) ──────────────────────────────────────────────
-- 2 reservas en abril + inscripción a Yoga y Zumba → deuda 0 € → VERDE
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (4, '2026-04-02', '10:00', '11:00', 12, NULL, 15.00, 'Pagado', 'Tarjeta'),
    (1, '2026-04-14', '10:00', '11:00', 12, NULL, 10.00, 'Pagado', 'Efectivo');

INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (12, 1, '2026-04-01'),   -- Marta: Yoga Mañanas
    (12, 3, '2026-04-01');   -- Marta: Zumba Fitness

-- ── DIEGO MOLINA (socio 13) ───────────────────────────────────────────────
-- 3 reservas en abril (2 pendientes) + inscripción a Pádel Avanzado → deuda 24 € → AMARILLO
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (2, '2026-04-03', '18:00', '19:00', 13, NULL, 12.00, 'Pagado',    'Tarjeta'),
    (2, '2026-04-10', '18:00', '19:00', 13, NULL, 12.00, 'Pendiente', 'Cuota_Mensual'),
    (2, '2026-04-17', '18:00', '19:00', 13, NULL, 12.00, 'Pendiente', 'Cuota_Mensual');

INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (13, 5, '2026-04-02');   -- Diego: Pádel Avanzado

-- ── VALERIA ORTEGA (socio 14) ─────────────────────────────────────────────
-- 2 reservas en abril + inscripción a 3 actividades → deuda 0 € → VERDE
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin,
                      id_socio, id_actividad, coste_reserva, estado_pago, metodo_pago) VALUES
    (1, '2026-04-07', '11:00', '12:00', 14, NULL, 10.00, 'Pagado', 'Tarjeta'),
    (3, '2026-04-16', '16:00', '17:00', 14, NULL, 10.00, 'Pagado', 'Efectivo');

INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES
    (14, 2, '2026-04-05'),   -- Valeria: Pilates Tarde
    (14, 4, '2026-04-05'),   -- Valeria: Tenis Iniciación
    (14, 3, '2026-04-06');   -- Valeria: Zumba Fitness