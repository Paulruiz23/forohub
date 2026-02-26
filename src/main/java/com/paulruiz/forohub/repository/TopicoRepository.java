package com.paulruiz.forohub.repository;

import com.paulruiz.forohub.model.Topico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long> {

    // Métodos anteriores
    boolean existsByTituloAndMensaje(String titulo, String mensaje);

    Optional<Topico> findByTituloAndMensaje(String titulo, String mensaje);

    // ============================================
    // NUEVO - Para validar duplicados al actualizar
    // ============================================

    /*
     Verifica si existe otro tópico con el mismo título y mensaje
     excluyendo el tópico que se está actualizando

     @param titulo Título del tópico
     @param mensaje Mensaje del tópico
     @param id ID del tópico que se está actualizando (para excluirlo)
     @return true si existe otro tópico duplicado
     */

    boolean existsByTituloAndMensajeAndIdNot(String titulo, String mensaje, Long id);

    Page<Topico> findByCursoNombreContainingIgnoreCase(
            String nombreCurso,
            Pageable paginacion);

    /*
     @param anio Año de creación
     @param paginacion Configuración de paginación
     @return Página de tópicos del año especificado
     */

    @Query("SELECT t FROM Topico t WHERE YEAR(t.fechaCreacion) = :anio")
    Page<Topico> findByAnio(
            @Param("anio") Integer anio,
            Pageable paginacion);

    /*
     @param nombreCurso Nombre del curso
     @param anio Año de creación
     @param paginacion Configuración de paginación
     @return Página de tópicos filtrados
     */

    @Query("SELECT t FROM Topico t " +
            "WHERE LOWER(t.curso.nombre) LIKE LOWER(CONCAT('%', :nombreCurso, '%')) " +
            "AND YEAR(t.fechaCreacion) = :anio")
    Page<Topico> findByCursoAndAnio(
            @Param("nombreCurso") String nombreCurso,
            @Param("anio") Integer anio,
            Pageable paginacion);





}