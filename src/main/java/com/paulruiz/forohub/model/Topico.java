package com.paulruiz.forohub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un tópico/pregunta del foro
 * Relaciones:
 * - ManyToOne con Usuario (autor)
 * - ManyToOne con Curso
 * - OneToMany con Respuesta (no implementado aún)
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

    @Enumerated(EnumType.STRING) // Guarda el nombre del enum (NO_RESPONDIDO) en la BD
    @Column(nullable = false, length = 50)
    private StatusTopico status;

    // Relación: Un tópico pertenece a un autor (Usuario)
    @ManyToOne(fetch = FetchType.LAZY) // LAZY: carga el usuario solo cuando lo necesites
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    // Relación: Un tópico pertenece a un curso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    /**
     * Método ejecutado automáticamente antes de persistir en BD
     * Asigna valores por defecto
     */
    @PrePersist
    public void prePersist() {
        // Si no se especifica fecha, usar la actual
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        // Si no se especifica status, usar NO_RESPONDIDO
        if (status == null) {
            status = StatusTopico.NO_RESPONDIDO;
        }
    }
}