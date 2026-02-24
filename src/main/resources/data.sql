-- Aquí añadiremos datos iniciales a la base de datos

-- ─────────────────────────────────────────────────────────────────
-- Datos iniciales PL-14
-- ─────────────────────────────────────────────────────────────────

-- Socio 0 = usuario actual de prueba
INSERT OR IGNORE INTO Usuarios(dni, nombre, apellidos, telefono, email)
    VALUES('00000000A','Usuario','Actual',600000000,'miusuariogrupo4@test.com');

INSERT OR IGNORE INTO Socios(id_socio, dni, contrasena, estado_pagos)
    VALUES(0,'00000000A','1234xd','Al Corriente');

-- Instalaciones de ejemplo
INSERT OR IGNORE INTO Instalaciones(nombre, tipo, coste_hora)
    VALUES('Pista Tenis 1','Tenis',8.00);
INSERT OR IGNORE INTO Instalaciones(nombre, tipo, coste_hora)
    VALUES('Piscina Cubierta','Natacion',5.50);
INSERT OR IGNORE INTO Instalaciones(nombre, tipo, coste_hora)
    VALUES('Sala Padel A','Padel',10.00);

-- Periodos de inscripcion de ejemplo
INSERT OR IGNORE INTO PeriodosInscripcion
    (nombre, inicio_socios, fin_socios, fin_no_socios)
    VALUES('Torneo Golf Primavera','2026-03-25','2026-04-01','2026-04-08');

INSERT OR IGNORE INTO PeriodosInscripcion
    (nombre, inicio_socios, fin_socios, fin_no_socios)
    VALUES('Clases Natacion Verano','2026-05-01','2026-05-15','2026-05-31');