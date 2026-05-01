-- 1. Crear un periodo de inscripción válido y actual
-- (Asegúrate de que las fechas abarcan el día de hoy)
INSERT INTO PeriodosInscripcion (id_periodo, nombre, descripcion, inicio_socios, fin_socios, fin_no_socios) 
VALUES (1, 'Primavera 2026', 'Periodo de primavera', '2026-01-01', '2026-12-31', '2026-12-31');

-- 2. Crear un par de instalaciones
INSERT INTO Instalaciones (id_instalacion, nombre, tipo, coste_hora) 
VALUES 
(1, 'Piscina Cubierta', 'Agua', 15.0),
(2, 'Sala Fitness', 'Gimnasio', 10.0);

-- 3. Crear actividades
-- Actividad 1: Aforo muy pequeño (2 personas) para llenarla fácilmente
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, id_periodo) 
VALUES (1, 'Natación Avanzada', 'Clase perfeccionamiento', 1, 2, '2026-05-01', '2026-05-31', 25.00, 1);

-- Actividad 2: Aforo grande, sin llenar
INSERT INTO Actividades (id_actividad, nombre, descripcion, id_instalacion, aforo, fecha_inicio, fecha_fin, precio_socio, id_periodo) 
VALUES (2, 'Yoga Matinal', 'Yoga relax por las mañanas', 2, 20, '2026-05-01', '2026-05-31', 15.00, 1);

-- 4. Crear los Usuarios y Socios
-- Socio 1: Es el ID_SOCIO_ACTUAL que usa tu controlador por defecto (el que va a probar la App)
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES ('11111111A', 'Juan', 'Pérez', 600111222, 'juan@mail.com');
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES (1, '11111111A', '1234', 'Al Corriente');

-- Socio 2 y Socio 3: Los usaremos para llenar la Actividad 1
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES ('22222222B', 'Ana', 'Gómez', 600222333, 'ana@mail.com');
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES (2, '22222222B', '1234', 'Al Corriente');

INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) VALUES ('33333333C', 'Luis', 'Ruiz', 600333444, 'luis@mail.com');
INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) VALUES (3, '33333333C', '1234', 'Al Corriente');

-- 5. Llenar el aforo de la Actividad 1
-- Inscribimos al Socio 2 y al Socio 3 en "Natación Avanzada" (Aforo de 2 alcanzado)
INSERT INTO Inscripciones (id_socio, id_actividad, precio_inscripcion) VALUES (2, 1, 25.00);
INSERT INTO Inscripciones (id_socio, id_actividad, precio_inscripcion) VALUES (3, 1, 25.00);