package com.paulruiz.forohub.infra.errores;


// Excepción lanzada cuando se intenta marcar una segunda solución en un tópico que ya tiene una

public class SolucionDuplicadaException extends RuntimeException {

    public SolucionDuplicadaException(String mensaje) {
        super(mensaje);
    }

    public SolucionDuplicadaException() {
        super("Este tópico ya tiene una respuesta marcada como solución");
    }
}