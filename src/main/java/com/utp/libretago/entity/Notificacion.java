package com.utp.libretago.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.utp.libretago.classes.dto.IdLabelDTO;
import com.utp.libretago.classes.dto.NotificacionDTO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa una notificación dentro del sistema Libretago.
 * <p>
 * Las notificaciones son mensajes o avisos generados por un usuario creador
 * y dirigidos a uno o varios grupos. También pueden ser evaluadas por otro
 * usuario (evaluador).
 * </p>
 *
 * <p>
 * La entidad gestiona estados de aprobación, fechas de evaluación y métodos
 * auxiliares para convertir la información a objetos DTO.
 * </p>
 *
 * @see Usuario
 * @see Grupo
 * @see NotificacionDTO
 * @see IdLabelDTO
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Setter
@Table(name = "notificacion")
public class Notificacion {

    // -------------------------------------------------------------------------
    // Constantes de estado
    // -------------------------------------------------------------------------

    /** Estado pendiente: la notificación aún no ha sido evaluada. */
    public static final String ESTADO_PENDIENTE = "P";

    /** Estado aprobado: la notificación fue aceptada o confirmada. */
    public static final String ESTADO_APROBADO = "A";

    /** Estado rechazado: la notificación fue denegada o descartada. */
    public static final String ESTADO_RECHAZADO = "R";

    // -------------------------------------------------------------------------
    // Atributos principales
    // -------------------------------------------------------------------------

    /** Identificador único de la notificación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título de la notificación. */
    @Column(nullable = false, length = 255)
    private String titulo;

    /** Detalle o contenido del mensaje de la notificación. */
    @Column(nullable = false, columnDefinition = "text")
    private String detalle;

    /**
     * Usuario que creó la notificación.
     * <p>
     * Relación muchos a uno con {@link Usuario}. Es obligatorio.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;

    /**
     * Grupos destinatarios de la notificación.
     * <p>
     * Relación muchos a muchos mediante la tabla intermedia
     * <strong>notificacion_grupo</strong>.
     * </p>
     */
    @ManyToMany
    @JoinTable(
        name = "notificacion_grupo",
        joinColumns = @JoinColumn(name = "notificacion_id"),
        inverseJoinColumns = @JoinColumn(name = "grupo_id")
    )
    private Set<Grupo> grupos = new HashSet<>();

    /**
     * Estado actual de la notificación.
     * <p>
     * Puede ser:
     * <ul>
     *   <li><strong>P</strong> – Pendiente</li>
     *   <li><strong>A</strong> – Aprobado</li>
     *   <li><strong>R</strong> – Rechazado</li>
     * </ul>
     * Valor por defecto: <strong>P</strong>.
     * </p>
     */
    @Column(length = 1, nullable = false)
    private String estado = ESTADO_PENDIENTE;

    /** Usuario evaluador asignado (opcional). */
    @ManyToOne
    @JoinColumn(name = "usuario_evaluador_id")
    private Usuario usuarioEvaluador;

    /** Fecha en la que la notificación fue evaluada. */
    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;

    /** Indica si la notificación está activa. Por defecto es {@code true}. */
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha y hora de creación de la notificación. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // -------------------------------------------------------------------------
    // Ciclo de vida
    // -------------------------------------------------------------------------

    /**
     * Inicializa los valores por defecto antes de persistir el registro.
     * <p>
     * Se ejecuta automáticamente al insertar una nueva notificación.
     * </p>
     */
    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares (DTOs)
    // -------------------------------------------------------------------------

    /**
     * Convierte la entidad {@link Notificacion} en un objeto {@link NotificacionDTO}.
     * <p>
     * Incluye información básica del creador y, si existe, del evaluador.
     * </p>
     *
     * @return un objeto {@link NotificacionDTO} con los datos principales.
     */
    public NotificacionDTO obtenerNotificacionDTO() {
        NotificacionDTO notificacionDTO = new NotificacionDTO();
        notificacionDTO.setId(id);
        notificacionDTO.setTitulo(titulo);
        notificacionDTO.setDetalle(detalle);
        notificacionDTO.setUsuarioCreadorId(usuarioCreador.getId());
        notificacionDTO.setUsuarioCreadorNombre(usuarioCreador.getNombreCompleto());
        notificacionDTO.setEstado(estado);

        if (usuarioEvaluador != null) {
            notificacionDTO.setUsuarioEvaluadorId(usuarioEvaluador.getId());
            notificacionDTO.setUsuarioEvaluadorNombre(usuarioEvaluador.getNombreCompleto());
        }

        notificacionDTO.setFechaEvaluacion(fechaEvaluacion);
        notificacionDTO.setActivo(activo);

        return notificacionDTO;
    }

    /**
     * Convierte la entidad {@link Notificacion} en un {@link NotificacionDTO}
     * incluyendo también la lista de grupos asociados.
     *
     * @return un {@link NotificacionDTO} con información completa (notificación + grupos).
     */
    public NotificacionDTO obtenerNotificacionConGruposDTO() {
        NotificacionDTO notificacionDTO = obtenerNotificacionDTO();

        if (grupos != null && !grupos.isEmpty()) {
            for (Grupo item : grupos) {
                notificacionDTO.getGrupos().add(new IdLabelDTO(item.getId(), item.getNombre()));
            }
        }

        return notificacionDTO;
    }
}
