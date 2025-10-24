package com.utp.libretago.classes.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.jspecify.annotations.NonNull;

import com.utp.libretago.entity.Grupo;
import com.utp.libretago.entity.Notificacion;
import com.utp.libretago.entity.Usuario;
import com.utp.libretago.utils.MensajesValidacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificacionDTO {

    Long id;

    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    String titulo;

    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    String detalle;

    @NotEmpty(message = MensajesValidacion.CAMPO_REQUERIDO)
    Set<@NonNull IdLabelDTO> grupos = new HashSet<>();

    Long usuarioCreadorId;
    String usuarioCreadorNombre;

    String estado;

    Long usuarioEvaluadorId;
    String usuarioEvaluadorNombre;

    LocalDateTime fechaEvaluacion;
    Boolean activo;

    public NotificacionDTO() {
    }

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

        if (grupos != null) {
            for (IdLabelDTO item : grupos) {
                notificacion.getGrupos().add(new Grupo(item.value()));
            }
        }

        if (estado != null) {
            notificacion.setEstado(estado);
        }

        notificacion.setActivo(activo);

        return notificacion;
    }
}
