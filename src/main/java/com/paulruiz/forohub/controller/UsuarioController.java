package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.DetalleUsuarioDTO;
import com.paulruiz.forohub.dto.RegistroUsuarioDTO;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


// Controlador para gestionar usuarios

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Operaciones para gestionar usuarios del foro")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ============================================
    // POST - Registrar usuario
    // ============================================

    /*
      POST /usuarios - Registrar un nuevo usuario

      Endpoint público para registro de usuarios.
     */
    @PostMapping
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema. El usuario se crea con rol USER por defecto. " +
                    "La contraseña se encripta automáticamente con BCrypt. NO requiere autenticación."
    )
    public ResponseEntity<DetalleUsuarioDTO> registrarUsuario(
            @RequestBody @Valid RegistroUsuarioDTO registroDTO,
            UriComponentsBuilder uriBuilder) {

        // Delegar registro al servicio
        Usuario usuario = usuarioService.registrarUsuario(registroDTO);

        // Construir URI del recurso creado
        URI url = uriBuilder.path("/usuarios/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity.created(url)
                .body(new DetalleUsuarioDTO(usuario));
    }

    // ============================================
    // GET - Detalle de usuario
    // ============================================

    /*
      GET /usuarios/{id} - Obtener detalle de un usuario

      Retorna información del usuario sin la contraseña.
     */
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Obtener detalle de usuario",
            description = "Retorna la información completa de un usuario específico por su ID. " +
                    "NO retorna la contraseña por razones de seguridad. Requiere autenticación JWT."
    )
    public ResponseEntity<DetalleUsuarioDTO> detalleUsuario(@PathVariable Long id) {

        // Obtener usuario del servicio
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);

        return ResponseEntity.ok(new DetalleUsuarioDTO(usuario));
    }

    // ============================================
    // DELETE - Bloquear usuario (Solo ADMIN)
    // ============================================

    /*
      DELETE /usuarios/{id} - Bloquear/Desactivar un usuario

      Soft delete: marca el usuario como inactivo sin eliminarlo.
      Solo accesible por usuarios con rol ADMIN.
     */
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Bloquear usuario",
            description = "Desactiva un usuario del sistema. El usuario bloqueado NO puede hacer login. " +
                    "Sus tópicos y respuestas permanecen visibles pero marcados como de usuario inactivo. " +
                    "NO elimina al usuario de la base de datos (soft delete). " +
                    "Requiere autenticación JWT con rol ADMIN."
    )
    public ResponseEntity<Void> bloquearUsuario(@PathVariable Long id) {

        // Delegar bloqueo al servicio
        usuarioService.bloquearUsuario(id);

        return ResponseEntity.noContent().build();
    }

    // ============================================
    // PUT - Desbloquear usuario (Solo ADMIN)
    // ============================================

    /*
      PUT /usuarios/{id}/desbloquear - Reactivar un usuario bloqueado

      Reactiva un usuario previamente bloqueado.
      Solo accesible por usuarios con rol ADMIN.
     */
    @PutMapping("/{id}/desbloquear")
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Desbloquear usuario",
            description = "Reactiva un usuario previamente bloqueado. El usuario podrá volver a hacer login. " +
                    "Requiere autenticación JWT con rol ADMIN."
    )
    public ResponseEntity<DetalleUsuarioDTO> desbloquearUsuario(@PathVariable Long id) {

        // Delegar desbloqueo al servicio
        Usuario usuario = usuarioService.desbloquearUsuario(id);

        return ResponseEntity.ok(new DetalleUsuarioDTO(usuario));
    }
}