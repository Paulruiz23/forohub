package com.paulruiz.forohub.service;

import com.paulruiz.forohub.dto.RegistroUsuarioDTO;
import com.paulruiz.forohub.infra.errores.EmailDuplicadoException;
import com.paulruiz.forohub.infra.errores.PerfilNotFoundException;
import com.paulruiz.forohub.infra.errores.UsuarioActivoException;
import com.paulruiz.forohub.infra.errores.UsuarioBloqueadoException;
import com.paulruiz.forohub.infra.errores.UsuarioNotFoundException;
import com.paulruiz.forohub.model.Perfil;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.repository.PerfilRepository;
import com.paulruiz.forohub.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio que contiene la lógica de negocio para Usuarios
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============================================
    // Registrar usuario
    // ============================================

    /**
     * Registra un nuevo usuario en el sistema
     * Encripta la contraseña y asigna rol USER por defecto
     *
     * @param registroDTO Datos del usuario
     * @return Usuario creado
     * @throws EmailDuplicadoException si el email ya existe
     * @throws PerfilNotFoundException si no existe el rol USER
     */
    @Transactional
    public Usuario registrarUsuario(RegistroUsuarioDTO registroDTO) {
        // Validar email duplicado
        if (usuarioRepository.existsByEmail(registroDTO.email())) {
            throw new EmailDuplicadoException("El email ya está registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.nombre());
        usuario.setEmail(registroDTO.email());

        // Encriptar contraseña
        String contrasenaEncriptada = passwordEncoder.encode(registroDTO.contrasena());
        usuario.setContrasena(contrasenaEncriptada);

        usuario.setActivo(true);

        // ============================================
        // Asignar perfil USER - CORREGIDO
        // ============================================
        Perfil perfilUser = perfilRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> PerfilNotFoundException.porNombre("ROLE_USER"));

        Set<Perfil> perfiles = new HashSet<>();
        perfiles.add(perfilUser);
        usuario.setPerfiles(perfiles);

        // Guardar y retornar
        return usuarioRepository.save(usuario);
    }

    // ============================================
    // Obtener usuario por ID
    // ============================================

    /**
     * Busca un usuario por su ID
     *
     * @param id ID del usuario
     * @return Usuario encontrado
     * @throws UsuarioNotFoundException si no existe
     */
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    // ============================================
    // Bloquear usuario
    // ============================================

    /**
     * Bloquea un usuario (soft delete)
     * El usuario no puede hacer login pero sus datos permanecen
     *
     * @param id ID del usuario a bloquear
     * @throws UsuarioNotFoundException si no existe
     * @throws UsuarioBloqueadoException si ya está bloqueado
     */
    @Transactional
    public void bloquearUsuario(Long id) {
        Usuario usuario = obtenerUsuarioPorId(id);

        // Validar que no esté ya bloqueado
        if (!usuario.getActivo()) {
            throw new UsuarioBloqueadoException();
        }

        // Marcar como inactivo
        usuario.setActivo(false);
    }

    // ============================================
    // Desbloquear usuario
    // ============================================

    /**
     * Desbloquea un usuario previamente bloqueado
     *
     * @param id ID del usuario a desbloquear
     * @return Usuario desbloqueado
     * @throws UsuarioNotFoundException si no existe
     * @throws UsuarioActivoException si ya está activo
     */
    @Transactional
    public Usuario desbloquearUsuario(Long id) {
        Usuario usuario = obtenerUsuarioPorId(id);

        // Validar que esté bloqueado
        if (usuario.getActivo()) {
            throw new UsuarioActivoException();
        }

        // Marcar como activo
        usuario.setActivo(true);

        return usuario;
    }
}