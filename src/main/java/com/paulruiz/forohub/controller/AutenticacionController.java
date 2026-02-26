package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.DatosAutenticacion;
import com.paulruiz.forohub.dto.DatosJWT;
import com.paulruiz.forohub.infra.security.TokenService;
import com.paulruiz.forohub.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// Controlador para gestionar la autenticación de usuarios

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticación", description = "Endpoint para autenticación de usuarios")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    // ============================================
    // POST /login - Autenticar usuario
    // ============================================

    /*
     POST /login - Autenticar usuario y generar token JWT

     Recibe email y contraseña, valida credenciales con Spring Security
     y retorna un token JWT válido por 1 hora.
     */

    @PostMapping
    @Operation(summary = "Autenticar usuario",
            description = "Autentica un usuario con email y contraseña, retorna un token JWT válido por 1 hora.")
    public ResponseEntity<DatosJWT> autenticarUsuario(
            @RequestBody @Valid DatosAutenticacion datosAutenticacion) {

        // Crear token de autenticación con email y contraseña
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                datosAutenticacion.email(),
                datosAutenticacion.contrasena()
        );

        // Autenticar el usuario (Spring Security valida credenciales automáticamente)
        Authentication usuarioAutenticado = authenticationManager.authenticate(authToken);

        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) usuarioAutenticado.getPrincipal();

        // Generar token JWT
        String tokenJWT = tokenService.generarToken(usuario);

        // Retornar token en la respuesta
        return ResponseEntity.ok(new DatosJWT(tokenJWT));
    }
}