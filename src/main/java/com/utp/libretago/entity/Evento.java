package com.utp.libretago.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * Entidad que representa un evento dentro del sistema Libretago.
 * <p>
 * Los eventos son actividades o acontecimientos creados por un usuario (creador),
 * que pueden estar asociados a uno o varios grupos de alumnos.
 * </p>
 *
 * <p>
 * La entidad gestiona información como el título, descripción, fecha del evento,
 * estado, evaluador asignado y fechas de creación o evaluación.
 * </p>
 *
 * <p>
 * Se relaciona con las entidades {@link Usuario} y {@link Grupo}.
 * </p>
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Table(name = "evento")
public class Evento {

    /** Identificador único del evento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título descriptivo del evento. */
    @Column(nullable = false, length = 255)
    private String titulo;

    /** Detalle o descripción completa del evento. */
    @Column(nullable = false, columnDefinition = "text")
    private String detalle;

    /** Fecha en la que se realizará el evento. */
    @Column(name = "fecha_evento", nullable = false)
    private LocalDate fechaEvento;

    /**
     * Usuario que creó el evento.
     * <p>
     * Relación muchos a uno con {@link Usuario}. No puede ser nula.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;

    /**
     * Grupos asociados al evento.
     * <p>
     * Relación muchos a muchos mediante la tabla intermedia
     * <strong>evento_grupo</strong>.
     * </p>
     */
    @ManyToMany
    @JoinTable(
        name = "evento_grupo",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "grupo_id")
    )
    private Set<Grupo> grupos = new HashSet<>();

    /**
     * Estado del evento.
     * <p>
     * Puede ser:
     * <ul>
     *   <li><strong>P</strong> – Pendiente</li>
     *   <li><strong>E</strong> – Evaluado</li>
     *   <li><strong>C</strong> – Cancelado</li>
     * </ul>
     * Valor por defecto: <strong>P</strong>.
     * </p>
     */
    @Column(length = 1, nullable = false)
    private String estado = "P";

    /**
     * Usuario asignado como evaluador del evento (opcional).
     */
    @ManyToOne
    @JoinColumn(name = "usuario_evaluador_id")
    private Usuario usuarioEvaluador;

    /** Fecha en la que el evento fue evaluado (si aplica). */
    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;

    /** Indica si el evento está activo. Por defecto es {@code true}. */
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha y hora en la que se registró el evento. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
