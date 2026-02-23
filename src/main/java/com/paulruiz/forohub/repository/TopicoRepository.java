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

    // Método del Paso 3
    boolean existsByTituloAndMensaje(String titulo, String mensaje);

    Optional<Topico> findByTituloAndMensaje(String titulo, String mensaje);

    // ============================================
    // NUEVOS MÉTODOS - Paso 4 (Opcionales)
    // ============================================

    /**
     * Buscar tópicos por nombre del curso
     * Ejemplo: /topicos/buscar?curso=Spring Boot
     *
     * @param nombreCurso Nombre del curso (case-insensitive)
     * @param paginacion Configuración de paginación
     * @return Página de tópicos filtrados
     */
    Page<Topico> findByCursoNombreContainingIgnoreCase(
            String nombreCurso,
            Pageable paginacion);

    /**
     * Buscar tópicos por año de creación
     * Ejemplo: /topicos/buscar?anio=2024
     *
     * @param anio Año de creación
     * @param paginacion Configuración de paginación
     * @return Página de tópicos del año especificado
     */
    @Query("SELECT t FROM Topico t WHERE YEAR(t.fechaCreacion) = :anio")
    Page<Topico> findByAnio(
            @Param("anio") Integer anio,
            Pageable paginacion);

    /**
     * Buscar tópicos por curso Y año
     * Ejemplo: /topicos/buscar?curso=Spring Boot&anio=2024
     *
     * @param nombreCurso Nombre del curso
     * @param anio Año de creación
     * @param paginacion Configuración de paginación
     * @return Página de tópicos filtrados
     */
    @Query("SELECT t FROM Topico t " +
            "WHERE LOWER(t.curso.nombre) LIKE LOWER(CONCAT('%', :nombreCurso, '%')) " +
            "AND YEAR(t.fechaCreacion) = :anio")
    Page<Topico> findByCursoAndAnio(
            @Param("nombreCurso") String nombreCurso,
            @Param("anio") Integer anio,
            Pageable paginacion);
}