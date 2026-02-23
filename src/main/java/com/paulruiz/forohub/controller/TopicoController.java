package com.paulruiz.forohub.controller;

import com.paulruiz.forohub.dto.DetalleTopicoDTO;
import com.paulruiz.forohub.dto.TopicoDTO;
import com.paulruiz.forohub.infra.errores.EntityNotFoundException;
import com.paulruiz.forohub.model.Curso;
import com.paulruiz.forohub.model.Topico;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.repository.CursoRepository;
import com.paulruiz.forohub.repository.TopicoRepository;
import com.paulruiz.forohub.repository.UsuarioRepository;
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

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    // ============================================
    // POST - Crear tópico (Paso 3)
    // ============================================

    @PostMapping
    @Transactional
    public ResponseEntity<DetalleTopicoDTO> crearTopico(
            @RequestBody @Valid TopicoDTO topicoDTO,
            UriComponentsBuilder uriBuilder) {

        if (topicoRepository.existsByTituloAndMensaje(
                topicoDTO.titulo(),
                topicoDTO.mensaje())) {
            return ResponseEntity.badRequest().build();
        }

        Usuario autor = usuarioRepository.findById(topicoDTO.autorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + topicoDTO.autorId() + " no encontrado"));

        Curso curso = cursoRepository.findById(topicoDTO.cursoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso con ID " + topicoDTO.cursoId() + " no encontrado"));

        Topico topico = new Topico();
        topico.setTitulo(topicoDTO.titulo());
        topico.setMensaje(topicoDTO.mensaje());
        topico.setAutor(autor);
        topico.setCurso(curso);

        topicoRepository.save(topico);

        URI url = uriBuilder.path("/topicos/{id}")
                .buildAndExpand(topico.getId())
                .toUri();

        return ResponseEntity.created(url)
                .body(new DetalleTopicoDTO(topico));
    }

    // ============================================
    // GET - Listar todos (Paso 4)
    // ============================================

    @GetMapping
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
    // GET - Detalle de un tópico específico (NUEVO - Paso 5)
    // ============================================

    /**
     * GET /topicos/{id} - Obtener detalle de un tópico específico
     *
     * @param id ID del tópico a consultar
     * @return ResponseEntity con el tópico o 404 si no existe
     *
     * Ejemplo en Insomnia:
     * GET http://localhost:8080/topicos/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<DetalleTopicoDTO> detalleTopico(
            @PathVariable Long id) {

        // ============================================
        // 1. Buscar el tópico en la base de datos
        // ============================================
        // findById() retorna Optional<Topico>
        // - Si existe: Optional contiene el tópico
        // - Si no existe: Optional está vacío
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tópico con ID " + id + " no encontrado"));

        // ============================================
        // 2. Convertir a DTO
        // ============================================
        DetalleTopicoDTO detalleDTO = new DetalleTopicoDTO(topico);

        // ============================================
        // 3. Retornar respuesta 200 OK
        // ============================================
        return ResponseEntity.ok(detalleDTO);
    }
}
