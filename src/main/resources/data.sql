-- Desactivar FK temporalmente para limpiar sin errores
PRAGMA foreign_keys = OFF;

DELETE FROM Pagos;
DELETE FROM Reservas;
DELETE FROM Socios;
DELETE FROM Usuarios;

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

PRAGMA foreign_keys = ON;