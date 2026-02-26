package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.ActualizarRespuestaDTO;
import com.paulruiz.forohub.dto.DetalleRespuestaDTO;
import com.paulruiz.forohub.dto.RespuestaDTO;
import com.paulruiz.forohub.infra.errores.EntityNotFoundException;
import com.paulruiz.forohub.model.Respuesta;
import com.paulruiz.forohub.model.Topico;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.repository.RespuestaRepository;
import com.paulruiz.forohub.repository.TopicoRepository;
import com.paulruiz.forohub.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
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
    private RespuestaRepository respuestaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
// ============================================
// Crear respuesta
// ============================================

/*
   POST /respuestas - Crear una respuesta a un tópico

   Crea una nueva respuesta asociada a un tópico existente.
   Valida que el tópico y el autor existan.
   Actualiza automáticamente el status del tópico a NO_SOLUCIONADO.
*/

    @PostMapping
    @Transactional
    @Operation(
            summary = "Crear respuesta a un tópico",
            description = "Crea una nueva respuesta asociada a un tópico específico. " +
                    "Actualiza automáticamente el status del tópico a NO_SOLUCIONADO. " +
                    "Requiere autenticación JWT."
    )
    public ResponseEntity<DetalleRespuestaDTO> crearRespuesta(
            @RequestBody @Valid RespuestaDTO respuestaDTO,
            UriComponentsBuilder uriBuilder) {

        Topico topico = topicoRepository.findById(respuestaDTO.topicoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tópico con ID " + respuestaDTO.topicoId() + " no encontrado"));

        Usuario autor = usuarioRepository.findById(respuestaDTO.autorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + respuestaDTO.autorId() + " no encontrado"));

        Respuesta respuesta = new Respuesta();
        respuesta.setMensaje(respuestaDTO.mensaje());
        respuesta.setTopico(topico);
        respuesta.setAutor(autor);
        respuesta.setSolucion(false);

        respuestaRepository.save(respuesta);

        topico.actualizarStatus(true, false);

        URI url = uriBuilder.path("/respuestas/{id}")
                .buildAndExpand(respuesta.getId())
                .toUri();

        return ResponseEntity.created(url)
                .body(new DetalleRespuestaDTO(respuesta));
    }


    // ============================================
    // Detalle de respuesta
    // ============================================

/*
   GET /respuestas/{id}

   Retorna la información completa de una respuesta específica.
*/

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalle de una respuesta",
            description = "Retorna la información completa de una respuesta específica por su ID."
    )
    public ResponseEntity<DetalleRespuestaDTO> detalleRespuesta(@PathVariable Long id) {

        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Respuesta con ID " + id + " no encontrada"));

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }


    // ============================================
    // Actualizar respuesta
    // ============================================

/*
   PUT /respuestas/{id}

   Permite modificar únicamente el mensaje de la respuesta.
*/

    @PutMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Actualizar una respuesta",
            description = "Actualiza el mensaje de una respuesta existente. No se puede cambiar el tópico ni el autor."
    )
    public ResponseEntity<DetalleRespuestaDTO> actualizarRespuesta(
            @PathVariable Long id,
            @RequestBody @Valid ActualizarRespuestaDTO actualizarDTO) {

        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Respuesta con ID " + id + " no encontrada"));

        respuesta.setMensaje(actualizarDTO.mensaje());

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }


    // ============================================
    // Eliminar respuesta
    // ============================================

/*
   DELETE /respuestas/{id}

   Elimina permanentemente una respuesta.
   Recalcula el status del tópico automáticamente.
*/

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Eliminar una respuesta",
            description = "Elimina permanentemente una respuesta y actualiza el status del tópico."
    )
    public ResponseEntity<Void> eliminarRespuesta(@PathVariable Long id) {

        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Respuesta con ID " + id + " no encontrada"));

        Topico topico = respuesta.getTopico();

        respuestaRepository.delete(respuesta);

        Long cantidadRespuestas = respuestaRepository.countByTopicoId(topico.getId());
        boolean tieneSolucion = respuestaRepository.existeSolucionEnTopico(topico.getId());

        topico.actualizarStatus(cantidadRespuestas > 0, tieneSolucion);

        return ResponseEntity.noContent().build();
    }


    // ============================================
    // Marcar respuesta como solución
    // ============================================

/*
   PUT /respuestas/{id}/marcar-solucion

   Marca una respuesta como la solución aceptada.
   Solo puede existir una solución por tópico.
*/

    @PutMapping("/{id}/marcar-solucion")
    @Transactional
    @Operation(
            summary = "Marcar respuesta como solución",
            description = "Marca una respuesta como la solución aceptada del tópico y actualiza su status a SOLUCIONADO."
    )
    public ResponseEntity<DetalleRespuestaDTO> marcarComoSolucion(@PathVariable Long id) {

        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Respuesta con ID " + id + " no encontrada"));

        if (respuestaRepository.existeSolucionEnTopico(respuesta.getTopico().getId())) {
            throw new RuntimeException("Este tópico ya tiene una respuesta marcada como solución");
        }

        respuesta.setSolucion(true);

        respuesta.getTopico().actualizarStatus(true, true);

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }

}