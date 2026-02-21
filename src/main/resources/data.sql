DELETE FROM Horarios;
DELETE FROM Reservas;
DELETE FROM Actividades;
DELETE FROM Instalaciones;
DELETE FROM PeriodosInscripcion;
DELETE FROM Socios;
DELETE FROM Usuarios;
-- Aquí añadiremos datos iniciales a la base de datos

-- Datos iniciales para Instalaciones
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Pista de Tenis 1', 'Exterior', 10.0);
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Piscina Climatizada', 'Interior', 15.5);
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Sala de Musculación', 'Interior', 5.0);
INSERT INTO Instalaciones (nombre, tipo, coste_hora) VALUES ('Cancha de Baloncesto', 'Exterior', 8.0);

-- Datos iniciales para Periodos de Inscripción
-- Formato de fecha estándar: YYYY-MM-DD
INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Temporada Verano', '2024-06-01', '2024-06-15', '2024-08-30');

INSERT INTO PeriodosInscripcion (nombre, inicio_socios, fin_socios, fin_no_socios) 
VALUES ('Temporada Invierno', '2024-10-01', '2024-10-15', '2024-12-20');

-- Datos para Usuarios y Socios (opcional, para pruebas de reservas)
INSERT INTO Usuarios (dni, nombre, apellidos, telefono, email) 
VALUES ('12345678A', 'Juan', 'Pérez García', 600123456, 'juan.perez@email.com');

INSERT INTO Socios (id_socio, dni, contrasena, estado_pagos) 
VALUES (1, '12345678A', 'password123', 'Al Corriente');