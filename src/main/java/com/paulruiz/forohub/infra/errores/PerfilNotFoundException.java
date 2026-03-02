package com.paulruiz.forohub.infra.errores;

/**
 * Excepción lanzada cuando no se encuentra un perfil/rol en la base de datos
 */
public class PerfilNotFoundException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado
     */
    public PerfilNotFoundException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor que genera mensaje automático con el nombre del perfil
     * Usa este método estático en lugar de un constructor
     */
    public static PerfilNotFoundException porNombre(String nombrePerfil) {
        return new PerfilNotFoundException("Perfil " + nombrePerfil + " no encontrado");
    }
}