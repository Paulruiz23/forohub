package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.DetalleUsuarioDTO;
import com.paulruiz.forohub.dto.RegistroUsuarioDTO;
import com.paulruiz.forohub.infra.errores.EmailDuplicadoException;
import com.paulruiz.forohub.infra.errores.EntityNotFoundException;
import com.paulruiz.forohub.model.Perfil;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.repository.PerfilRepository;
import com.paulruiz.forohub.repository.UsuarioRepository;
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
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    public ResponseEntity<DetalleUsuarioDTO> registrarUsuario(
            @RequestBody @Valid RegistroUsuarioDTO registroDTO,
            UriComponentsBuilder uriBuilder) {

        // ============================================
        // 1. VALIDACIÓN: Verificar que el email no exista
        // ============================================
        if (usuarioRepository.existsByEmail(registroDTO.email())) {
            // Lanzar excepción personalizada (será capturada por TratadorDeErrores)
            throw new EmailDuplicadoException("El email ya está registrado");
        }

        // ============================================
        // 2. Crear el usuario
        // ============================================
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.nombre());
        usuario.setEmail(registroDTO.email());

        // Encriptar la contraseña con BCrypt
        String contrasenaEncriptada = passwordEncoder.encode(registroDTO.contrasena());
        usuario.setContrasena(contrasenaEncriptada);

        usuario.setActivo(true);

        // ============================================
        // 3. Asignar el perfil USER por defecto
        // ============================================
        Perfil perfilUser = perfilRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException(
                        "Perfil ROLE_USER no encontrado"));

        Set<Perfil> perfiles = new HashSet<>();
        perfiles.add(perfilUser);
        usuario.setPerfiles(perfiles);

        // ============================================
        // 4. Guardar en la base de datos
        // ============================================
        usuarioRepository.save(usuario);

        // ============================================
        // 5. Construir URI del recurso creado
        // ============================================
        URI url = uriBuilder.path("/usuarios/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        // ============================================
        // 6. Retornar respuesta 201 Created
        // ============================================
        return ResponseEntity.created(url)
                .body(new DetalleUsuarioDTO(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleUsuarioDTO> detalleUsuario(@PathVariable Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + id + " no encontrado"));

        return ResponseEntity.ok(new DetalleUsuarioDTO(usuario));
    }
}