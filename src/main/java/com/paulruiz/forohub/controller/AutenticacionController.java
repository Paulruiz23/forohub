package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.DatosAutenticacion;
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

/**
 * Controlador para manejar la autenticación de usuarios
 */
@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;


//     * POST /login - Autenticar usuario
//     *
//     * @param datosAutenticacion Email y contraseña del usuario
//     * @return Token JWT (en el Paso 10)
//     *
//     * Ejemplo en Insomnia:
//     * POST http://localhost:8080/login
//     * Body:
//     * {
//     *   "email": "admin@forohub.com",
//     *   "contrasena": "123456"
//     * }


    @PostMapping
    public ResponseEntity<String> autenticarUsuario(
            @RequestBody @Valid DatosAutenticacion datosAutenticacion) {

        // ============================================
        // 1. Crear token de autenticación
        // ============================================
        // UsernamePasswordAuthenticationToken contiene:
        // - username: el email del usuario
        // - password: la contraseña en texto plano
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                datosAutenticacion.email(),
                datosAutenticacion.contrasena()
        );

        // ============================================
        // 2. Autenticar el usuario
        // ============================================
        // Spring Security:
        // 1. Llama a AutenticacionService.loadUserByUsername()
        // 2. Obtiene el usuario de la BD
        // 3. Compara la contraseña con BCrypt
        // 4. Si coincide → Authentication con usuario autenticado
        // 5. Si no coincide → Lanza BadCredentialsException
        Authentication usuarioAutenticado = authenticationManager.authenticate(authToken);

        // ============================================
        // 3. Obtener datos del usuario autenticado
        // ============================================
        Usuario usuario = (Usuario) usuarioAutenticado.getPrincipal();

        // ============================================
        // 4. Retornar respuesta (por ahora solo mensaje)
        // ============================================
        // En el Paso 10 generaremos el token JWT aquí
        return ResponseEntity.ok("Login exitoso para: " + usuario.getEmail());
    }
}