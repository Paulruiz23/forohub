package com.paulruiz.forohub.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.paulruiz.forohub.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


// Servicio para generar y validar tokens JWT

@Service
public class TokenService {

    // Inyectar la clave secreta desde application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Inyectar tiempo de expiración desde application.properties
    @Value("${jwt.expiration}")
    private Long expiration;

    /*
     Genera un token JWT para un usuario autenticado

     @param usuario Usuario que se autenticó
     @return String con el token JWT
     */

    public String generarToken(Usuario usuario) {
        try {
            // Crear algoritmo de firma usando la clave secreta
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Generar el token
            return JWT.create()
                    .withIssuer("forohub")              // Emisor del token
                    .withSubject(usuario.getEmail())    // Subject = email del usuario
                    .withClaim("id", usuario.getId())   // Claim adicional: ID del usuario
                    .withExpiresAt(generarFechaExpiracion()) // Fecha de expiración
                    .sign(algorithm);                   // Firmar con el algoritmo

        } catch (JWTCreationException exception){
            throw new RuntimeException("Error al generar token JWT", exception);
        }
    }

    /*
     Valida un token JWT y extrae el subject (email del usuario)

     @param token Token JWT a validar
     @return Email del usuario si el token es válido
     @throws RuntimeException si el token es inválido o expiró
     */

    public String getSubject(String token) {
        if (token == null) {
            throw new RuntimeException("Token nulo");
        }

        try {
            // Crear algoritmo de verificación
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Verificar y decodificar el token
            return JWT.require(algorithm)
                    .withIssuer("forohub")              // Verificar que el emisor sea correcto
                    .build()
                    .verify(token)                      // Verificar firma y expiración
                    .getSubject();                      // Obtener el subject (email)

        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido o expirado");
        }
    }

    /*
     Genera la fecha de expiración del token
     Fecha actual + tiempo de expiración configurado

     @return Instant con la fecha de expiración
     */
    private Instant generarFechaExpiracion() {
        return LocalDateTime.now()
                .plusSeconds(expiration / 1000)  // Convertir milisegundos a segundos
                .toInstant(ZoneOffset.of("-05:00")); // Zona horaria (ajústala a la tuya)
    }
}