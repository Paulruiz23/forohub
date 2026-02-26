package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.DetalleUsuarioDTO;
import com.paulruiz.forohub.dto.RegistroUsuarioDTO;
import com.paulruiz.forohub.infra.errores.EmailDuplicadoException;
import com.paulruiz.forohub.infra.errores.EntityNotFoundException;
import com.paulruiz.forohub.model.Perfil;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.repository.PerfilRepository;
import com.paulruiz.forohub.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Operaciones para gestionar usuarios del foro")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema. El usuario se crea con rol USER por defecto. " +
                    "La contraseña se encripta automáticamente con BCrypt. NO requiere autenticación."
    )
    public ResponseEntity<DetalleUsuarioDTO> registrarUsuario(
            @RequestBody @Valid RegistroUsuarioDTO registroDTO,
            UriComponentsBuilder uriBuilder) {

        if (usuarioRepository.existsByEmail(registroDTO.email())) {
            throw new EmailDuplicadoException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.nombre());
        usuario.setEmail(registroDTO.email());

        String contrasenaEncriptada = passwordEncoder.encode(registroDTO.contrasena());
        usuario.setContrasena(contrasenaEncriptada);

        usuario.setActivo(true);

        Perfil perfilUser = perfilRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException(
                        "Perfil ROLE_USER no encontrado"));

        Set<Perfil> perfiles = new HashSet<>();
        perfiles.add(perfilUser);
        usuario.setPerfiles(perfiles);

        usuarioRepository.save(usuario);

        URI url = uriBuilder.path("/usuarios/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity.created(url)
                .body(new DetalleUsuarioDTO(usuario));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Obtener detalle de usuario",
            description = "Retorna la información completa de un usuario específico por su ID. " +
                    "NO retorna la contraseña por razones de seguridad. Requiere autenticación JWT."
    )
    public ResponseEntity<DetalleUsuarioDTO> detalleUsuario(@PathVariable Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + id + " no encontrado"));

        return ResponseEntity.ok(new DetalleUsuarioDTO(usuario));
    }

    // ============================================
    // Bloquear usuario (Soft Delete)
    // ============================================

    /*  DELETE /usuarios/{id} - Bloquear/Desactivar un usuario

      No elimina el usuario de la base de datos, solo lo marca como inactivo.
      Un usuario inactivo NO puede hacer login.
      Los tópicos y respuestas del usuario permanecen en el sistema.
    */

    @DeleteMapping("/{id}")
    @Transactional
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Bloquear usuario",
            description = "Desactiva un usuario del sistema. El usuario bloqueado NO puede hacer login. " +
                    "Sus tópicos y respuestas permanecen visibles pero marcados como de usuario inactivo. " +
                    "NO elimina al usuario de la base de datos (soft delete). " +
                    "Requiere autenticación JWT con rol ADMIN."
    )
    public ResponseEntity<Void> bloquearUsuario(@PathVariable Long id) {

        // Buscar el usuario
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + id + " no encontrado"));

        // Verificar que no esté ya bloqueado
        if (!usuario.getActivo()) {
            throw new RuntimeException("El usuario ya está bloqueado");
        }

        // Marcar como inactivo (bloquear)
        usuario.setActivo(false);

        // No es necesario save() porque @Transactional guarda automáticamente

        return ResponseEntity.noContent().build();
    }

    // ============================================
    // Desbloquear usuario
    // ============================================

    //PUT /usuarios/{id}/desbloquear - Reactivar un usuario bloqueado

    @PutMapping("/{id}/desbloquear")
    @Transactional
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Desbloquear usuario",
            description = "Reactiva un usuario previamente bloqueado. El usuario podrá volver a hacer login. " +
                    "Requiere autenticación JWT con rol ADMIN."
    )
    public ResponseEntity<DetalleUsuarioDTO> desbloquearUsuario(@PathVariable Long id) {

        // Buscar el usuario
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + id + " no encontrado"));

        // Verificar que esté bloqueado
        if (usuario.getActivo()) {
            throw new RuntimeException("El usuario ya está activo");
        }

        // Marcar como activo (desbloquear)
        usuario.setActivo(true);

        return ResponseEntity.ok(new DetalleUsuarioDTO(usuario));
    }
}
