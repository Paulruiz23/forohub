package com.paulruiz.forohub.infra.errores;

/**
 * Excepción lanzada cuando se intenta crear un tópico duplicado
 * (mismo título y mensaje)
 */
public class TopicoDuplicadoException extends RuntimeException {

    public TopicoDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public TopicoDuplicadoException() {
        super("Ya existe un tópico con el mismo título y mensaje");
    }
}