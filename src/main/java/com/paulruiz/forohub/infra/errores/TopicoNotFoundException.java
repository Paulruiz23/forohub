package com.paulruiz.forohub.infra.errores;

/**
 * Excepción lanzada cuando no se encuentra un tópico en la base de datos
 */
public class TopicoNotFoundException extends RuntimeException {

    public TopicoNotFoundException(String mensaje) {
        super(mensaje);
    }

    public TopicoNotFoundException(Long id) {
        super("Tópico con ID " + id + " no encontrado");
    }
}