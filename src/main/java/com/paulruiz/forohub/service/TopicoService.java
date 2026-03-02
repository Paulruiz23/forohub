package com.paulruiz.forohub.service;

import com.paulruiz.forohub.dto.ActualizarTopicoDTO;
import com.paulruiz.forohub.dto.TopicoDTO;
import com.paulruiz.forohub.infra.errores.CursoNotFoundException;
import com.paulruiz.forohub.infra.errores.TopicoDuplicadoException;
import com.paulruiz.forohub.infra.errores.TopicoNotFoundException;
import com.paulruiz.forohub.model.Curso;
import com.paulruiz.forohub.model.Topico;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.repository.CursoRepository;
import com.paulruiz.forohub.repository.TopicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio que contiene la lógica de negocio para Tópicos
 */
@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AutorizacionService autorizacionService;

    // ============================================
    // Crear tópico
    // ============================================

    /**
     * Crea un nuevo tópico en el sistema
     * El autor se obtiene automáticamente del usuario autenticado (JWT)
     * Valida que no exista duplicado (mismo título y mensaje)
     *
     * @param topicoDTO Datos del tópico a crear
     * @return Tópico creado
     * @throws TopicoDuplicadoException si el tópico está duplicado
     * @throws CursoNotFoundException si el curso no existe
     */
    @Transactional
    public Topico crearTopico(TopicoDTO topicoDTO) {
        // Validar que no exista duplicado
        validarDuplicado(topicoDTO.titulo(), topicoDTO.mensaje());

        // Obtener autor del JWT automáticamente
        Usuario autor = autorizacionService.obtenerUsuarioAutenticado();

        // Buscar curso
        Curso curso = buscarCurso(topicoDTO.cursoId());

        // Crear el tópico
        Topico topico = new Topico();
        topico.setTitulo(topicoDTO.titulo());
        topico.setMensaje(topicoDTO.mensaje());
        topico.setAutor(autor);  // Autor obtenido del JWT
        topico.setCurso(curso);

        // Guardar y retornar
        return topicoRepository.save(topico);
    }

    // ============================================
    // Listar tópicos
    // ============================================

    /**
     * Lista todos los tópicos con paginación
     *
     * @param paginacion Configuración de paginación
     * @return Página de tópicos
     */
    public Page<Topico> listarTopicos(Pageable paginacion) {
        return topicoRepository.findAll(paginacion);
    }

    // ============================================
    // Obtener tópico por ID
    // ============================================

    /**
     * Busca un tópico por su ID
     *
     * @param id ID del tópico
     * @return Tópico encontrado
     * @throws TopicoNotFoundException si no existe
     */
    public Topico obtenerTopicoPorId(Long id) {
        return topicoRepository.findById(id)
                .orElseThrow(() -> new TopicoNotFoundException(id));
    }

    // ============================================
    // Actualizar tópico
    // ============================================

    /**
     * Actualiza un tópico existente
     * Valida que no se cree duplicado al actualizar
     * SOLO el autor del tópico o un ADMIN puede actualizarlo
     *
     * @param id ID del tópico a actualizar
     * @param actualizarDTO Datos a actualizar
     * @return Tópico actualizado
     * @throws TopicoNotFoundException si el tópico no existe
     * @throws TopicoDuplicadoException si se crea un duplicado
     * @throws CursoNotFoundException si el curso no existe
     * @throws AccesoDenegadoException si no tiene permisos
     */
    @Transactional
    public Topico actualizarTopico(Long id, ActualizarTopicoDTO actualizarDTO) {
        // Buscar tópico
        Topico topico = obtenerTopicoPorId(id);

        // Validar permisos
        autorizacionService.validarPermisoParaModificarTopico(topico);

        // Validar duplicado (excluyendo el tópico actual)
        validarDuplicadoAlActualizar(
                actualizarDTO.titulo(),
                actualizarDTO.mensaje(),
                id
        );

        // Buscar curso si cambió
        Curso curso = buscarCurso(actualizarDTO.cursoId());

        // Actualizar datos
        topico.actualizarDatos(actualizarDTO, curso);

        return topico;
    }

    // ============================================
    // Eliminar tópico
    // ============================================

    /**
     * Elimina un tópico permanentemente
     * SOLO el autor del tópico o un ADMIN puede eliminarlo
     *
     * @param id ID del tópico a eliminar
     * @throws TopicoNotFoundException si no existe
     * @throws AccesoDenegadoException si no tiene permisos
     */
    @Transactional
    public void eliminarTopico(Long id) {
        Topico topico = obtenerTopicoPorId(id);

        // Validar permisos
        autorizacionService.validarPermisoParaModificarTopico(topico);

        topicoRepository.delete(topico);
    }

    // ============================================
    // Métodos privados de validación
    // ============================================

    /**
     * Valida que no exista un tópico con el mismo título y mensaje
     *
     * @throws TopicoDuplicadoException si existe duplicado
     */
    private void validarDuplicado(String titulo, String mensaje) {
        if (topicoRepository.existsByTituloAndMensaje(titulo, mensaje)) {
            throw new TopicoDuplicadoException();
        }
    }

    /**
     * Valida duplicado al actualizar (excluyendo el tópico actual)
     *
     * @throws TopicoDuplicadoException si existe duplicado
     */
    private void validarDuplicadoAlActualizar(String titulo, String mensaje, Long id) {
        if (topicoRepository.existsByTituloAndMensajeAndIdNot(titulo, mensaje, id)) {
            throw new TopicoDuplicadoException();
        }
    }

    /**
     * Busca un curso por ID
     *
     * @throws CursoNotFoundException si no existe
     */
    private Curso buscarCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new CursoNotFoundException(id));
    }
}