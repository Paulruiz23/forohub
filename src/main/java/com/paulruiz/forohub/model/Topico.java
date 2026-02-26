package com.paulruiz.forohub.model;

import com.paulruiz.forohub.dto.ActualizarTopicoDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/*
 Entidad que representa un tópico/pregunta del foro
 Relaciones:
 - ManyToOne con Usuario (autor del tópico)
 - ManyToOne con Curso (curso al que pertenece)
 */

@Entity
@Table(name = "topicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Status del tópico: NO_RESPONDIDO, NO_SOLUCIONADO, SOLUCIONADO, CERRADO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusTopico status;

    // Relación: Un tópico tiene un autor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    // Relación: Un tópico pertenece a un curso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    // ============================================
    // Asignar valores por defecto antes de guardar
    // ============================================

    /*
     Método ejecutado antes de persistir
     Asigna fecha de creación y status NO_RESPONDIDO por defecto
     */

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusTopico.NO_RESPONDIDO;
        }
    }

    // ============================================
    // Actualizar datos del tópico
    // ============================================

    /*
     Actualiza título, mensaje y/o curso
     Solo modifica campos que no sean null

     @param datos DTO con los nuevos datos
     @param curso Nuevo curso (si cambió)
     */

    public void actualizarDatos(ActualizarTopicoDTO datos, Curso curso) {
        if (datos.titulo() != null) {
            this.titulo = datos.titulo();
        }
        if (datos.mensaje() != null) {
            this.mensaje = datos.mensaje();
        }
        if (curso != null) {
            this.curso = curso;
        }
    }

    // ============================================
    // Actualizar status según respuestas
    // ============================================

    /*
     Actualiza el status automáticamente según respuestas:
     - NO_RESPONDIDO: Sin respuestas
     - NO_SOLUCIONADO: Con respuestas pero sin solución
     - SOLUCIONADO: Con respuesta marcada como solución

     @param tieneRespuestas Si el tópico tiene respuestas
     @param tieneSolucion Si hay una respuesta marcada como solución
     */

    public void actualizarStatus(boolean tieneRespuestas, boolean tieneSolucion) {
        if (tieneSolucion) {
            this.status = StatusTopico.SOLUCIONADO;
        } else if (tieneRespuestas) {
            this.status = StatusTopico.NO_SOLUCIONADO;
        } else {
            this.status = StatusTopico.NO_RESPONDIDO;
        }
    }
}