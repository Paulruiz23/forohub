package com.paulruiz.forohub.infra.errores;

/**
 * Excepción lanzada cuando no se encuentra una entidad en la BD
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String mensaje) {
        super(mensaje);
    }
}