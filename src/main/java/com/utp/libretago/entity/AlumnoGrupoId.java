package com.utp.libretago.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlumnoGrupoId implements Serializable {

    private Long alumnoId;
    private Long grupoId;

    public AlumnoGrupoId() {
    }

    public AlumnoGrupoId(Long alumnoId, Long grupoId) {
        this.alumnoId = alumnoId;
        this.grupoId = grupoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AlumnoGrupoId that = (AlumnoGrupoId) o;
        return Objects.equals(alumnoId, that.alumnoId) && Objects.equals(grupoId, that.grupoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alumnoId, grupoId);
    }
}
