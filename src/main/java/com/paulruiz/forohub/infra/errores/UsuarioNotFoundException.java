package com.paulruiz.forohub.infra.errores;


// Excepción lanzada cuando no se encuentra un usuario en la base de datos

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(String mensaje) {
        super(mensaje);
    }

    public UsuarioNotFoundException(Long id) {
        super("Usuario con ID " + id + " no encontrado");
    }
}