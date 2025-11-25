package com.utp.libretago.classes.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.jspecify.annotations.NonNull;

import com.utp.libretago.entity.Notificacion;
import com.utp.libretago.entity.Usuario;
import com.utp.libretago.utils.MensajesValidacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) que representa una notificación del sistema.
 * <p>
 * Contiene toda la información relacionada con una notificación, incluyendo su título,
 * detalle, grupos destinatarios, usuario creador, estado de aprobación y metadatos de gestión.
 * </p>
 * <p>
 * Este DTO se utiliza para transferir información de notificaciones entre las capas de presentación,
 * servicio y persistencia, abstrayendo la complejidad de las entidades JPA.
 * </p>
 *
 * @see com.utp.libretago.entity.Notificacion
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
@Getter
@Setter
public class NotificacionDTO {

    /** Identificador único de la notificación. */
    Long id;

    /** Título de la notificación. Campo obligatorio con longitud máxima de 255 caracteres. */
    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    String titulo;

    /** Detalle o contenido completo de la notificación. Campo obligatorio. */
    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    String detalle;

    /** 
     * Conjunto de grupos destinatarios de la notificación.
     * Cada elemento contiene el ID y nombre del grupo. Campo obligatorio (al menos un grupo).
     */
    @NotEmpty(message = MensajesValidacion.CAMPO_REQUERIDO)
    Set<@NonNull IdLabelDTO> grupos = new HashSet<>();

    /** Identificador del usuario que creó la notificación. */
    Long usuarioCreadorId;
    
    /** Nombre completo del usuario creador. */
    String usuarioCreadorNombre;

    /** 
     * Estado de la notificación.
     * Valores posibles: "P" (Pendiente), "A" (Aprobado), "R" (Rechazado).
     * @see com.utp.libretago.entity.Notificacion#ESTADO_PENDIENTE
     * @see com.utp.libretago.entity.Notificacion#ESTADO_APROBADO
     * @see com.utp.libretago.entity.Notificacion#ESTADO_RECHAZADO
     */
    String estado;

    /** Identificador del usuario que evaluó la notificación (aprobador o rechazador). */
    Long usuarioEvaluadorId;
    
    /** Nombre completo del usuario evaluador. */
    String usuarioEvaluadorNombre;

    /** Fecha y hora en que se evaluó la notificación. */
    LocalDateTime fechaEvaluacion;
    
    /** Indica si la notificación está activa (true) o inactiva (false). */
    Boolean activo;

    /** Constructor vacío requerido para frameworks como Spring y serialización JSON. */
    public NotificacionDTO() {
    }

    /**
     * Convierte este DTO en una entidad {@link Notificacion} para su persistencia.
     * <p>
     * Este método realiza el mapeo de los campos del DTO hacia una instancia de la entidad {@link Notificacion},
     * estableciendo las relaciones con {@link Usuario} cuando corresponde.
     * </p>
     * <p>
     * <b>Nota:</b> Este método no mapea los grupos asociados, ya que esa relación se gestiona
     * mediante la tabla intermedia NotificacionGrupo en la capa de servicio.
     * </p>
     *
     * @return instancia de {@link Notificacion} con los datos de este DTO
     */
    public Notificacion obtenerNotificacion() {
        var notificacion = new Notificacion();
        notificacion.setId(id);
        notificacion.setTitulo(titulo);
        notificacion.setDetalle(detalle);

        if (usuarioEvaluadorId != null) {
            notificacion.setUsuarioEvaluador(new Usuario(usuarioEvaluadorId));
        }

        if (usuarioCreadorId != null) {
            notificacion.setUsuarioCreador(new Usuario(usuarioCreadorId));
        }

        if (estado != null) {
            notificacion.setEstado(estado);
        }

        notificacion.setActivo(activo);

        return notificacion;
    }
}
