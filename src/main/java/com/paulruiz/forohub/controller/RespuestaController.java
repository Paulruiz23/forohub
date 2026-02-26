package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.ActualizarRespuestaDTO;
import com.paulruiz.forohub.dto.DetalleRespuestaDTO;
import com.paulruiz.forohub.dto.RespuestaDTO;
import com.paulruiz.forohub.model.Respuesta;
import com.paulruiz.forohub.service.RespuestaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


// Controlador para gestionar respuestas a tópicos

@RestController
@RequestMapping("/respuestas")
@Tag(name = "Respuestas", description = "Operaciones CRUD para gestionar respuestas a tópicos del foro")
@SecurityRequirement(name = "bearer-key")
public class RespuestaController {

    @Autowired
    private RespuestaService respuestaService;

    // ============================================
    // POST - Crear respuesta
    // ============================================

    /*
      POST /respuestas - Crear una respuesta a un tópico

      Crea la respuesta y actualiza el status del tópico automáticamente.
     */
    @PostMapping
    @Operation(
            summary = "Crear respuesta a un tópico",
            description = "Crea una nueva respuesta asociada a un tópico específico. " +
                    "Actualiza automáticamente el status del tópico a NO_SOLUCIONADO. " +
                    "Requiere autenticación JWT."
    )
    public ResponseEntity<DetalleRespuestaDTO> crearRespuesta(
            @RequestBody @Valid RespuestaDTO respuestaDTO,
            UriComponentsBuilder uriBuilder) {

        // Delegar creación al servicio
        Respuesta respuesta = respuestaService.crearRespuesta(respuestaDTO);

        // Construir URI del recurso creado
        URI url = uriBuilder.path("/respuestas/{id}")
                .buildAndExpand(respuesta.getId())
                .toUri();

        return ResponseEntity.created(url)
                .body(new DetalleRespuestaDTO(respuesta));
    }

    // ============================================
    // GET - Detalle de respuesta
    // ============================================

    /*
      GET /respuestas/{id} - Obtener detalle de una respuesta

      Retorna información completa de la respuesta.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalle de una respuesta",
            description = "Retorna la información completa de una respuesta específica por su ID, " +
                    "incluyendo el tópico al que pertenece y el autor. Requiere autenticación JWT."
    )
    public ResponseEntity<DetalleRespuestaDTO> detalleRespuesta(@PathVariable Long id) {

        // Obtener respuesta del servicio
        Respuesta respuesta = respuestaService.obtenerRespuestaPorId(id);

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }

    // ============================================
    // PUT - Actualizar respuesta
    // ============================================

    /*
      PUT /respuestas/{id} - Actualizar una respuesta

      Actualiza el mensaje de la respuesta.
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar una respuesta",
            description = "Actualiza el mensaje de una respuesta existente. " +
                    "No se puede cambiar el tópico o autor de la respuesta. Requiere autenticación JWT."
    )
    public ResponseEntity<DetalleRespuestaDTO> actualizarRespuesta(
            @PathVariable Long id,
            @RequestBody @Valid ActualizarRespuestaDTO actualizarDTO) {

        // Delegar actualización al servicio
        Respuesta respuesta = respuestaService.actualizarRespuesta(id, actualizarDTO);

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }

    // ============================================
    // DELETE - Eliminar respuesta
    // ============================================

    /*
      DELETE /respuestas/{id} - Eliminar una respuesta

      Elimina la respuesta y actualiza el status del tópico.
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una respuesta",
            description = "Elimina permanentemente una respuesta del sistema. " +
                    "Actualiza automáticamente el status del tópico según las respuestas restantes. " +
                    "Requiere autenticación JWT."
    )
    public ResponseEntity<Void> eliminarRespuesta(@PathVariable Long id) {

        // Delegar eliminación al servicio
        respuestaService.eliminarRespuesta(id);

        return ResponseEntity.noContent().build();
    }

    // ============================================
    // PUT - Marcar como solución
    // ============================================

    /*
      PUT /respuestas/{id}/marcar-solucion - Marcar respuesta como solución

      Marca la respuesta como solución aceptada del tópico.
     */
    @PutMapping("/{id}/marcar-solucion")
    @Operation(
            summary = "Marcar respuesta como solución",
            description = "Marca una respuesta como la solución aceptada del tópico. " +
                    "Solo puede haber una solución por tópico. " +
                    "Actualiza automáticamente el status del tópico a SOLUCIONADO. " +
                    "Requiere autenticación JWT."
    )
    public ResponseEntity<DetalleRespuestaDTO> marcarComoSolucion(@PathVariable Long id) {

        // Delegar al servicio
        Respuesta respuesta = respuestaService.marcarComoSolucion(id);

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }
}