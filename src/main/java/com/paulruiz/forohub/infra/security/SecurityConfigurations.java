package com.paulruiz.forohub.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
  Configuración de seguridad de la aplicación
  Define qué endpoints son públicos y cuáles requieren autenticación
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private AutenticacionService autenticacionService;

    @Autowired
    private SecurityFilter securityFilter;

    // ============================================
    // Configurar cadena de filtros de seguridad
    // ============================================

    /*
     Configura las reglas de seguridad HTTP
     - Endpoints públicos: /login, /usuarios (registro), /swagger-ui/**
     - Endpoints protegidos: todos los demás requieren JWT
     - Solo ADMIN puede bloquear/desbloquear usuarios
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Deshabilitar CSRF (no es necesario para APIs stateless)
                .csrf(csrf -> csrf.disable())

                // Configurar sesiones como STATELESS (sin estado, usa JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configurar autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (sin autenticación)
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        // Endpoints solo para ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/*/desbloquear").hasRole("ADMIN")

                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated()
                )

                // Añadir filtro personalizado antes del filtro de autenticación de Spring
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ============================================
    // AuthenticationManager Bean
    // ============================================

    /*
     Bean para gestionar autenticación
     Spring Security lo usa para validar credenciales
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // ============================================
    // PasswordEncoder Bean
    // ============================================

    /*
     Bean para encriptar contraseñas con BCrypt
     Spring Security lo usa automáticamente para comparar passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}