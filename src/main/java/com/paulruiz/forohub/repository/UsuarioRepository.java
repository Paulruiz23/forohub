package com.paulruiz.forohub.repository;

import com.paulruiz.forohub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

/**
 * Repository para operaciones con Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por email
     * Usado por Spring Security para autenticación
     *
     * @param email Email del usuario
     * @return UserDetails (Usuario implementa esta interfaz)
     */
    UserDetails findByEmail(String email);
}