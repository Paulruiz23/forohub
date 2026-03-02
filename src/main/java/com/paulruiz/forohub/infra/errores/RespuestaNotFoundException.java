package com.paulruiz.forohub.infra.errores;


// Excepción lanzada cuando no se encuentra una respuesta en la base de datos

public class RespuestaNotFoundException extends RuntimeException {

    public RespuestaNotFoundException(String mensaje) {
        super(mensaje);
    }

    public RespuestaNotFoundException(Long id) {
        super("Respuesta con ID " + id + " no encontrada");
    }
}