package com.paulruiz.forohub.infra.errores;

/**
 * Excepción lanzada cuando se intenta bloquear un usuario ya bloqueado
 */
public class UsuarioBloqueadoException extends RuntimeException {

    public UsuarioBloqueadoException(String mensaje) {
        super(mensaje);
    }

    public UsuarioBloqueadoException() {
        super("El usuario ya está bloqueado");
    }
}