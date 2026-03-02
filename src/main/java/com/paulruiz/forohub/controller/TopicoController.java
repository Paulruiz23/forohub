package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.ActualizarTopicoDTO;
import com.paulruiz.forohub.dto.DetalleRespuestaDTO;
import com.paulruiz.forohub.dto.DetalleTopicoDTO;
import com.paulruiz.forohub.dto.TopicoDTO;
import com.paulruiz.forohub.model.Topico;
import com.paulruiz.forohub.service.RespuestaService;
import com.paulruiz.forohub.service.TopicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
  Controlador para gestionar tópicos del foro
  Delega la lógica de negocio a TopicoService
 */
@RestController
@RequestMapping("/topicos")
@Tag(name = "Tópicos", description = "Operaciones CRUD para gestionar tópicos del foro")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;

    @Autowired
    private RespuestaService respuestaService;

    // ============================================
    // POST - Crear tópico
    // ============================================

    /*
      POST /topicos - Crear un nuevo tópico

      El autor del tópico se obtiene automáticamente del usuario autenticado (JWT).
      NO es necesario enviar autorId en el request.
      Valida que no exista duplicado (mismo título y mensaje).
      Crea el tópico con status NO_RESPONDIDO por defecto.
      Retorna 201 Created con la URI del recurso creado.

     */
    @PostMapping
    @Operation(
            summary = "Crear un nuevo tópico",
            description = "Crea un nuevo tópico en el foro. **El autor se obtiene automáticamente " +
                    "del usuario autenticado (JWT)**, por lo que NO es necesario enviar `autorId`. " +
                    "Valida que no exista duplicado. Requiere autenticación JWT.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del tópico a crear. El autor se obtiene del JWT automáticamente.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ejemplo de tópico",
                                    summary = "Crear tópico sobre Spring Security",
                                    description = "El campo 'autorId' NO es necesario. El autor se obtiene del token JWT.",
                                    value = """
                        {
                          "titulo": "¿Cómo usar Spring Security?",
                          "mensaje": "Necesito ayuda para implementar autenticación JWT en mi proyecto",
                          "cursoId": 1
                        }
                        """
                            )
                    )
            )
    )
    public ResponseEntity<DetalleTopicoDTO> crearTopico(
            @RequestBody @Valid TopicoDTO topicoDTO,
            UriComponentsBuilder uriBuilder) {

        // Delegar creación al servicio (autor se obtiene del JWT automáticamente)
        Topico topico = topicoService.crearTopico(topicoDTO);

        // Construir URI del recurso creado
        URI url = uriBuilder.path("/topicos/{id}")
                .buildAndExpand(topico.getId())
                .toUri();

        // Retornar respuesta con DTO
        return ResponseEntity.created(url)
                .body(new DetalleTopicoDTO(topico));
    }

    // ============================================
    // GET - Listar tópicos
    // ============================================

    /*
      GET /topicos - Listar todos los tópicos paginados

      Retorna lista paginada ordenada por fecha de creación descendente (más recientes primero).

      Parámetros opcionales:
      - page: Número de página (comienza en 0, default: 0)
      - size: Elementos por página (default: 10)
      - sort: Campo de ordenamiento (default: fechaCreacion,desc)

      Campos disponibles para ordenar:
      - fechaCreacion: Fecha de creación del tópico
      - titulo: Título del tópico
      - status: Estado (NO_RESPONDIDO, NO_SOLUCIONADO, SOLUCIONADO)
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los tópicos",
            description = "Retorna una lista paginada de todos los tópicos.\n\n" +
                    "**Parámetros de paginación:**\n" +
                    "- `page`: Número de página (comienza en 0, default: 0)\n" +
                    "- `size`: Elementos por página (default: 10)\n" +
                    "- `sort`: Campo de ordenamiento (default: fechaCreacion,desc)\n\n" +
                    "**Ejemplos de sort:**\n" +
                    "- `fechaCreacion,desc` - Más recientes primero (default)\n" +
                    "- `fechaCreacion,asc` - Más antiguos primero\n" +
                    "- `titulo,asc` - Alfabéticamente por título\n" +
                    "- `status,asc` - Por status del tópico\n\n" +
                    "**Campos ordenables:** fechaCreacion, titulo, status"
    )
    @Parameter(
            name = "page",
            description = "Número de página (0-indexed). La primera página es 0.",
            example = "0"
    )
    @Parameter(
            name = "size",
            description = "Cantidad de elementos por página. Máximo recomendado: 100",
            example = "10"
    )
    @Parameter(
            name = "sort",
            description = "Criterio de ordenamiento en formato: campo,dirección. " +
                    "Dirección puede ser 'asc' (ascendente) o 'desc' (descendente). " +
                    "Se pueden usar múltiples valores para ordenamiento compuesto.",
            example = "fechaCreacion,desc"
    )
    public ResponseEntity<Page<DetalleTopicoDTO>> listarTopicos(
            @PageableDefault(
                    size = 10,
                    sort = "fechaCreacion",
                    direction = Sort.Direction.DESC
            ) Pageable paginacion) {

        // Obtener página de tópicos del servicio
        Page<Topico> topicos = topicoService.listarTopicos(paginacion);

        // Convertir entidades a DTOs
        Page<DetalleTopicoDTO> topicosDTO = topicos.map(DetalleTopicoDTO::new);

        return ResponseEntity.ok(topicosDTO);
    }

    // ============================================
    // GET - Detalle de tópico
    // ============================================

    /*
      GET /topicos/{id} - Obtener detalle de un tópico

      Retorna información completa del tópico incluyendo:
      - Datos del tópico (título, mensaje, fecha, status)
      - Información del autor
      - Información del curso

     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalle de un tópico",
            description = "Retorna la información completa de un tópico específico por su ID."
    )
    @Parameter(
            name = "id",
            description = "ID del tópico a consultar",
            example = "1",
            required = true
    )
    public ResponseEntity<DetalleTopicoDTO> detalleTopico(@PathVariable Long id) {

        // Obtener tópico del servicio
        Topico topico = topicoService.obtenerTopicoPorId(id);

        return ResponseEntity.ok(new DetalleTopicoDTO(topico));
    }

    // ============================================
    // GET - Respuestas de un tópico
    // ============================================

    /*
      GET /topicos/{id}/respuestas - Listar respuestas de un tópico

      Retorna todas las respuestas asociadas al tópico ordenadas por fecha.
      Incluye información del autor de cada respuesta y si está marcada como solución.

     */
    @GetMapping("/{id}/respuestas")
    @Operation(
            summary = "Listar respuestas de un tópico",
            description = "Retorna todas las respuestas asociadas a un tópico específico, " +
                    "ordenadas por fecha de creación."
    )
    @Parameter(
            name = "id",
            description = "ID del tópico del cual se desean obtener las respuestas",
            example = "1",
            required = true
    )
    public ResponseEntity<List<DetalleRespuestaDTO>> listarRespuestasDeTopico(
            @PathVariable Long id) {

        // Validar que el tópico existe
        topicoService.obtenerTopicoPorId(id);

        // Obtener respuestas del servicio
        List<DetalleRespuestaDTO> respuestas = respuestaService
                .listarRespuestasDeTopico(id);

        return ResponseEntity.ok(respuestas);
    }

    // ============================================
    // PUT - Actualizar tópico
    // ============================================

    /*
      PUT /topicos/{id} - Actualizar un tópico

      Permite actualizar título, mensaje y/o curso del tópico.
      NO permite cambiar el autor del tópico.
      SOLO el autor del tópico o un ADMIN pueden actualizarlo.
      Valida que no se cree duplicado al actualizar.

     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un tópico",
            description = "Actualiza el título, mensaje y/o curso de un tópico existente. " +
                    "NO permite cambiar el autor. Solo el autor del tópico o un ADMIN pueden actualizarlo. " +
                    "Valida duplicados.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos a actualizar del tópico",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Ejemplo de actualización",
                                    summary = "Actualizar tópico existente",
                                    value = """
                        {
                          "titulo": "¿Cómo usar Spring Security con JWT?",
                          "mensaje": "Necesito ayuda para implementar autenticación completa",
                          "cursoId": 1
                        }
                        """
                            )
                    )
            )
    )
    @Parameter(
            name = "id",
            description = "ID del tópico a actualizar",
            example = "1",
            required = true
    )
    public ResponseEntity<DetalleTopicoDTO> actualizarTopico(
            @PathVariable Long id,
            @RequestBody @Valid ActualizarTopicoDTO actualizarDTO) {

        // Delegar actualización al servicio
        Topico topico = topicoService.actualizarTopico(id, actualizarDTO);

        return ResponseEntity.ok(new DetalleTopicoDTO(topico));
    }

    // ============================================
    // DELETE - Eliminar tópico
    // ============================================

    /*
      DELETE /topicos/{id} - Eliminar un tópico

      Elimina permanentemente el tópico y todas sus respuestas asociadas (CASCADE).
      SOLO el autor del tópico o un ADMIN pueden eliminarlo.
      Esta acción NO es reversible.
      Retorna 204 No Content si la eliminación es exitosa.

    */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un tópico",
            description = "Elimina permanentemente un tópico y todas sus respuestas asociadas. " +
                    "Solo el autor del tópico o un ADMIN pueden eliminarlo. " +
                    "Esta acción no es reversible."
    )
    @Parameter(
            name = "id",
            description = "ID del tópico a eliminar",
            example = "1",
            required = true
    )
    public ResponseEntity<Void> eliminarTopico(@PathVariable Long id) {

        // Delegar eliminación al servicio
        topicoService.eliminarTopico(id);

        return ResponseEntity.noContent().build();
    }
}