package com.paulruiz.forohub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


 //DTO para crear una respuesta a un tópico

public record RespuestaDTO(

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @NotNull(message = "El ID del tópico es obligatorio")
        Long topicoId,

        @NotNull(message = "El ID del autor es obligatorio")
        Long autorId
) {
}