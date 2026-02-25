package com.paulruiz.forohub.dto;

/**
 * DTO para retornar el token JWT después del login
 */
public record DatosJWT(
        String token
) {
}