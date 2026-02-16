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

-- Tabla para reservas (tanto reservas de socios, como las reservas de instalaciones debidas a actividades)
CREATE TABLE IF NOT EXISTS Reservas (

    id_reserva INTEGER PRIMARY KEY AUTOINCREMENT,
    id_instalacion INTEGER NOT NULL,
    fecha DATE NOT NULL,				-- Formato (dd/mm/aaaa)
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,

    -- Si la reserva es para un socio, se llena id_socio
    -- Si es reserva es por una actividad, se llena id_actividad
    id_socio INTEGER NULL,
    id_actividad INTEGER NULL,

    coste_reserva DECIMAL(10, 2) DEFAULT 0,

    -- Estado del pago: 'Pendiente', 'Pagado', 'Anulado', 'Cuota'
    estado_pago VARCHAR(20) DEFAULT 'Pendiente',
    
    -- Forma de pago: 'Tarjeta', 'Efectivo', 'Cuota_Mensual'
    metodo_pago VARCHAR(20),

    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP, -- Para el resguardo

    FOREIGN KEY (id_instalacion) REFERENCES Instalaciones(id_instalacion),
    
    -- Si eliminamos un socio, mantenemos el histórico poniendo NULL
    FOREIGN KEY (id_socio) REFERENCES Socios(id_socio) ON DELETE SET NULL,
    
    -- Si eliminamos una actividad, borramos sus reservas futuras automáticamente.
    FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad) ON DELETE CASCADE,

    -- No se puede terminar antes de empezar
    CONSTRAINT chk_horas_validas CHECK (hora_fin > hora_inicio),

    -- Garantiza que una reserva no pueda pertenecer a un socio y a una actividad a la vez.
    CONSTRAINT chk_origen_unico CHECK (
        (id_socio IS NOT NULL AND id_actividad IS NULL) OR  -- Caso: Reserva de Socio
        (id_socio IS NULL AND id_actividad IS NOT NULL)	    -- Caso: Clase de Actividad
    )
);

CREATE TABLE IF NOT EXISTS Horarios (
    id_horario INTEGER PRIMARY KEY AUTOINCREMENT,
    id_actividad INTEGER NOT NULL,
    dia_semana VARCHAR(15) NOT NULL, -- 'Lunes', 'Martes'...
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    
    FOREIGN KEY (id_actividad) REFERENCES Actividades(id_actividad) ON DELETE CASCADE
);

