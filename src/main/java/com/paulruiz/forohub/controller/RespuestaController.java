package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.ActualizarRespuestaDTO;
import com.paulruiz.forohub.dto.DetalleRespuestaDTO;
import com.paulruiz.forohub.dto.RespuestaDTO;
import com.paulruiz.forohub.model.Respuesta;
import com.paulruiz.forohub.service.RespuestaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Controlador para gestionar respuestas a tópicos
 * Delega la lógica de negocio a RespuestaService
 */
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

    /**
     * POST /respuestas - Crear una respuesta a un tópico
     *
     * El autor de la respuesta se obtiene automáticamente del usuario autenticado (JWT).
     * NO es necesario enviar autorId en el request.
     * Crea la respuesta y actualiza el status del tópico automáticamente.
     *
     * Ejemplo en Insomnia:
     * POST http://localhost:8080/respuestas
     * Authorization: Bearer [TOKEN]
     * Body:
     * {
     *   "mensaje": "Aquí está la solución...",
     *   "topicoId": 1
     * }
     */
    @PostMapping
    @Operation(
            summary = "Crear respuesta a un tópico",
            description = "Crea una nueva respuesta asociada a un tópico específico. " +
                    "**El autor se obtiene automáticamente del usuario autenticado (JWT)**, " +
                    "por lo que NO es necesario enviar `autorId`. " +
                    "Actualiza automáticamente el status del tópico a NO_SOLUCIONADO. " +
                    "Requiere autenticación JWT.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la respuesta a crear. El autor se obtiene del JWT automáticamente.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ejemplo de respuesta",
                                    summary = "Crear respuesta a un tópico",
                                    description = "El campo 'autorId' NO es necesario. El autor se obtiene del token JWT.",
                                    value = """
                            {
                              "mensaje": "Para implementar JWT con Spring Security, primero necesitas agregar las dependencias correspondientes en tu pom.xml...",
                              "topicoId": 1
                            }
                            """
                            )
                    )
            )
    )
    public ResponseEntity<DetalleRespuestaDTO> crearRespuesta(
            @RequestBody @Valid RespuestaDTO respuestaDTO,
            UriComponentsBuilder uriBuilder) {

        // Delegar creación al servicio (autor se obtiene del JWT automáticamente)
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

    /**
     * GET /respuestas/{id} - Obtener detalle de una respuesta
     *
     * Retorna información completa de la respuesta.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalle de una respuesta",
            description = "Retorna la información completa de una respuesta específica por su ID, " +
                    "incluyendo el tópico al que pertenece y el autor. Requiere autenticación JWT."
    )
    @Parameter(
            name = "id",
            description = "ID de la respuesta a consultar",
            example = "1",
            required = true
    )
    public ResponseEntity<DetalleRespuestaDTO> detalleRespuesta(@PathVariable Long id) {

        // Obtener respuesta del servicio
        Respuesta respuesta = respuestaService.obtenerRespuestaPorId(id);

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }

    // ============================================
    // PUT - Actualizar respuesta
    // ============================================

    /**
     * PUT /respuestas/{id} - Actualizar una respuesta
     *
     * Actualiza el mensaje de la respuesta.
     * SOLO el autor de la respuesta o un ADMIN pueden actualizarla.
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar una respuesta",
            description = "Actualiza el mensaje de una respuesta existente. " +
                    "No se puede cambiar el tópico o autor de la respuesta. " +
                    "Solo el autor de la respuesta o un ADMIN pueden actualizarla. " +
                    "Requiere autenticación JWT.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo mensaje de la respuesta",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ejemplo de actualización",
                                    summary = "Actualizar mensaje de respuesta",
                                    value = """
                            {
                              "mensaje": "Corrección: Además de las dependencias, también necesitas configurar el SecurityFilterChain..."
                            }
                            """
                            )
                    )
            )
    )
    @Parameter(
            name = "id",
            description = "ID de la respuesta a actualizar",
            example = "1",
            required = true
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

    /**
     * DELETE /respuestas/{id} - Eliminar una respuesta
     *
     * Elimina la respuesta y actualiza el status del tópico.
     * SOLO el autor de la respuesta o un ADMIN pueden eliminarla.
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una respuesta",
            description = "Elimina permanentemente una respuesta del sistema. " +
                    "Actualiza automáticamente el status del tópico según las respuestas restantes. " +
                    "Solo el autor de la respuesta o un ADMIN pueden eliminarla. " +
                    "Requiere autenticación JWT."
    )
    @Parameter(
            name = "id",
            description = "ID de la respuesta a eliminar",
            example = "1",
            required = true
    )
    public ResponseEntity<Void> eliminarRespuesta(@PathVariable Long id) {

        // Delegar eliminación al servicio
        respuestaService.eliminarRespuesta(id);

        return ResponseEntity.noContent().build();
    }

    // ============================================
    // PUT - Marcar como solución
    // ============================================

    /**
     * PUT /respuestas/{id}/marcar-solucion - Marcar respuesta como solución
     *
     * Marca la respuesta como solución aceptada del tópico.
     * SOLO el autor del tópico (quien hizo la pregunta) o un ADMIN pueden marcarla.
     */
    @PutMapping("/{id}/marcar-solucion")
    @Operation(
            summary = "Marcar respuesta como solución",
            description = "Marca una respuesta como la solución aceptada del tópico. " +
                    "Solo puede haber una solución por tópico. " +
                    "**Solo el autor del tópico** (quien hizo la pregunta) o un ADMIN pueden marcar la solución. " +
                    "Actualiza automáticamente el status del tópico a SOLUCIONADO. " +
                    "Requiere autenticación JWT."
    )
    @Parameter(
            name = "id",
            description = "ID de la respuesta a marcar como solución",
            example = "1",
            required = true
    )
    public ResponseEntity<DetalleRespuestaDTO> marcarComoSolucion(@PathVariable Long id) {

        // Delegar al servicio
        Respuesta respuesta = respuestaService.marcarComoSolucion(id);

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }
}