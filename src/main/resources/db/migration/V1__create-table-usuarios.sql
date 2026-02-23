-- TABLA: usuarios
-- Almacena los datos de los usuarios del foro

CREATE TABLE usuarios (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          nombre VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          contrasena VARCHAR(255) NOT NULL,
                          activo BOOLEAN NOT NULL DEFAULT TRUE,
                          fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          PRIMARY KEY (id),
                          CONSTRAINT uk_usuarios_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Índice para búsquedas por email (usado en login)
CREATE INDEX idx_usuarios_email ON usuarios(email);