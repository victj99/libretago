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

@Entity
@Table(name = "alumno_grupo")
@IdClass(AlumnoGrupoId.class)
@Getter
@Setter
public class AlumnoGrupo {

    @Id
    @Column(name = "alumno_id")
    private Long alumnoId;

    @Id
    @Column(name = "grupo_id")
    private Long grupoId;

    @ManyToOne
    @JoinColumn(name = "alumno_id", insertable = false, updatable = false)
    @JsonIgnore
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "grupo_id", insertable = false, updatable = false)
    @JsonIgnore
    private Grupo grupo;

    public AlumnoGrupo() {
    }

    public AlumnoGrupo(Long alumnoId, Long grupoId) {
        this.alumnoId = alumnoId;
        this.grupoId = grupoId;
    }
}