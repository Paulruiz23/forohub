package com.paulruiz.forohub.infra.errores;

/**
 * Excepción lanzada cuando un usuario intenta acceder a un recurso
 * que no le pertenece y no tiene permisos de ADMIN
 */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }

    public AccesoDenegadoException() {
        super("No tienes permisos para realizar esta acción");
    }
}