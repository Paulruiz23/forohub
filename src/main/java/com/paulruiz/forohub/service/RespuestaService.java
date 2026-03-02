package com.paulruiz.forohub.service;

import com.paulruiz.forohub.dto.ActualizarRespuestaDTO;
import com.paulruiz.forohub.dto.DetalleRespuestaDTO;
import com.paulruiz.forohub.dto.RespuestaDTO;
import com.paulruiz.forohub.infra.errores.RespuestaNotFoundException;
import com.paulruiz.forohub.infra.errores.SolucionDuplicadaException;
import com.paulruiz.forohub.infra.errores.TopicoNotFoundException;
import com.paulruiz.forohub.model.Respuesta;
import com.paulruiz.forohub.model.Topico;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.repository.RespuestaRepository;
import com.paulruiz.forohub.repository.TopicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que contiene la lógica de negocio para Respuestas
 */
@Service
public class RespuestaService {

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private AutorizacionService autorizacionService;

    // ============================================
    // Crear respuesta
    // ============================================

    /**
     * Crea una nueva respuesta a un tópico
     * El autor se obtiene automáticamente del usuario autenticado (JWT)
     * Actualiza automáticamente el status del tópico a NO_SOLUCIONADO
     *
     * @param respuestaDTO Datos de la respuesta
     * @return Respuesta creada
     * @throws TopicoNotFoundException si el tópico no existe
     */
    @Transactional
    public Respuesta crearRespuesta(RespuestaDTO respuestaDTO) {
        // Buscar tópico
        Topico topico = buscarTopico(respuestaDTO.topicoId());

        // Obtener autor del JWT automáticamente
        Usuario autor = autorizacionService.obtenerUsuarioAutenticado();

        // Crear respuesta
        Respuesta respuesta = new Respuesta();
        respuesta.setMensaje(respuestaDTO.mensaje());
        respuesta.setTopico(topico);
        respuesta.setAutor(autor);  // Autor obtenido del JWT
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

    /**
     * Lista todas las respuestas de un tópico
     *
     * @param topicoId ID del tópico
     * @return Lista de respuestas en DTO
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

    /**
     * Busca una respuesta por su ID
     *
     * @param id ID de la respuesta
     * @return Respuesta encontrada
     * @throws RespuestaNotFoundException si no existe
     */
    public Respuesta obtenerRespuestaPorId(Long id) {
        return respuestaRepository.findById(id)
                .orElseThrow(() -> new RespuestaNotFoundException(id));
    }

    // ============================================
    // Actualizar respuesta
    // ============================================

    /**
     * Actualiza el mensaje de una respuesta
     * SOLO el autor de la respuesta o un ADMIN puede actualizarla
     *
     * @param id ID de la respuesta
     * @param actualizarDTO Nuevo mensaje
     * @return Respuesta actualizada
     * @throws RespuestaNotFoundException si no existe
     * @throws AccesoDenegadoException si no tiene permisos
     */
    @Transactional
    public Respuesta actualizarRespuesta(Long id, ActualizarRespuestaDTO actualizarDTO) {
        Respuesta respuesta = obtenerRespuestaPorId(id);

        // Validar permisos
        autorizacionService.validarPermisoParaModificarRespuesta(respuesta);

        respuesta.setMensaje(actualizarDTO.mensaje());
        return respuesta;
    }

    // ============================================
    // Eliminar respuesta
    // ============================================

    /**
     * Elimina una respuesta permanentemente
     * SOLO el autor de la respuesta o un ADMIN puede eliminarla
     * Actualiza el status del tópico según las respuestas restantes
     *
     * @param id ID de la respuesta a eliminar
     * @throws RespuestaNotFoundException si no existe
     * @throws AccesoDenegadoException si no tiene permisos
     */
    @Transactional
    public void eliminarRespuesta(Long id) {
        Respuesta respuesta = obtenerRespuestaPorId(id);

        // Validar permisos
        autorizacionService.validarPermisoParaModificarRespuesta(respuesta);

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

    /**
     * Marca una respuesta como solución del tópico
     * SOLO el autor del tópico (quien hizo la pregunta) o un ADMIN puede marcar la solución
     * Solo puede haber una solución por tópico
     *
     * @param id ID de la respuesta
     * @return Respuesta marcada como solución
     * @throws RespuestaNotFoundException si no existe
     * @throws SolucionDuplicadaException si ya hay una solución
     * @throws AccesoDenegadoException si no tiene permisos
     */
    @Transactional
    public Respuesta marcarComoSolucion(Long id) {
        Respuesta respuesta = obtenerRespuestaPorId(id);

        // Validar permisos
        autorizacionService.validarPermisoParaMarcarSolucion(respuesta);

        // Validar que no haya otra solución
        if (respuestaRepository.existeSolucionEnTopico(respuesta.getTopico().getId())) {
            throw new SolucionDuplicadaException();
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

    /**
     * Busca un tópico por ID
     *
     * @throws TopicoNotFoundException si no existe
     */
    private Topico buscarTopico(Long id) {
        return topicoRepository.findById(id)
                .orElseThrow(() -> new TopicoNotFoundException(id));
    }
}