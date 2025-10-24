package com.utp.libretago.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;

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
@Getter
@Setter
@Table(name = "usuario_institucion")
@IdClass(UsuarioInstitucionId.class)
public class UsuarioInstitucion {

    @Id
    @Column(name = "usuario_colegio_id")
    private Long usuarioColegioId;

    @Id
    @Column(name = "institucion_educativa_id")
    private Long institucionEducativaId;

    @ManyToOne
    @JoinColumn(name = "usuario_colegio_id", insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuarioColegio;

    @ManyToOne
    @JoinColumn(name = "institucion_educativa_id", insertable = false, updatable = false)
    @JsonIgnore
    private InstitucionEducativa institucionEducativa;

    public UsuarioInstitucion() {
    }

    public UsuarioInstitucion(Long usuarioColegioId, Long institucionEducativaId) {
        this.usuarioColegioId = usuarioColegioId;
        this.institucionEducativaId = institucionEducativaId;
    }

    public UsuarioInstitucionDTO obtenerUsuarioInstitucionDTO() {
        return new UsuarioInstitucionDTO(usuarioColegioId, //
                usuarioColegio.getNombreUsuario(), //
                usuarioColegio.getNombreCompleto(), //
                usuarioColegio.getEmail(), //
                usuarioColegio.getTelefono(), //
                usuarioColegio.getActivo(), //
                institucionEducativa.getId(), //
                institucionEducativa.getNombre()//
        );
    }
}