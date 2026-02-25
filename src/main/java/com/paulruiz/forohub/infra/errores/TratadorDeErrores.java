package com.paulruiz.forohub.infra.errores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorDeErrores {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DatosErrorValidacion> tratarError404(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> tratarError400(
            MethodArgumentNotValidException e) {

        List<DatosErrorValidacion> errores = e.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();

        return ResponseEntity.badRequest().body(errores);
    }

    // ============================================
    // NUEVO - Manejar errores de autenticación
    // ============================================

    /**
     * Maneja errores de credenciales inválidas (email o contraseña incorrectos)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new DatosErrorValidacion("Credenciales inválidas"));
    }

    /**
     * Maneja otros errores de autenticación
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorAuthentication(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new DatosErrorValidacion("Error de autenticación: " + e.getMessage()));
    }

    private record DatosErrorValidacion(String campo, String error) {

        public DatosErrorValidacion(String error) {
            this(null, error);
        }

        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}