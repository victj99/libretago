package com.utp.libretago.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa la relación entre {@link Notificacion} y {@link Grupo}.
 * <p>
 * Corresponde a la tabla intermedia <strong>notificacion_grupo</strong>, que asocia múltiples notificaciones con
 * múltiples grupos (relación muchos a muchos).
 * </p>
 *
 * <p>
 * Utiliza {@link NotificacionGrupoId} como clave compuesta para definir la relación entre ambos identificadores.
 * </p>
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
@Entity
@Table(name = "notificacion_grupo")
@IdClass(NotificacionGrupoId.class)
@Getter
@Setter
public class NotificacionGrupo {

    /** Identificador de la notificación asociada. */
    @Id
    @Column(name = "notificacion_id")
    private Long notificacionId;

    /** Identificador del grupo asociado. */
    @Id
    @Column(name = "grupo_id")
    private Long grupoId;

    /**
     * Relación con la entidad {@link Notificacion}.
     * <p>
     * Está marcada con {@link JsonIgnore} para evitar recursividad en la serialización JSON.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "notificacion_id", insertable = false, updatable = false)
    @JsonIgnore
    private Notificacion notificacion;

    /**
     * Relación con la entidad {@link Grupo}.
     * <p>
     * También ignorada en JSON para prevenir ciclos de referencia.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "grupo_id", insertable = false, updatable = false)
    @JsonIgnore
    private Grupo grupo;

    /** Constructor por defecto requerido por JPA. */
    public NotificacionGrupo() {
    }

    /**
     * Constructor que permite crear la asociación entre una notificación y un grupo.
     *
     * @param notificacionId identificador de la notificación.
     * @param grupoId        identificador del grupo.
     */
    public NotificacionGrupo(Long notificacionId, Long grupoId) {
        this.notificacionId = notificacionId;
        this.grupoId = grupoId;
    }
}
