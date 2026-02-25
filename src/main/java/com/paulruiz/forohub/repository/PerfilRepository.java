package com.paulruiz.forohub.repository;

import com.paulruiz.forohub.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operaciones con Perfil
 */
@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    /**
     * Busca un perfil por su nombre
     * @param nombre Nombre del perfil (ej: ROLE_USER)
     * @return Optional con el perfil si existe
     */
    Optional<Perfil> findByNombre(String nombre);
}