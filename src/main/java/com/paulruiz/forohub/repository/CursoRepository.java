package com.paulruiz.forohub.repository;

import com.paulruiz.forohub.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository para operaciones con Curso

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    // Por ahora solo necesitamos los métodos básicos de JpaRepository
}