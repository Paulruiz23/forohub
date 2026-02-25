package com.paulruiz.forohub.repository;

import com.paulruiz.forohub.model.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operaciones con Respuesta
 */
@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    /**
     * Obtiene todas las respuestas de un tópico específico
     * Ordenadas por fecha de creación (más antiguas primero)
     *
     * @param topicoId ID del tópico
     * @return Lista de respuestas del tópico
     */
    @Query("SELECT r FROM Respuesta r WHERE r.topico.id = :topicoId ORDER BY r.fechaCreacion ASC")
    List<Respuesta> findByTopicoId(@Param("topicoId") Long topicoId);

    /**
     * Cuenta cuántas respuestas tiene un tópico
     *
     * @param topicoId ID del tópico
     * @return Cantidad de respuestas
     */
    @Query("SELECT COUNT(r) FROM Respuesta r WHERE r.topico.id = :topicoId")
    Long countByTopicoId(@Param("topicoId") Long topicoId);

    /**
     * Verifica si un tópico ya tiene una respuesta marcada como solución
     *
     * @param topicoId ID del tópico
     * @return true si ya tiene solución, false si no
     */
    @Query("SELECT COUNT(r) > 0 FROM Respuesta r WHERE r.topico.id = :topicoId AND r.solucion = true")
    boolean existeSolucionEnTopico(@Param("topicoId") Long topicoId);
}