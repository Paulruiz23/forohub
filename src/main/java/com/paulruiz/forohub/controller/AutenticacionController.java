package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.DatosAutenticacion;
import com.paulruiz.forohub.dto.DatosJWT;
import com.paulruiz.forohub.infra.security.TokenService;
import com.paulruiz.forohub.model.Usuario;
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

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;  // NUEVO

    @PostMapping
    public ResponseEntity<DatosJWT> autenticarUsuario(
            @RequestBody @Valid DatosAutenticacion datosAutenticacion) {

        // Crear token de autenticación
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                datosAutenticacion.email(),
                datosAutenticacion.contrasena()
        );

        // Autenticar el usuario
        Authentication usuarioAutenticado = authenticationManager.authenticate(authToken);

        // Obtener datos del usuario autenticado
        Usuario usuario = (Usuario) usuarioAutenticado.getPrincipal();

        // ============================================
        // NUEVO - Generar token JWT
        // ============================================
        String tokenJWT = tokenService.generarToken(usuario);

        // ============================================
        // Retornar el token en la respuesta
        // ============================================
        return ResponseEntity.ok(new DatosJWT(tokenJWT));
    }
}