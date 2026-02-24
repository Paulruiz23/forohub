package com.paulruiz.forohub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para recibir datos al ACTUALIZAR un tópico existente
 * Todos los campos son opcionales (pueden ser null si no se quieren cambiar)
 * pero si se envían, deben cumplir las validaciones
 */
public record ActualizarTopicoDTO(

        @NotBlank(message = "El título no puede estar vacío")
        String titulo,

        @NotBlank(message = "El mensaje no puede estar vacío")
        String mensaje,

        @NotNull(message = "El ID del curso es obligatorio")
        Long cursoId
) {
    // No se permite cambiar el autor del tópico
    // Solo se puede modificar: título, mensaje y curso
}