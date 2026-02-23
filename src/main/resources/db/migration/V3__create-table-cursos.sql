-- ============================================
-- TABLA: cursos
-- Categorías de los tópicos del foro
-- ============================================

CREATE TABLE cursos (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        nombre VARCHAR(100) NOT NULL UNIQUE,
                        categoria VARCHAR(50) NOT NULL,
                        descripcion TEXT,
                        activo BOOLEAN NOT NULL DEFAULT TRUE,

                        PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insertar algunos cursos de ejemplo
INSERT INTO cursos (nombre, categoria, descripcion) VALUES
                                                        ('Spring Boot', 'Backend', 'Desarrollo de APIs REST con Spring Boot'),
                                                        ('Java', 'Backend', 'Programación Java desde cero'),
                                                        ('React', 'Frontend', 'Desarrollo de interfaces con React'),
                                                        ('MySQL', 'Base de Datos', 'Administración de bases de datos MySQL'),
                                                        ('JavaScript', 'Frontend', 'Programación JavaScript moderno');

-- Índice para búsquedas por categoría
CREATE INDEX idx_cursos_categoria ON cursos(categoria);