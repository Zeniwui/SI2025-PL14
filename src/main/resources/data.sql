DELETE FROM Pagos;
DELETE FROM Reservas;
DELETE FROM Socios;
DELETE FROM Usuarios;
DELETE FROM Instalaciones;
DELETE FROM Inscripciones;
DELETE FROM Actividades;

-- Insertar Usuario y Socio con ID EXPLÍCITO
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('12345678A', 'Carlos', 'Gomez', 600111222, 'carlos@email.com');
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('11111111A', 'Marcos', 'Suarez', 600333222, 'ma@email.com');
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('22222222A', 'Lucia', 'Perez', 600444222, 'luci@email.com');


INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) 
VALUES (1, '12345678A', 'pass123', 'Al Corriente');
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) 
VALUES (2, '11111111A', 'pass124', 'Al Corriente');
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) 
VALUES (3, '22222222A', 'pass125', 'Al Corriente');

-- Insertar Reserva vinculada al Socio 1
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago)
VALUES (1, '2026-05-10', '10:00', '11:00', 1, 12.00, 'Pendiente');
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago)
VALUES (1, '2026-05-10', '11:00', '12:00', 1, 12.00, 'Pendiente');
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago)
VALUES (2, '2026-05-10', '13:00', '14:00', 2, 12.00, 'Pendiente');
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago)
VALUES (3, '2026-05-10', '14:00', '15:00', 3, 12.00, 'Pendiente');

-- Insertar Instalaciones (necesarias para las actividades)
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) 
VALUES (1, 'Pista Tenis 1', 'Tenis', 10.00);
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) 
VALUES (2, 'Piscina Cubierta', 'Natación', 15.00);
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) 
VALUES (3, 'Sala Polivalente', 'Fitness', 8.00);

-- Insertar Actividades
-- Actividad A: Padel Iniciación (Aforo 20)
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio)
VALUES (1, 'Padel Iniciación', 'Clases para principiantes', 1, 20, '2026-06-01', '2026-06-30', 20.00, 40.00);

-- Actividad B: Natación 2 (Aforo 15)
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, precio_no_socio)
VALUES (2, 'Natación 2', 'Nivel intermedio adultos', 2, 15, '2026-06-01', '2026-06-30', 25.00, 50.00);

-- Crear Reservas automáticas para estas actividades
-- (La IU pide que se liberen las instalaciones, así que deben tener reservas)
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago)
VALUES (1, '2026-06-01', '09:00', '10:00', 1, 0.00, 'Pagado');
INSERT INTO Reservas (id_instalacion, fecha, hora_inicio, hora_fin, id_actividad, coste_reserva, estado_pago)
VALUES (2, '2026-06-01', '10:00', '11:00', 2, 0.00, 'Pagado');

-- Insertar Inscripciones para simular el "bajo mínimo"
-- Inscribimos a Carlos (ID 1), Marcos (ID 2) y Lucia (ID 3) en Padel (Total: 3 inscritos)
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES (1, 1, '2026-04-10');
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES (2, 1, '2026-04-11');
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES (3, 1, '2026-04-12');

-- Inscribimos solo a Carlos (ID 1) en Natación (Total: 1 inscrito)
INSERT INTO Inscripciones (id_socio, id_actividad, fecha_inscripcion) VALUES (1, 2, '2026-04-10');

-- Insertar Pagos vinculados a esas inscripciones
-- Esto servirá para probar que el sistema "devuelve" el dinero al cancelar
INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion)
VALUES (1, 20.00, 'Tarjeta', 'Pagado', 'Inscripción Padel Carlos', 1);
INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion)
VALUES (1, 25.00, 'Cuota', 'Pendiente', 'Inscripción Natación Carlos', 4);
INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion)
VALUES (2, 20.00, 'Transferencia', 'Pagado', 'Inscripción Padel Marcos', 2);

INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion)
VALUES (3, 20.00, 'Tarjeta', 'Pagado', 'Inscripción Padel Lucia', 3);

-- 1. Insertar Reservas de Alquiler (Socios)
INSERT INTO Reservas (id_reserva, id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago)
VALUES (17, 1, '2026-05-10', '10:00', '11:00', 1, 12.00, 'Pagado');
INSERT INTO Reservas (id_reserva, id_instalacion, fecha, hora_inicio, hora_fin, id_socio, coste_reserva, estado_pago)
VALUES (19, 2, '2026-05-10', '13:00', '14:00', 2, 12.00, 'Pagado');

-- 2. Insertar Inscripciones (Actividades)
INSERT INTO Inscripciones (id_inscripcion, id_socio, id_actividad, fecha_inscripcion) VALUES (1, 1, 1, '2026-04-10');
INSERT INTO Inscripciones (id_inscripcion, id_socio, id_actividad, fecha_inscripcion) VALUES (2, 2, 1, '2026-04-11');

-- 3. INSERTAR PAGOS CRUCIALES
-- Pago vinculado a RESERVA (Para la HU de Anulación de Reservas)
INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_reserva)
VALUES (1, 12.00, 'Tarjeta', 'Pagado', 'Pago Alquiler Pista Reserva #17', 17);

-- Pagos vinculados a INSCRIPCIÓN (Para la HU de Cancelación Actividades)
INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion)
VALUES (1, 20.00, 'Tarjeta', 'Pagado', 'Inscripción Padel Carlos', 1);
INSERT INTO Pagos (id_socio, monto, metodo_pago, estado_pago, concepto, id_inscripcion)
VALUES (2, 20.00, 'Transferencia', 'Pagado', 'Inscripción Padel Marcos', 2);
