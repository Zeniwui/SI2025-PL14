-- Tabla base para todos los usuarios
CREATE TABLE IF NOT EXISTS Usuarios (
    dni VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono INT NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- Tabla para los socios
CREATE TABLE IF NOT EXISTS Socios (
    id_socio INTEGER PRIMARY KEY,
    dni VARCHAR(20) NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    estado_pagos VARCHAR(30) DEFAULT 'Al Corriente',
    FOREIGN KEY (dni) REFERENCES Usuarios(dni)
);

CREATE TABLE IF NOT EXISTS PeriodosInscripcion (
    id_periodo INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(50) NOT NULL,        
    inicio_socios DATE NOT NULL,
    fin_socios DATE NOT NULL,
    fin_no_socios DATE NOT NULL
);

-- Tabla para las instalaciones del centro
CREATE TABLE IF NOT EXISTS Instalaciones (
    id_instalacion INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    coste_hora REAL NOT NULL
);

-- Tabla para reservas
CREATE TABLE IF NOT EXISTS Reservas (
    id_reserva INTEGER PRIMARY KEY AUTOINCREMENT,
    id_instalacion INT NOT NULL,
    fecha DATE NOT NULL,                -- Formato (dia-mes-año)
    hora_inicio INT NOT NULL,           
    hora_fin INT NOT NULL,              
    
    id_socio INT,                     
    id_actividad INT,                   
    
    estado_pago VARCHAR(20),
    fecha_reserva DATETIME DEFAULT CURRENT_TIMESTAMP, -- Para el resguardo
    
    FOREIGN KEY (id_instalacion) REFERENCES Instalaciones(id_instalacion),
    FOREIGN KEY (id_socio) REFERENCES Socios(id_socio),
    FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad),
    
    UNIQUE(id_instalacion, fecha, hora_inicio)
);

-- Tabla para las actividades
CREATE TABLE IF NOT EXISTS Actividades (
    id_actividad INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_instalacion INT NOT NULL,
    aforo INT NOT NULL,                
    fecha_inicio DATE,
    fecha_fin DATE,
    precio_socio DECIMAL(6,2),
    precio_no_socio DECIMAL(6,2),
    
    id_periodo INT,
    
    FOREIGN KEY (id_instalacion) REFERENCES Instalaciones(id_instalacion),
    FOREIGN KEY (id_periodo) REFERENCES PeriodosInscripcion(id_periodo)
);

-- Tabla para los horarios de las actividades (para definir el patrón semanal)
CREATE TABLE IF NOT EXISTS Horarios (
    id_horario INTEGER PRIMARY KEY AUTOINCREMENT,
    id_actividad INTEGER NOT NULL,
    dia_semana VARCHAR(15) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    
    -- Relación con la tabla Actividades
    FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad)
        ON DELETE CASCADE,

    -- Validaciones de integridad
    CONSTRAINT check_dias CHECK (dia_semana IN ('Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado', 'Domingo')),
    CONSTRAINT check_horas CHECK (hora_fin > hora_inicio)
);
