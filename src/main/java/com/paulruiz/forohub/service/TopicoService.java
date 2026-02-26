package com.paulruiz.forohub.service;

import com.paulruiz.forohub.dto.ActualizarTopicoDTO;
import com.paulruiz.forohub.dto.TopicoDTO;
import com.paulruiz.forohub.infra.errores.EntityNotFoundException;
import com.paulruiz.forohub.model.Curso;
import com.paulruiz.forohub.model.Topico;
import com.paulruiz.forohub.model.Usuario;
import com.paulruiz.forohub.repository.CursoRepository;
import com.paulruiz.forohub.repository.TopicoRepository;
import com.paulruiz.forohub.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


// Servicio que contiene la lógica de negocio para Tópicos

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    // ============================================
    // Crear tópico
    // ============================================

    /*
      Crea un nuevo tópico en el sistema
      Valida que no exista duplicado (mismo título y mensaje)

      @param topicoDTO Datos del tópico a crear
      @return Tópico creado
      @throws RuntimeException si el tópico está duplicado
      @throws EntityNotFoundException si el autor o curso no existen
     */
    @Transactional
    public Topico crearTopico(TopicoDTO topicoDTO) {
        // Validar que no exista duplicado
        validarDuplicado(topicoDTO.titulo(), topicoDTO.mensaje());

        // Buscar autor y curso
        Usuario autor = buscarAutor(topicoDTO.autorId());
        Curso curso = buscarCurso(topicoDTO.cursoId());

        // Crear el tópico
        Topico topico = new Topico();
        topico.setTitulo(topicoDTO.titulo());
        topico.setMensaje(topicoDTO.mensaje());
        topico.setAutor(autor);
        topico.setCurso(curso);

        // Guardar y retornar
        return topicoRepository.save(topico);
    }

    // ============================================
    // Listar tópicos
    // ============================================

    /*
      Lista todos los tópicos con paginación

      @param paginacion Configuración de paginación
      @return Página de tópicos
     */
    public Page<Topico> listarTopicos(Pageable paginacion) {
        return topicoRepository.findAll(paginacion);
    }

    // ============================================
    // Obtener tópico por ID
    // ============================================

    /*
      Busca un tópico por su ID

      @param id ID del tópico
      @return Tópico encontrado
      @throws EntityNotFoundException si no existe
     */
    public Topico obtenerTopicoPorId(Long id) {
        return topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tópico con ID " + id + " no encontrado"));
    }

    // ============================================
    // Actualizar tópico
    // ============================================

    /*
      Actualiza un tópico existente
      Valida que no se cree duplicado al actualizar

      @param id ID del tópico a actualizar
      @param actualizarDTO Datos a actualizar
      @return Tópico actualizado
      @throws EntityNotFoundException si el tópico no existe
      @throws RuntimeException si se crea un duplicado
     */
    @Transactional
    public Topico actualizarTopico(Long id, ActualizarTopicoDTO actualizarDTO) {
        // Buscar tópico
        Topico topico = obtenerTopicoPorId(id);

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

    /*
      Elimina un tópico permanentemente

      @param id ID del tópico a eliminar
      @throws EntityNotFoundException si no existe
     */
    @Transactional
    public void eliminarTopico(Long id) {
        Topico topico = obtenerTopicoPorId(id);
        topicoRepository.delete(topico);
    }

    // ============================================
    // Métodos privados de validación
    // ============================================


    // Valida que no exista un tópico con el mismo título y mensaje

    private void validarDuplicado(String titulo, String mensaje) {
        if (topicoRepository.existsByTituloAndMensaje(titulo, mensaje)) {
            throw new RuntimeException("Ya existe un tópico con el mismo título y mensaje");
        }
    }


    // Valida duplicado al actualizar (excluyendo el tópico actual)

    private void validarDuplicadoAlActualizar(String titulo, String mensaje, Long id) {
        if (topicoRepository.existsByTituloAndMensajeAndIdNot(titulo, mensaje, id)) {
            throw new RuntimeException("Ya existe otro tópico con el mismo título y mensaje");
        }
    }


    // Busca un autor por ID

    private Usuario buscarAutor(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + id + " no encontrado"));
    }


    // Busca un curso por ID

    private Curso buscarCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso con ID " + id + " no encontrado"));
    }
}