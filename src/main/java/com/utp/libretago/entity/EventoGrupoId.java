package com.utp.libretago.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Clave compuesta para la entidad {@link EventoGrupo}.
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
@Getter
@Setter
public class EventoGrupoId implements Serializable {

    private Long eventoId;
    private Long grupoId;

    public EventoGrupoId() {
    }

    public EventoGrupoId(Long eventoId, Long grupoId) {
        this.eventoId = eventoId;
        this.grupoId = grupoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventoGrupoId that = (EventoGrupoId) o;
        return Objects.equals(eventoId, that.eventoId) &&
               Objects.equals(grupoId, that.grupoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventoId, grupoId);
    }
}
