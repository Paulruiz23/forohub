-- TABLA: perfiles
-- Define los roles/permisos de usuarios

CREATE TABLE perfiles (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          nombre VARCHAR(50) NOT NULL UNIQUE,
                          descripcion VARCHAR(255),

                          PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insertar perfiles por defecto
INSERT INTO perfiles (nombre, descripcion) VALUES
                                               ('ROLE_USER', 'Usuario normal del foro'),
                                               ('ROLE_ADMIN', 'Administrador con todos los permisos'),
                                               ('ROLE_MODERADOR', 'Moderador que puede editar/eliminar tópicos');

-- ============================================
-- TABLA INTERMEDIA: usuarios_perfiles
-- Relación Many-to-Many entre usuarios y perfiles
-- ============================================

CREATE TABLE usuarios_perfiles (
                                   usuario_id BIGINT NOT NULL,
                                   perfil_id BIGINT NOT NULL,

                                   PRIMARY KEY (usuario_id, perfil_id),

                                   CONSTRAINT fk_usuarios_perfiles_usuario
                                       FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_usuarios_perfiles_perfil
                                       FOREIGN KEY (perfil_id) REFERENCES perfiles(id)
                                           ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;