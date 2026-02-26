package com.paulruiz.forohub.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Servicio que contiene la lógica de negocio para Respuestas

@Service
public class RespuestaService {

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
      Crea una nueva respuesta a un tópico
      Actualiza automáticamente el status del tópico a NO_SOLUCIONADO

      @param respuestaDTO Datos de la respuesta
      @return Respuesta creada
     */
    @Transactional
    public Respuesta crearRespuesta(RespuestaDTO respuestaDTO) {
        // Buscar tópico y autor
        Topico topico = buscarTopico(respuestaDTO.topicoId());
        Usuario autor = buscarAutor(respuestaDTO.autorId());

        // Crear respuesta
        Respuesta respuesta = new Respuesta();
        respuesta.setMensaje(respuestaDTO.mensaje());
        respuesta.setTopico(topico);
        respuesta.setAutor(autor);
        respuesta.setSolucion(false);

        // Guardar respuesta
        respuestaRepository.save(respuesta);

        // Actualizar status del tópico
        topico.actualizarStatus(true, false);

        return respuesta;
    }

    // ============================================
    // Listar respuestas de un tópico
    // ============================================

    /*
      Lista todas las respuestas de un tópico

      @param topicoId ID del tópico
      @return Lista de respuestas en DTO
     */
    public List<DetalleRespuestaDTO> listarRespuestasDeTopico(Long topicoId) {
        List<Respuesta> respuestas = respuestaRepository.findByTopicoId(topicoId);

        return respuestas.stream()
                .map(DetalleRespuestaDTO::new)
                .collect(Collectors.toList());
    }

    // ============================================
    // Obtener respuesta por ID
    // ============================================

    /*
      Busca una respuesta por su ID

      @param id ID de la respuesta
      @return Respuesta encontrada
     */
    public Respuesta obtenerRespuestaPorId(Long id) {
        return respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Respuesta con ID " + id + " no encontrada"));
    }

    // ============================================
    // Actualizar respuesta
    // ============================================

    /*
      Actualiza el mensaje de una respuesta

      @param id ID de la respuesta
      @param actualizarDTO Nuevo mensaje
      @return Respuesta actualizada
     */
    @Transactional
    public Respuesta actualizarRespuesta(Long id, ActualizarRespuestaDTO actualizarDTO) {
        Respuesta respuesta = obtenerRespuestaPorId(id);
        respuesta.setMensaje(actualizarDTO.mensaje());
        return respuesta;
    }

    // ============================================
    // Eliminar respuesta
    // ============================================

    /*
      Elimina una respuesta permanentemente
      Actualiza el status del tópico según las respuestas restantes

      @param id ID de la respuesta a eliminar
     */
    @Transactional
    public void eliminarRespuesta(Long id) {
        Respuesta respuesta = obtenerRespuestaPorId(id);
        Topico topico = respuesta.getTopico();

        // Eliminar respuesta
        respuestaRepository.delete(respuesta);

        // Actualizar status del tópico
        Long cantidadRespuestas = respuestaRepository.countByTopicoId(topico.getId());
        boolean tieneSolucion = respuestaRepository.existeSolucionEnTopico(topico.getId());

        topico.actualizarStatus(cantidadRespuestas > 0, tieneSolucion);
    }

    // ============================================
    // Marcar respuesta como solución
    // ============================================

    /*
      Marca una respuesta como solución del tópico
      Solo puede haber una solución por tópico

      @param id ID de la respuesta
      @return Respuesta marcada como solución
     */
    @Transactional
    public Respuesta marcarComoSolucion(Long id) {
        Respuesta respuesta = obtenerRespuestaPorId(id);

        // Validar que no haya otra solución
        if (respuestaRepository.existeSolucionEnTopico(respuesta.getTopico().getId())) {
            throw new RuntimeException("Este tópico ya tiene una respuesta marcada como solución");
        }

        // Marcar como solución
        respuesta.setSolucion(true);

        // Actualizar status del tópico a SOLUCIONADO
        respuesta.getTopico().actualizarStatus(true, true);

        return respuesta;
    }

    // ============================================
    // Métodos privados
    // ============================================


    // Busca un tópico por ID

    private Topico buscarTopico(Long id) {
        return topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tópico con ID " + id + " no encontrado"));
    }


    // Busca un autor por ID

    private Usuario buscarAutor(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + id + " no encontrado"));
    }
}