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

/**
 * Manejador global de excepciones
 * Captura todas las excepciones de la aplicación y retorna respuestas HTTP apropiadas
 */
@RestControllerAdvice
public class TratadorDeErrores {

    // ============================================
    // Errores 404 - Not Found
    // ============================================

    /**
     * Maneja errores cuando no se encuentra un tópico
     */
    @ExceptionHandler(TopicoNotFoundException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorTopicoNoEncontrado(
            TopicoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion(e.getMessage()));
    }

    /**
     * Maneja errores cuando no se encuentra un usuario
     */
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorUsuarioNoEncontrado(
            UsuarioNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion(e.getMessage()));
    }

    /**
     * Maneja errores cuando no se encuentra un curso
     */
    @ExceptionHandler(CursoNotFoundException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorCursoNoEncontrado(
            CursoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion(e.getMessage()));
    }

    /**
     * Maneja errores cuando no se encuentra una respuesta
     */
    @ExceptionHandler(RespuestaNotFoundException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorRespuestaNoEncontrada(
            RespuestaNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion(e.getMessage()));
    }

    /**
     * Maneja errores cuando no se encuentra un perfil/rol
     */
    @ExceptionHandler(PerfilNotFoundException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorPerfilNoEncontrado(
            PerfilNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion(e.getMessage()));
    }

    /**
     * Maneja errores genéricos de entidad no encontrada
     * (fallback para compatibilidad con código legacy)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DatosErrorValidacion> tratarError404(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion(e.getMessage()));
    }

    // ============================================
    // Errores 400 - Bad Request
    // ============================================

    /**
     * Maneja errores de validación de campos (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> tratarErrorValidacion(
            MethodArgumentNotValidException e) {

        List<DatosErrorValidacion> errores = e.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();

        return ResponseEntity.badRequest().body(errores);
    }

    /**
     * Maneja errores cuando se intenta registrar un email duplicado
     */
    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorEmailDuplicado(
            EmailDuplicadoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DatosErrorValidacion("email", e.getMessage()));
    }

    /**
     * Maneja errores cuando se intenta crear un tópico duplicado
     */
    @ExceptionHandler(TopicoDuplicadoException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorTopicoDuplicado(
            TopicoDuplicadoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DatosErrorValidacion("topico", e.getMessage()));
    }

    /**
     * Maneja errores cuando se intenta bloquear un usuario ya bloqueado
     */
    @ExceptionHandler(UsuarioBloqueadoException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorUsuarioBloqueado(
            UsuarioBloqueadoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DatosErrorValidacion("activo", e.getMessage()));
    }

    /**
     * Maneja errores cuando se intenta desbloquear un usuario ya activo
     */
    @ExceptionHandler(UsuarioActivoException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorUsuarioActivo(
            UsuarioActivoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DatosErrorValidacion("activo", e.getMessage()));
    }

    /**
     * Maneja errores cuando se intenta marcar una segunda solución en un tópico
     */
    @ExceptionHandler(SolucionDuplicadaException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorSolucionDuplicada(
            SolucionDuplicadaException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DatosErrorValidacion("solucion", e.getMessage()));
    }

    // ============================================
    // Errores 401 - Unauthorized
    // ============================================

    /**
     * Maneja errores de credenciales inválidas (login fallido)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new DatosErrorValidacion("Credenciales inválidas"));
    }

    /**
     * Maneja errores genéricos de autenticación
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorAuthentication(
            AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new DatosErrorValidacion("Error de autenticación: " + e.getMessage()));
    }

    // ============================================
    // DTO interno para respuestas de error
    // ============================================

    /**
     * Registro interno para estructurar respuestas de error
     */
    private record DatosErrorValidacion(String campo, String error) {

        public DatosErrorValidacion(String error) {
            this(null, error);
        }

        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
    // ============================================
    // Errores 403 - Forbidden
    // ============================================

    /**
     * Maneja errores de acceso denegado (sin permisos)
     */
    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<DatosErrorValidacion> tratarErrorAccesoDenegado(
            AccesoDenegadoException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new DatosErrorValidacion("permiso", e.getMessage()));
    }
}