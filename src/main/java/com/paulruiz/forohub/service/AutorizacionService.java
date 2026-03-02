package com.paulruiz.forohub.service;

import com.paulruiz.forohub.infra.errores.AccesoDenegadoException;
import com.paulruiz.forohub.model.Respuesta;
import com.paulruiz.forohub.model.Topico;
import com.paulruiz.forohub.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// Servicio para validar permisos de autorización
// Verifica si el usuario actual puede realizar acciones sobre recursos

@Service
public class AutorizacionService {

    // ============================================
    // Obtener usuario autenticado actual
    // ============================================

    // Obtiene el usuario actualmente autenticado desde el contexto de seguridad

    public Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    // ============================================
    // Verificar si es ADMIN
    // ============================================

    // Verifica si el usuario actual tiene rol ADMIN

    public boolean esAdmin() {
        return obtenerUsuarioAutenticado().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equals("ROLE_ADMIN"));
    }

    // ============================================
    // Validar permisos sobre Tópico
    // ============================================

    /*
      Valida que el usuario actual pueda modificar el tópico
      Puede modificar si:
      - Es el autor del tópico, O
      - Tiene rol ADMIN

      @param topico Tópico a validar
      @throws AccesoDenegadoException si no tiene permisos
     */
    public void validarPermisoParaModificarTopico(Topico topico) {
        Usuario usuarioActual = obtenerUsuarioAutenticado();

        // Si es ADMIN, puede hacer cualquier cosa
        if (esAdmin()) {
            return;
        }

        // Si es el autor, puede modificar
        if (topico.getAutor().getId().equals(usuarioActual.getId())) {
            return;
        }

        // No es ni ADMIN ni autor → Acceso denegado
        throw new AccesoDenegadoException(
                "Solo el autor del tópico o un administrador pueden realizar esta acción"
        );
    }

    // ============================================
    // Validar permisos sobre Respuesta
    // ============================================

    /*
      Valida que el usuario actual pueda modificar la respuesta
      Puede modificar si:
      - Es el autor de la respuesta, O
      - Tiene rol ADMIN

      @param respuesta Respuesta a validar
      @throws AccesoDenegadoException si no tiene permisos
     */
    public void validarPermisoParaModificarRespuesta(Respuesta respuesta) {
        Usuario usuarioActual = obtenerUsuarioAutenticado();

        // Si es ADMIN, puede hacer cualquier cosa
        if (esAdmin()) {
            return;
        }

        // Si es el autor, puede modificar
        if (respuesta.getAutor().getId().equals(usuarioActual.getId())) {
            return;
        }

        // No es ni ADMIN ni autor → Acceso denegado
        throw new AccesoDenegadoException(
                "Solo el autor de la respuesta o un administrador pueden realizar esta acción"
        );
    }

    // ============================================
    // Validar permisos para marcar solución
    // ============================================

    /*
      Valida que el usuario actual pueda marcar una respuesta como solución
      Puede marcar si:
      - Es el autor del tópico (el que hizo la pregunta), O
      - Tiene rol ADMIN

      @param respuesta Respuesta a marcar como solución
      @throws AccesoDenegadoException si no tiene permisos
     */
    public void validarPermisoParaMarcarSolucion(Respuesta respuesta) {
        Usuario usuarioActual = obtenerUsuarioAutenticado();

        // Si es ADMIN, puede hacer cualquier cosa
        if (esAdmin()) {
            return;
        }

        // Si es el autor del TÓPICO (no de la respuesta), puede marcar solución
        if (respuesta.getTopico().getAutor().getId().equals(usuarioActual.getId())) {
            return;
        }

        // No es ni ADMIN ni autor del tópico → Acceso denegado
        throw new AccesoDenegadoException(
                "Solo el autor del tópico o un administrador pueden marcar una respuesta como solución"
        );
    }
}