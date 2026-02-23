CREATE TABLE topicos (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         titulo VARCHAR(200) NOT NULL,
                         mensaje TEXT NOT NULL,
                         fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         status VARCHAR(50) NOT NULL DEFAULT 'NO_RESPONDIDO',
                         autor_id BIGINT NOT NULL,
                         curso_id BIGINT NOT NULL,

                         PRIMARY KEY (id),

                         CONSTRAINT fk_topicos_autor
                             FOREIGN KEY (autor_id) REFERENCES usuarios(id)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_topicos_curso
                             FOREIGN KEY (curso_id) REFERENCES cursos(id)
                                 ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_topicos_titulo ON topicos(titulo);
CREATE INDEX idx_topicos_autor ON topicos(autor_id);
CREATE INDEX idx_topicos_curso ON topicos(curso_id);
CREATE INDEX idx_topicos_fecha ON topicos(fecha_creacion);
CREATE INDEX idx_topicos_status ON topicos(status);