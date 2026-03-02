package com.paulruiz.forohub.infra.errores;

/**
 * Excepción lanzada cuando se intenta desbloquear un usuario ya activo
 */
public class UsuarioActivoException extends RuntimeException {

    public UsuarioActivoException(String mensaje) {
        super(mensaje);
    }

    public UsuarioActivoException() {
        super("El usuario ya está activo");
    }
}