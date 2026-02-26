package com.paulruiz.forohub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/*
 Entidad que representa una respuesta a un tópico
 Relaciones:
 - ManyToOne con Topico
 - ManyToOne con Usuario (autor)
 */

@Entity
@Table(name = "respuestas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private Boolean solucion = false;  // Indica si esta respuesta es la solución

    // Relación: Una respuesta pertenece a un tópico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topico_id", nullable = false)
    private Topico topico;

    // Relación: Una respuesta tiene un autor (Usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    // Método ejecutado automáticamente antes de persistir

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (solucion == null) {
            solucion = false;
        }
    }
}