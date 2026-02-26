package com.paulruiz.forohub.dto;

import com.paulruiz.forohub.model.StatusTopico;
import com.paulruiz.forohub.model.Topico;

import java.time.LocalDateTime;

/*
 DTO para RETORNAR datos de un tópico
 Se usa en las respuestas de la API
 */
public record DetalleTopicoDTO(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        StatusTopico status,
        String nombreAutor,
        String emailAutor,
        String nombreCurso,
        String categoriaCurso
) {
    /*
     Constructor que convierte una entidad Topico en DTO

     @param topico Entidad Topico de la base de datos
     */
    public DetalleTopicoDTO(Topico topico) {
        this(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor().getNombre(),
                topico.getAutor().getEmail(),
                topico.getCurso().getNombre(),
                topico.getCurso().getCategoria()
        );
    }
}