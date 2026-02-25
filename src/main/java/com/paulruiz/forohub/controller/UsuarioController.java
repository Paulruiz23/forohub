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

/**
 * Controlador para gestionar usuarios
 */
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

    /**
     * POST /usuarios - Registrar un nuevo usuario
     */
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

        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(registroDTO.email())) {
            throw new EmailDuplicadoException("El email ya está registrado");
        }

        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.nombre());
        usuario.setEmail(registroDTO.email());

        // Encriptar la contraseña con BCrypt
        String contrasenaEncriptada = passwordEncoder.encode(registroDTO.contrasena());
        usuario.setContrasena(contrasenaEncriptada);

        usuario.setActivo(true);

        // Asignar el perfil USER por defecto
        Perfil perfilUser = perfilRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException(
                        "Perfil ROLE_USER no encontrado"));

        Set<Perfil> perfiles = new HashSet<>();
        perfiles.add(perfilUser);
        usuario.setPerfiles(perfiles);

        // Guardar en la base de datos
        usuarioRepository.save(usuario);

        // Construir URI del recurso creado
        URI url = uriBuilder.path("/usuarios/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity.created(url)
                .body(new DetalleUsuarioDTO(usuario));
    }

    /**
     * GET /usuarios/{id} - Obtener detalle de un usuario
     */
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
}