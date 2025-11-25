package com.utp.libretago.classes.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.jspecify.annotations.NonNull;

import com.utp.libretago.entity.Evento;
import com.utp.libretago.entity.Usuario;
import com.utp.libretago.utils.MensajesValidacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) que representa un evento del sistema.
 * <p>
 * Contiene toda la información relacionada con un evento, incluyendo su título, detalle, fecha programada, grupos
 * asociados, usuario creador, estado de aprobación y metadatos de gestión.
 * </p>
 * <p>
 * Este DTO se utiliza para transferir información de eventos entre las capas de presentación, servicio y persistencia,
 * abstrayendo la complejidad de las entidades JPA.
 * </p>
 *
 * @see com.utp.libretago.entity.Evento
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
@Getter
@Setter
public class EventoDTO {

    Long id;

    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    String titulo;

    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    String detalle;

    @NotNull(message = MensajesValidacion.CAMPO_REQUERIDO)
    LocalDateTime fechaEvento;

    @NotEmpty(message = MensajesValidacion.CAMPO_REQUERIDO)
    Set<@NonNull IdLabelDTO> grupos = new HashSet<>();

    Long usuarioCreadorId;
    String usuarioCreadorNombre;

    String estado;

    Long usuarioEvaluadorId;
    String usuarioEvaluadorNombre;

    LocalDateTime fechaEvaluacion;
    Boolean activo;

    public EventoDTO() {
    }

    /**
     * Convierte este DTO en una entidad {@link Evento} para su persistencia.
     * <p>
     * Este método realiza el mapeo de los campos del DTO hacia una instancia de la entidad {@link Evento}, estableciendo
     * las relaciones con {@link Usuario} cuando corresponde.
     * </p>
     *
     * @return instancia de {@link Evento} con los datos de este DTO
     */
    public Evento obtenerEvento() {
        var evento = new Evento();
        evento.setId(id);
        evento.setTitulo(titulo);
        evento.setDetalle(detalle);
        evento.setFechaEvento(fechaEvento);

        if (usuarioEvaluadorId != null) {
            evento.setUsuarioEvaluador(new Usuario(usuarioEvaluadorId));
        }

        if (usuarioCreadorId != null) {
            evento.setUsuarioCreador(new Usuario(usuarioCreadorId));
        }

        if (estado != null) {
            evento.setEstado(estado);
        }

        evento.setActivo(activo);

        return evento;
    }
}
