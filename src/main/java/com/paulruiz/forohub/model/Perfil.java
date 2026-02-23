package com.paulruiz.forohub.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un rol/perfil de usuario
 * Ejemplos: ROLE_USER, ROLE_ADMIN, ROLE_MODERADOR
 */
@Entity
@Table(name = "perfiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;
}