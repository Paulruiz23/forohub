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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/respuestas")
public class RespuestaController {

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // POST /respuestas
    @PostMapping
    @Transactional
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

    // GET /respuestas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DetalleRespuestaDTO> detalleRespuesta(@PathVariable Long id) {

        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Respuesta con ID " + id + " no encontrada"));

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }

    // PUT /respuestas/{id}
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DetalleRespuestaDTO> actualizarRespuesta(
            @PathVariable Long id,
            @RequestBody @Valid ActualizarRespuestaDTO actualizarDTO) {

        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Respuesta con ID " + id + " no encontrada"));

        respuesta.setMensaje(actualizarDTO.mensaje());

        return ResponseEntity.ok(new DetalleRespuestaDTO(respuesta));
    }

    // DELETE /respuestas/{id}
    @DeleteMapping("/{id}")
    @Transactional
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

    // PUT /respuestas/{id}/marcar-solucion
    @PutMapping("/{id}/marcar-solucion")
    @Transactional
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