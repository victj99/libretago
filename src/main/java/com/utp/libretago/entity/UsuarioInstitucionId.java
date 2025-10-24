package com.utp.libretago.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioInstitucionId implements Serializable {
    private Long institucionEducativaId;
    private Long usuarioColegioId;

    public UsuarioInstitucionId() {
    }

    public UsuarioInstitucionId(Long institucionEducativaId, Long usuarioColegioId) {
        this.institucionEducativaId = institucionEducativaId;
        this.usuarioColegioId = usuarioColegioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UsuarioInstitucionId that = (UsuarioInstitucionId) o;
        return Objects.equals(institucionEducativaId, that.institucionEducativaId) && Objects.equals(usuarioColegioId, that.usuarioColegioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(institucionEducativaId, usuarioColegioId);
    }
}