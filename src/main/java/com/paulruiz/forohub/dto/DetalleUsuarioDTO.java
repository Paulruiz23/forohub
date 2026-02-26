package com.paulruiz.forohub.dto;

import com.paulruiz.forohub.model.Usuario;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/*
 DTO para retornar datos de un usuario
 NO incluye la contraseña por seguridad
 */

public record DetalleUsuarioDTO(
        Long id,
        String nombre,
        String email,
        Boolean activo,
        LocalDateTime fechaCreacion,
        Set<String> perfiles
) {
    /*
     Constructor que convierte una entidad Usuario en DTO
     @param usuario Entidad Usuario de la base de datos
     */

    public DetalleUsuarioDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getActivo(),
                usuario.getFechaCreacion(),
                usuario.getPerfiles().stream()
                        .map(perfil -> perfil.getNombre())
                        .collect(Collectors.toSet())
        );
    }
}