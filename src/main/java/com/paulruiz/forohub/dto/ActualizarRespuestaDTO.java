package com.paulruiz.forohub.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para actualizar una respuesta existente
 */
public record ActualizarRespuestaDTO(

        @NotBlank(message = "El mensaje no puede estar vacío")
        String mensaje
) {
}