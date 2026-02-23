package com.paulruiz.forohub.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entidad que representa un usuario del sistema
 * Implementa UserDetails de Spring Security para autenticación
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Relación: Un usuario puede tener múltiples perfiles (ADMIN, USER, etc.)
    @ManyToMany(fetch = FetchType.EAGER) // EAGER: carga los perfiles siempre (necesario para auth)
    @JoinTable(
            name = "usuarios_perfiles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "perfil_id")
    )
    private Set<Perfil> perfiles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    // ============================================
    // MÉTODOS DE UserDetails (Spring Security)
    // ============================================

    /**
     * Retorna los roles/permisos del usuario
     * Spring Security usa esto para autorización
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return perfiles.stream()
                .map(perfil -> new SimpleGrantedAuthority(perfil.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return contrasena; // Spring Security busca el password aquí
    }

    @Override
    public String getUsername() {
        return email; // Usamos email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta no expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta no se bloquea
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales no expiran
    }

    @Override
    public boolean isEnabled() {
        return activo; // Usuario activo = habilitado
    }
}