-- ============================================
-- TABLA: respuestas
-- Respuestas a los tópicos del foro
-- ============================================

CREATE TABLE respuestas (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            mensaje TEXT NOT NULL,
                            topico_id BIGINT NOT NULL,
                            fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            autor_id BIGINT NOT NULL,
                            solucion BOOLEAN NOT NULL DEFAULT FALSE,

                            PRIMARY KEY (id),

    -- Foreign Keys
                            CONSTRAINT fk_respuestas_topico
                                FOREIGN KEY (topico_id) REFERENCES topicos(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_respuestas_autor
                                FOREIGN KEY (autor_id) REFERENCES usuarios(id)
                                    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Índices para mejorar rendimiento
CREATE INDEX idx_respuestas_topico ON respuestas(topico_id);
CREATE INDEX idx_respuestas_autor ON respuestas(autor_id);
CREATE INDEX idx_respuestas_fecha ON respuestas(fecha_creacion DESC);
CREATE INDEX idx_respuestas_solucion ON respuestas(solucion);