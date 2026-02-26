package com.paulruiz.forohub.dto;

import jakarta.validation.constraints.NotBlank;

/*
 DTO para actualizar una respuesta existente
 Solo permite modificar el mensaje, no el tópico ni el autor
 */
public record ActualizarRespuestaDTO(

        @NotBlank(message = "El mensaje no puede estar vacío")
        String mensaje
) {
}