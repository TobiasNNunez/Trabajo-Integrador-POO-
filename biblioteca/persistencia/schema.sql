-- ==============================================================
-- Esquema de Base de Datos para el Sistema de Gestion de Biblioteca
-- ==============================================================

-- 1. Tabla de Libros (Ejemplares)
CREATE TABLE IF NOT EXISTS libros (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL,
    edicion INTEGER NOT NULL,
    editorial TEXT NOT NULL,
    anio INTEGER NOT NULL
);

-- 2. Tabla de Socios (Herencia mediante Single Table)
CREATE TABLE IF NOT EXISTS socios (
    dni INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    dias_prestamo INTEGER NOT NULL,
    tipo TEXT NOT NULL CHECK(tipo IN ('Estudiante', 'Docente')),
    carrera TEXT,   -- Aplica solo a Estudiante
    area TEXT       -- Aplica solo a Docente
);

-- 3. Tabla de Prestamos e Historial
CREATE TABLE IF NOT EXISTS prestamos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_libro INTEGER NOT NULL,
    dni_socio INTEGER NOT NULL,
    fecha_retiro TEXT NOT NULL,       -- Guardada como String "AAAA/MM/DD"
    fecha_devolucion TEXT,            -- Guardada como String "AAAA/MM/DD" o NULL si sigue prestado
    FOREIGN KEY (id_libro) REFERENCES libros(id) ON DELETE RESTRICT,
    FOREIGN KEY (dni_socio) REFERENCES socios(dni) ON DELETE RESTRICT
);

-- 4.
CREATE UNIQUE INDEX IF NOT EXISTS ux_prestamo_activo_por_libro
ON prestamos (id_libro)
WHERE fecha_devolucion IS NULL;