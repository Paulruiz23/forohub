package com.paulruiz.forohub.model;

import com.paulruiz.forohub.dto.ActualizarTopicoDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusTopico status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

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
    // MÉTODO NUEVO - Para actualizar datos
    // ============================================

    /**
     * Actualiza los datos del tópico
     * Solo actualiza los campos que no sean null
     *
     * @param datos DTO con los nuevos datos
     * @param curso Nuevo curso (si se cambió)
     */
    public void actualizarDatos(ActualizarTopicoDTO datos, Curso curso) {
        // Actualizar solo si se enviaron nuevos datos
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

    /**
     * Actualiza el status del tópico según las respuestas
     * - NO_RESPONDIDO: Sin respuestas
     * - NO_SOLUCIONADO: Con respuestas pero sin solución
     * - SOLUCIONADO: Con respuesta marcada como solución
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