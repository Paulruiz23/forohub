package com.paulruiz.forohub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para recibir datos al CREAR un nuevo tópico
 * Validaciones:
 * - @NotBlank: El campo no puede estar vacío
 * - @NotNull: El campo no puede ser null
 */
public record TopicoDTO(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @NotNull(message = "El ID del autor es obligatorio")
        Long autorId,

        @NotNull(message = "El ID del curso es obligatorio")
        Long cursoId
) {
    // Record automáticamente crea:
    // - Constructor
    // - Getters (titulo(), mensaje(), etc.)
    // - equals(), hashCode(), toString()
}