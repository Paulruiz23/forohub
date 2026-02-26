package com.paulruiz.forohub.repository;

import com.paulruiz.forohub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    UserDetails findByEmail(String email);

    // ============================================
    // NUEVO - Para validar email duplicado
    // ============================================

    /*
     Verifica si ya existe un usuario con el email dado

     @param email Email a verificar
     @return true si existe, false si no
     */

    boolean existsByEmail(String email);
}