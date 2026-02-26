package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.ActualizarTopicoDTO;
import com.paulruiz.forohub.dto.DetalleRespuestaDTO;
import com.paulruiz.forohub.dto.DetalleTopicoDTO;
import com.paulruiz.forohub.dto.TopicoDTO;
import com.paulruiz.forohub.model.Topico;
import com.paulruiz.forohub.service.RespuestaService;
import com.paulruiz.forohub.service.TopicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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


// Controlador para gestionar tópicos del foro
// Delega la lógica de negocio a TopicoService

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

      Valida duplicados y crea el tópico en el sistema.
      Retorna 201 Created con la URI del recurso creado.
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo tópico",
            description = "Crea un nuevo tópico en el foro. Requiere autenticación JWT.")
    public ResponseEntity<DetalleTopicoDTO> crearTopico(
            @RequestBody @Valid TopicoDTO topicoDTO,
            UriComponentsBuilder uriBuilder) {

        // Delegar creación al servicio
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

      Retorna lista paginada ordenada por fecha de creación descendente.
     */
    @GetMapping
    @Operation(summary = "Listar todos los tópicos",
            description = "Retorna una lista paginada de todos los tópicos por fecha de creación descendente.")
    public ResponseEntity<Page<DetalleTopicoDTO>> listarTopicos(
            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "fechaCreacion",
                    direction = Sort.Direction.DESC
            ) Pageable paginacion) {

        // Obtener página de tópicos del servicio
        Page<Topico> topicos = topicoService.listarTopicos(paginacion);

        // Convertir a DTOs
        Page<DetalleTopicoDTO> topicosDTO = topicos.map(DetalleTopicoDTO::new);

        return ResponseEntity.ok(topicosDTO);
    }

    // ============================================
    // GET - Detalle de tópico
    // ============================================

    /**
     * GET /topicos/{id} - Obtener detalle de un tópico
     *
     * Retorna información completa del tópico.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un tópico",
            description = "Retorna la información completa de un tópico específico por su ID.")
    public ResponseEntity<DetalleTopicoDTO> detalleTopico(@PathVariable Long id) {

        // Obtener tópico del servicio
        Topico topico = topicoService.obtenerTopicoPorId(id);

        return ResponseEntity.ok(new DetalleTopicoDTO(topico));
    }

    // ============================================
    // GET - Respuestas de un tópico
    // ============================================

    /**
     * GET /topicos/{id}/respuestas - Listar respuestas de un tópico
     *
     * Retorna todas las respuestas asociadas al tópico.
     */
    @GetMapping("/{id}/respuestas")
    @Operation(summary = "Listar respuestas de un tópico",
            description = "Retorna todas las respuestas asociadas a un tópico específico.")
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

    /**
     * PUT /topicos/{id} - Actualizar un tópico
     *
     * Actualiza título, mensaje y/o curso del tópico.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un tópico",
            description = "Actualiza el título, mensaje y/o curso de un tópico existente.")
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

    /**
     * DELETE /topicos/{id} - Eliminar un tópico
     *
     * Elimina permanentemente el tópico y sus respuestas.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un tópico",
            description = "Elimina permanentemente un tópico y todas sus respuestas asociadas.")
    public ResponseEntity<Void> eliminarTopico(@PathVariable Long id) {

        // Delegar eliminación al servicio
        topicoService.eliminarTopico(id);

        return ResponseEntity.noContent().build();
    }
}