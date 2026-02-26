package com.paulruiz.forohub.model;


// Enum que representa los posibles estados de un tópico

public enum StatusTopico {
    NO_RESPONDIDO,    // Tópico sin respuestas
    NO_SOLUCIONADO,   // Tiene respuestas pero sin solución
    SOLUCIONADO,      // Tiene respuesta marcada como solución
    CERRADO           // Cerrado por moderador/autor
}