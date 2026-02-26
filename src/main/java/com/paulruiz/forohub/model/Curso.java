package com.paulruiz.forohub.model;

import jakarta.persistence.*;
import lombok.*;

/*
 Entidad que representa un curso/categoría del foro
 Un tópico pertenece a un curso (ejem: Spring Boot, React, MySQL)
*/

@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;
}