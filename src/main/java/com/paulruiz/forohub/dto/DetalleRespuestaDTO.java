package com.paulruiz.forohub.dto;

import com.paulruiz.forohub.model.Respuesta;

import java.time.LocalDateTime;

/**
 * DTO para retornar datos de una respuesta
 */
public record DetalleRespuestaDTO(
        Long id,
        String mensaje,
        LocalDateTime fechaCreacion,
        Boolean solucion,
        Long topicoId,
        String tituloTopico,
        String nombreAutor,
        String emailAutor
) {
    /**
     * Constructor que convierte una entidad Respuesta en DTO
     * @param respuesta Entidad Respuesta de la base de datos
     */
    public DetalleRespuestaDTO(Respuesta respuesta) {
        this(
                respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getFechaCreacion(),
                respuesta.getSolucion(),
                respuesta.getTopico().getId(),
                respuesta.getTopico().getTitulo(),
                respuesta.getAutor().getNombre(),
                respuesta.getAutor().getEmail()
        );
    }
}