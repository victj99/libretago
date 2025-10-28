package com.utp.libretago.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa a un usuario dentro del sistema Libretago.
 * <p>
 * Los usuarios pueden tener diferentes roles (como colegio, profesor o apoderado)
 * y están asociados a distintas entidades del sistema, tales como eventos,
 * notificaciones y grupos.
 * </p>
 *
 * <p>
 * Incluye información de autenticación, datos personales y estado de actividad.
 * </p>
 *
 * @see Rol
 * @see TokenDispositivo
 * @see Evento
 * @see Notificacion
 * @see Grupo
 * @see Alumno
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Setter
@Table(name = "usuario")
public class Usuario {

    /** Identificador único del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario utilizado para autenticación.
     * <p>
     * Debe ser único y tener un máximo de 50 caracteres.
     * </p>
     */
    @Column(name = "nombre_usuario", nullable = false, length = 50, unique = true)
    private String nombreUsuario;

    /** Nombre completo del usuario. */
    @Column(name = "nombre_completo", nullable = false, length = 255)
    private String nombreCompleto;

    /** Correo electrónico del usuario (opcional). */
    @Column(nullable = true, length = 255)
    private String email;

    /** Número telefónico de contacto (opcional). */
    @Column(nullable = true, length = 20)
    private String telefono;

    /**
     * Contraseña cifrada del usuario.
     * <p>
     * Marcada con {@link JsonIgnore} para evitar su exposición en respuestas JSON.
     * </p>
     */
    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String contrasenia;

    /**
     * Roles asignados al usuario.
     * <p>
     * Relación muchos a muchos con {@link Rol} mediante la tabla intermedia
     * <strong>usuario_rol</strong>.
     * </p>
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    /** Indica si el usuario está activo en el sistema. Por defecto es {@code true}. */
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha y hora en la que el usuario fue registrado en el sistema. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Inicializa los valores por defecto antes de persistir el usuario.
     * <p>
     * Se ejecuta automáticamente al insertar un nuevo registro.
     * </p>
     */
    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    /** Constructor vacío requerido por JPA. */
    public Usuario() {
    }

    /**
     * Constructor que permite inicializar un usuario con su identificador.
     *
     * @param id identificador único del usuario.
     */
    public Usuario(Long id) {
        this.id = id;
    }
}
