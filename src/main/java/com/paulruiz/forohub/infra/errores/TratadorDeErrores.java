package com.paulruiz.forohub.infra.errores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Manejador global de excepciones
 * Captura errores en todos los controllers y retorna respuestas apropiadas
 */
@RestControllerAdvice
public class TratadorDeErrores {

    /**
     * Maneja EntityNotFoundException
     * Retorna 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DatosErrorValidacion> tratarError404(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosErrorValidacion(e.getMessage()));
    }

    /**
     * Maneja errores de validación (@Valid)
     * Retorna 400 Bad Request con lista de campos inválidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> tratarError400(
            MethodArgumentNotValidException e) {

        // Obtener todos los errores de validación
        List<DatosErrorValidacion> errores = e.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();

        return ResponseEntity.badRequest().body(errores);
    }

    /**
     * DTO para retornar información de errores
     */
    private record DatosErrorValidacion(String campo, String error) {

        // Constructor para errores generales
        public DatosErrorValidacion(String error) {
            this(null, error);
        }

        // Constructor para errores de validación de campos
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}