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
 * Entidad que representa la relación entre {@link Evento} y {@link Grupo}.
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
@Entity
@Table(name = "evento_grupo")
@IdClass(EventoGrupoId.class)
@Getter
@Setter
public class EventoGrupo {

    @Id
    @Column(name = "evento_id")
    private Long eventoId;

    @Id
    @Column(name = "grupo_id")
    private Long grupoId;

    @ManyToOne
    @JoinColumn(name = "evento_id", insertable = false, updatable = false)
    @JsonIgnore
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "grupo_id", insertable = false, updatable = false)
    @JsonIgnore
    private Grupo grupo;

    public EventoGrupo() {
    }

    public EventoGrupo(Long eventoId, Long grupoId) {
        this.eventoId = eventoId;
        this.grupoId = grupoId;
    }
}
