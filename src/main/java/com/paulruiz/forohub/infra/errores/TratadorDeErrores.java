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

    // Maneja errores cuando no se encuentra una entidad en la base de datos (404)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DatosErrorValidacion> tratarError404(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion(e.getMessage()));
    }

    // Maneja errores de validación en los campos del request (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> tratarError400(
            MethodArgumentNotValidException e) {

        // Convierte los errores de validación en una lista personalizada
        List<DatosErrorValidacion> errores = e.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();

        return ResponseEntity.badRequest().body(errores);
    }

    // Maneja error cuando las credenciales son incorrectas (401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new DatosErrorValidacion("Credenciales inválidas"));
    }

    // Maneja errores generales de autenticación (401)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorAuthentication(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new DatosErrorValidacion("Error de autenticación: " + e.getMessage()));
    }

    // Maneja errores cuando se intenta registrar un email duplicado (400)
    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorEmailDuplicado(EmailDuplicadoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DatosErrorValidacion("email", e.getMessage()));
    }

    // Record que representa la estructura del error que se envía en la respuesta
    private record DatosErrorValidacion(String campo, String error) {

        public DatosErrorValidacion(String error) {
            this(null, error);
        }

        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}