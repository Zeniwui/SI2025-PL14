-- ─────────────────────────────────────────────────────────────────
-- DATOS DE PRUEBA: PL-14
-- ─────────────────────────────────────────────────────────────────

-- 1. USUARIOS
-- Creamos dos usuarios para poder tener diferentes perfiles de prueba
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('12345678A', 'Laura', 'García', 600111222, 'laura@email.com');

INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('87654321B', 'Carlos', 'López', 600333444, 'carlos@email.com');


-- 2. SOCIOS
-- Laura está 'Al Corriente' (debería poder inscribirse)
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) 
VALUES (1, '12345678A', 'pass123', 'Al Corriente');

-- Carlos tiene deudas (para probar que salta la ApplicationException "No estás al corriente de pago")
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) 
VALUES (2, '87654321B', 'pass456', 'Moroso');


-- 3. PERIODOS DE INSCRIPCIÓN
-- Periodo ABIERTO actualmente (marzo 2026)
INSERT INTO PeriodosInscripcion (id_periodo, nombre, descripcion, inicio_socios, fin_socios, fin_no_socios) 
VALUES (1, 'Primavera 2026', 'Inscripciones actividades primavera', '2026-03-01', '2026-03-31', '2026-04-10');

-- Periodo CERRADO (futuro - estas actividades no deberían salir en la tabla de la interfaz)
INSERT INTO PeriodosInscripcion (id_periodo, nombre, descripcion, inicio_socios, fin_socios, fin_no_socios) 
VALUES (2, 'Verano 2026', 'Inscripciones actividades verano', '2026-06-01', '2026-06-30', '2026-07-10');


-- 4. INSTALACIONES
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) 
VALUES (1, 'Piscina Climatizada', 'Agua', 40.0);

INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) 
VALUES (2, 'Pista de Pádel 1', 'Pista', 15.0);

INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) 
VALUES (3, 'Sala Multiusos', 'Sala', 25.0);


-- 5. ACTIVIDADES
-- Actividad 1: Disponible, periodo abierto, plazas libres (Aforo 20)
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, es_evento_social, id_periodo) 
VALUES (1, 'Aquagym Nivel Medio', 'Clases de gimnasia en el agua', 1, 20, '2026-04-01', '2026-06-30', 25.50, 35.00, 0, 1);

-- Actividad 2: Periodo abierto, pero vamos a simular que se llena rápido (Aforo 1)
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, es_evento_social, id_periodo) 
VALUES (2, 'Pádel Intensivo', 'Clases de técnica', 2, 1, '2026-04-01', '2026-06-30', 30.00, 45.00, 0, 1);

-- Actividad 3: Periodo cerrado (pertenece al periodo 2, verano). No debería aparecer en la interfaz.
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, es_evento_social, id_periodo) 
VALUES (3, 'Yoga Verano', 'Yoga relajante', 3, 15, '2026-07-01', '2026-08-31', 20.00, 30.00, 0, 2);


-- 6. HORARIOS (Opcional para la inscripción, pero útil para rellenar tu Resumen en la vista)
INSERT INTO Horarios (id_horario, id_actividad, dia_semana, hora_inicio, hora_fin) 
VALUES (1, 1, 'Lunes', '10:00', '11:00');

INSERT INTO Horarios (id_horario, id_actividad, dia_semana, hora_inicio, hora_fin) 
VALUES (2, 2, 'Martes', '18:00', '19:30');

-- ─────────────────────────────────────────────────────────────────
-- DATOS DE PRUEBA: ACTIVIDAD CON AFORO COMPLETO
-- ─────────────────────────────────────────────────────────────────

-- 1. Creamos dos nuevos usuarios y socios para rellenar el aforo
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('11111111C', 'Ana', 'Martínez', 600555666, 'ana@email.com');

INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('22222222D', 'David', 'Gómez', 600777888, 'david@email.com');

INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) 
VALUES (3, '11111111C', 'pass789', 'Al Corriente');

INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) 
VALUES (4, '22222222D', 'pass012', 'Al Corriente');

-- 2. Creamos la actividad con Aforo = 2
-- Usamos id_periodo = 1 (Primavera 2026) para que aparezca en la lista actual
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio, es_evento_social, id_periodo) 
VALUES (4, 'Pilates Avanzado', 'Clase de pilates para expertos', 3, 2, '2026-04-01', '2026-06-30', 20.00, 30.00, 0, 1);

-- 3. Inscribimos a los dos socios nuevos en la actividad (Llenando el aforo de 2)
INSERT INTO Inscripciones (id_inscripcion, id_socio, id_actividad, fecha_inscripcion, precio_inscripcion) 
VALUES (1, 3, 4, CURRENT_DATE, 20.00);

INSERT INTO Inscripciones (id_inscripcion, id_socio, id_actividad, fecha_inscripcion, precio_inscripcion) 
VALUES (2, 4, 4, CURRENT_DATE, 20.00);

-- 4. Generamos los pagos pendientes asociados a esas inscripciones
INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion) 
VALUES (3, 20.00, 'Cuota Mensual', 'Pendiente', 'Cuota actividad: Pilates Avanzado', 1);

INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion) 
VALUES (4, 20.00, 'Cuota Mensual', 'Pendiente', 'Cuota actividad: Pilates Avanzado', 2);