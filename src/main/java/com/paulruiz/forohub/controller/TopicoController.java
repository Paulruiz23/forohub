package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.ActualizarTopicoDTO;
import com.paulruiz.forohub.dto.DetalleRespuestaDTO;
import com.paulruiz.forohub.dto.DetalleTopicoDTO;
import com.paulruiz.forohub.dto.TopicoDTO;
import com.paulruiz.forohub.infra.errores.EntityNotFoundException;
import com.paulruiz.forohub.model.Curso;
import com.paulruiz.forohub.model.Respuesta;
import com.paulruiz.forohub.model.Topico;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.repository.CursoRepository;
import com.paulruiz.forohub.repository.RespuestaRepository;
import com.paulruiz.forohub.repository.TopicoRepository;
import com.paulruiz.forohub.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
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

@RestController
@RequestMapping("/topicos")
@Tag(name = "Tópicos", description = "Operaciones CRUD para gestionar tópicos del foro")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private RespuestaRepository respuestaRepository;

    // ============================================
    // Crear tópico
    // ============================================

/*
   POST /topicos - Crear un nuevo tópico

   Crea un nuevo tópico en el sistema.
   Valida que no exista otro con el mismo título y mensaje.
   Asocia el tópico a un usuario (autor) y a un curso.
   Requiere autenticación JWT.
*/

    @PostMapping
    @Transactional
    @Operation(
            summary = "Crear un nuevo tópico",
            description = "Crea un nuevo tópico en el foro. Requiere autenticación JWT."
    )
    public ResponseEntity<DetalleTopicoDTO> crearTopico(
            @RequestBody @Valid TopicoDTO topicoDTO,
            UriComponentsBuilder uriBuilder) {

        // Verificar que no exista un tópico duplicado
        if (topicoRepository.existsByTituloAndMensaje(
                topicoDTO.titulo(),
                topicoDTO.mensaje())) {
            return ResponseEntity.badRequest().build();
        }

        // Buscar el usuario autor
        Usuario autor = usuarioRepository.findById(topicoDTO.autorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + topicoDTO.autorId() + " no encontrado"));

        // Buscar el curso asociado
        Curso curso = cursoRepository.findById(topicoDTO.cursoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso con ID " + topicoDTO.cursoId() + " no encontrado"));

        // Crear el tópico
        Topico topico = new Topico();
        topico.setTitulo(topicoDTO.titulo());
        topico.setMensaje(topicoDTO.mensaje());
        topico.setAutor(autor);
        topico.setCurso(curso);

        topicoRepository.save(topico);

        // Construir URI del recurso creado
        URI url = uriBuilder.path("/topicos/{id}")
                .buildAndExpand(topico.getId())
                .toUri();

        return ResponseEntity.created(url)
                .body(new DetalleTopicoDTO(topico));
    }

    // ============================================
    // Listar tópicos
    // ============================================

/*
   GET /topicos - Listar todos los tópicos

   Retorna una lista paginada de tópicos.
   Ordenados por fecha de creación descendente.
*/

    @GetMapping
    @Operation(
            summary = "Listar todos los tópicos",
            description = "Retorna una lista paginada de todos los tópicos. Ordenados por fecha de creación descendente."
    )
    public ResponseEntity<Page<DetalleTopicoDTO>> listarTopicos(
            @PageableDefault(
                    size = 10,
                    sort = "fechaCreacion",
                    direction = Sort.Direction.DESC
            ) Pageable paginacion) {

        Page<Topico> topicos = topicoRepository.findAll(paginacion);
        Page<DetalleTopicoDTO> topicosDTO = topicos.map(DetalleTopicoDTO::new);

        return ResponseEntity.ok(topicosDTO);
    }


    // ============================================
    // Detalle de tópico
    // ============================================

/*
   GET /topicos/{id} - Obtener detalle de un tópico

   Retorna la información completa de un tópico específico por su ID.
*/

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalle de un tópico",
            description = "Retorna la información completa de un tópico específico por su ID."
    )
    public ResponseEntity<DetalleTopicoDTO> detalleTopico(@PathVariable Long id) {

        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tópico con ID " + id + " no encontrado"));

        return ResponseEntity.ok(new DetalleTopicoDTO(topico));
    }


    // ============================================
    // Listar respuestas de un tópico
    // ============================================

/*
   GET /topicos/{id}/respuestas

   Retorna todas las respuestas asociadas a un tópico específico.
*/

    @GetMapping("/{id}/respuestas")
    @Operation(
            summary = "Listar respuestas de un tópico",
            description = "Retorna todas las respuestas asociadas a un tópico específico."
    )
    public ResponseEntity<List<DetalleRespuestaDTO>> listarRespuestasDeTopico(
            @PathVariable Long id) {

        // Verificar que el tópico exista
        topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tópico con ID " + id + " no encontrado"));

        List<Respuesta> respuestas = respuestaRepository.findByTopicoId(id);

        List<DetalleRespuestaDTO> respuestasDTO = respuestas.stream()
                .map(DetalleRespuestaDTO::new)
                .toList();

        return ResponseEntity.ok(respuestasDTO);
    }


    // ============================================
    // Actualizar tópico
    // ============================================

/*
   PUT /topicos/{id} - Actualizar un tópico

   Permite modificar título, mensaje y curso.
   Valida que no exista otro tópico duplicado.
*/

    @PutMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Actualizar un tópico",
            description = "Actualiza el título, mensaje y/o curso de un tópico existente."
    )
    public ResponseEntity<DetalleTopicoDTO> actualizarTopico(
            @PathVariable Long id,
            @RequestBody @Valid ActualizarTopicoDTO actualizarDTO) {

        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tópico con ID " + id + " no encontrado"));

        if (topicoRepository.existsByTituloAndMensajeAndIdNot(
                actualizarDTO.titulo(),
                actualizarDTO.mensaje(),
                id)) {
            return ResponseEntity.badRequest().build();
        }

        Curso curso = cursoRepository.findById(actualizarDTO.cursoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso con ID " + actualizarDTO.cursoId() + " no encontrado"));

        topico.actualizarDatos(actualizarDTO, curso);

        return ResponseEntity.ok(new DetalleTopicoDTO(topico));
    }


    // ============================================
    // Eliminar tópico
    // ============================================

/*
   DELETE /topicos/{id}

   Elimina permanentemente un tópico y sus respuestas asociadas.
*/

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Eliminar un tópico",
            description = "Elimina permanentemente un tópico y todas sus respuestas asociadas."
    )
    public ResponseEntity<Void> eliminarTopico(@PathVariable Long id) {

        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tópico con ID " + id + " no encontrado"));

        topicoRepository.delete(topico);

        return ResponseEntity.noContent().build();
    }
}