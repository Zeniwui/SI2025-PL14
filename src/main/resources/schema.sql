DROP TABLE IF EXISTS PagosNoSocios;
DROP TABLE IF EXISTS Pagos;
DROP TABLE IF EXISTS Horarios;
DROP TABLE IF EXISTS Reservas;
DROP TABLE IF EXISTS Inscripciones;
DROP TABLE IF EXISTS Actividades;
DROP TABLE IF EXISTS Instalaciones;
DROP TABLE IF EXISTS PeriodosInscripcion;
DROP TABLE IF EXISTS NoSocios;
DROP TABLE IF EXISTS Socios;
DROP TABLE IF EXISTS Usuarios;

CREATE TABLE IF NOT EXISTS Usuarios (
    dni       VARCHAR(20)  PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono  INT          NOT NULL,
    email     VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Socios (
    id_socio     INTEGER      PRIMARY KEY,
    dni          VARCHAR(20)  NOT NULL,
    contrasena   VARCHAR(100) NOT NULL,
    estado_pagos VARCHAR(30)  DEFAULT 'Al Corriente',
    FOREIGN KEY (dni) REFERENCES Usuarios(dni)
);

CREATE TABLE IF NOT EXISTS NoSocios (
    id_no_socio INTEGER PRIMARY KEY AUTOINCREMENT,
    dni         VARCHAR(20) NOT NULL UNIQUE,
    FOREIGN KEY (dni) REFERENCES Usuarios(dni)
);

CREATE TABLE IF NOT EXISTS PeriodosInscripcion (
    id_periodo    INTEGER      PRIMARY KEY AUTOINCREMENT,
    nombre        VARCHAR(50)  NOT NULL,
    descripcion   TEXT,
    inicio_socios DATE         NOT NULL,
    fin_socios    DATE         NOT NULL,
    fin_no_socios DATE         NOT NULL,
    CONSTRAINT chk_socios    CHECK (inicio_socios <= fin_socios),
    CONSTRAINT chk_no_socios CHECK (fin_socios    <= fin_no_socios)
);

CREATE TABLE IF NOT EXISTS Instalaciones (
    id_instalacion INTEGER     PRIMARY KEY AUTOINCREMENT,
    nombre         VARCHAR(50) NOT NULL,
    tipo           VARCHAR(20) NOT NULL,
    coste_hora     REAL        NOT NULL
);

CREATE TABLE IF NOT EXISTS Actividades (
    id_actividad     INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre           VARCHAR(100) NOT NULL,
    descripcion      TEXT,
    id_instalacion   INT          NOT NULL,
    aforo            INT          NOT NULL,
    fecha_inicio     DATE,
    fecha_fin        DATE,
    precio_socio     DECIMAL(6,2),
    precio_no_socio  DECIMAL(6,2),
    es_evento_social INTEGER NOT NULL DEFAULT 0,
    id_periodo       INT,
    FOREIGN KEY (id_instalacion) REFERENCES Instalaciones(id_instalacion),
    FOREIGN KEY (id_periodo)     REFERENCES PeriodosInscripcion(id_periodo)
);

CREATE TABLE IF NOT EXISTS Reservas (
    id_reserva     INTEGER PRIMARY KEY AUTOINCREMENT,
    id_instalacion INTEGER      NOT NULL,
    fecha          DATE         NOT NULL,
    hora_inicio    TIME         NOT NULL,
    hora_fin       TIME         NOT NULL,
    id_socio       INTEGER      NULL,
    id_actividad   INTEGER      NULL,
    coste_reserva  DECIMAL(10,2) DEFAULT 0,
    estado_pago    VARCHAR(20)   DEFAULT 'Pendiente',
    metodo_pago    VARCHAR(20),
    fecha_creacion DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_instalacion) REFERENCES Instalaciones(id_instalacion),
    FOREIGN KEY (id_socio)       REFERENCES Socios(id_socio)         ON DELETE SET NULL,
    FOREIGN KEY (id_actividad)   REFERENCES Actividades(id_actividad) ON DELETE CASCADE,
    CONSTRAINT chk_horas  CHECK (hora_fin > hora_inicio),
    CONSTRAINT chk_origen CHECK (
        (id_socio IS NOT NULL AND id_actividad IS NULL) OR
        (id_socio IS NULL     AND id_actividad IS NOT NULL)
    )
);

CREATE TABLE IF NOT EXISTS Horarios (
    id_horario   INTEGER     PRIMARY KEY AUTOINCREMENT,
    id_actividad INTEGER     NOT NULL,
    dia_semana   VARCHAR(15) NOT NULL,
    hora_inicio  TIME        NOT NULL,
    hora_fin     TIME        NOT NULL,
    FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Inscripciones (
    id_inscripcion INTEGER PRIMARY KEY AUTOINCREMENT,
    id_socio       INTEGER NULL,
    id_no_socio    INTEGER NULL,
    id_actividad   INTEGER NOT NULL,
    fecha_inscripcion DATE DEFAULT CURRENT_DATE,
    precio_inscripcion DECIMAL(10,2),
    FOREIGN KEY (id_socio) REFERENCES Socios(id_socio),
    FOREIGN KEY (id_no_socio) REFERENCES NoSocios(id_no_socio),
    FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad),
    CONSTRAINT chk_inscrito CHECK (
        (id_socio IS NOT NULL AND id_no_socio IS NULL) OR 
        (id_socio IS NULL AND id_no_socio IS NOT NULL)
    )
);

CREATE TABLE IF NOT EXISTS Pagos (
    id_pago          INTEGER PRIMARY KEY AUTOINCREMENT,
    id_socio         INTEGER       NOT NULL,
    monto            DECIMAL(10,2) NOT NULL,
    fecha_pago       DATETIME      DEFAULT CURRENT_TIMESTAMP,
    metodo_pago      VARCHAR(20)   NOT NULL,
    estado_pago      VARCHAR(20)   DEFAULT 'Pendiente',
    concepto         VARCHAR(100),
    id_reserva       INTEGER       NULL,
    id_inscripcion   INTEGER       NULL,
    FOREIGN KEY (id_socio)       REFERENCES Socios(id_socio),
    FOREIGN KEY (id_reserva)     REFERENCES Reservas(id_reserva) ON DELETE SET NULL,
    FOREIGN KEY (id_inscripcion) REFERENCES Inscripciones(id_inscripcion) ON DELETE SET NULL,
    CONSTRAINT chk_origen_pago CHECK (
        (id_reserva IS NOT NULL AND id_inscripcion IS NULL) OR
        (id_reserva IS NULL AND id_inscripcion IS NOT NULL) OR
        (id_reserva IS NULL AND id_inscripcion IS NULL)
    )
);

CREATE TABLE IF NOT EXISTS PagosNoSocios (
    id_pago_no_socio INTEGER PRIMARY KEY AUTOINCREMENT,
    id_no_socio      INTEGER       NOT NULL,
    monto            DECIMAL(10,2) NOT NULL,
    fecha_pago       DATETIME      DEFAULT CURRENT_TIMESTAMP,
    metodo_pago      VARCHAR(20)   NOT NULL,
    estado_pago      VARCHAR(20)   DEFAULT 'Pagado',
    concepto         VARCHAR(100),
    id_inscripcion   INTEGER       NOT NULL,
    FOREIGN KEY (id_no_socio)    REFERENCES NoSocios(id_no_socio),
    FOREIGN KEY (id_inscripcion) REFERENCES Inscripciones(id_inscripcion) ON DELETE SET NULL
);