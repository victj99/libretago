package com.utp.libretago.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa la clave primaria compuesta de la entidad {@link NotificacionGrupo}.
 * <p>
 * Esta clase define los identificadores combinados de una notificación y un grupo, utilizados como clave compuesta para
 * la tabla intermedia <strong>notificacion_grupo</strong>.
 * </p>
 *
 * <p>
 * Es esencial que esta clase implemente {@link Serializable} y sobrescriba correctamente los métodos
 * {@link #equals(Object)} y {@link #hashCode()} para garantizar el funcionamiento adecuado de las operaciones JPA.
 * </p>
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
@Getter
@Setter
public class NotificacionGrupoId implements Serializable {

    /** Identificador de la notificación asociada. */
    private Long notificacionId;

    /** Identificador del grupo asociado. */
    private Long grupoId;

    /** Constructor por defecto requerido por JPA. */
    public NotificacionGrupoId() {
    }

    /**
     * Constructor que inicializa los identificadores de notificación y grupo.
     *
     * @param notificacionId identificador de la notificación.
     * @param grupoId        identificador del grupo.
     */
    public NotificacionGrupoId(Long notificacionId, Long grupoId) {
        this.notificacionId = notificacionId;
        this.grupoId = grupoId;
    }

    /**
     * Compara dos objetos {@link NotificacionGrupoId} para determinar si son iguales.
     * <p>
     * Dos claves son iguales si tienen el mismo {@code notificacionId} y {@code grupoId}.
     * </p>
     *
     * @param o objeto a comparar.
     * @return {@code true} si ambos objetos representan la misma clave, de lo contrario {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NotificacionGrupoId that = (NotificacionGrupoId) o;
        return Objects.equals(notificacionId, that.notificacionId) && Objects.equals(grupoId, that.grupoId);
    }

    /**
     * Calcula el código hash de la clave compuesta.
     * <p>
     * Este método asegura la correcta identificación del objeto en estructuras de datos basadas en hash, como
     * {@link java.util.HashMap}.
     * </p>
     *
     * @return valor hash basado en los identificadores {@code notificacionId} y {@code grupoId}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(notificacionId, grupoId);
    }
}
