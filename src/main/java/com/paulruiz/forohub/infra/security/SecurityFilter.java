package com.paulruiz.forohub.infra.security;

import com.paulruiz.forohub.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 Filtro que intercepta todas las peticiones HTTP
 Valida el token JWT y autentica al usuario
 */

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /*
     Método que se ejecuta en cada petición HTTP

     @param request Petición HTTP
     @param response Respuesta HTTP
     @param filterChain Cadena de filtros
     */

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {


        // 1. Obtener el token del header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {
            // El header viene como: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6..."
            // Necesitamos quitar el "Bearer " y quedarnos solo con el token
            String token = authHeader.replace("Bearer ", "");


            // 2. Validar el token y obtener el email del usuario
            String email = tokenService.getSubject(token);

            if (email != null) {

                // 3. Buscar el usuario en la base de datos
                UserDetails usuario = usuarioRepository.findByEmail(email);

                // 4. Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                usuario,              // Usuario autenticado
                                null,                 // Credenciales (no son necesarias aquí)
                                usuario.getAuthorities()  // Permisos del usuario
                        );

                // 5. Forzar autenticación en el contexto de seguridad
                // Esto le dice a Spring Security que el usuario está autenticado
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 6. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}