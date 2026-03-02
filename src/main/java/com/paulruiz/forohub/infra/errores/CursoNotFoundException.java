package com.paulruiz.forohub.infra.errores;


// Excepción lanzada cuando no se encuentra un curso en la base de datos

public class CursoNotFoundException extends RuntimeException {

    public CursoNotFoundException(String mensaje) {
        super(mensaje);
    }

    public CursoNotFoundException(Long id) {
        super("Curso con ID " + id + " no encontrado");
    }
}
