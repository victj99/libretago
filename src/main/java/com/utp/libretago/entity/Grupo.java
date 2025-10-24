package com.utp.libretago.entity;

import java.time.LocalDateTime;

import com.utp.libretago.classes.dto.GrupoDTO;
import com.utp.libretago.classes.dto.IdLabelDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(name = "institucion_educativa_id")
    private Long institucionEducativaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_educativa_id", nullable = false, insertable = false, updatable = false)
    private InstitucionEducativa institucionEducativa;

    @Column(name = "usuario_profesor_id")
    private Long usuarioProfesorId;

    @ManyToOne
    @JoinColumn(name = "usuario_profesor_id", insertable = false, updatable = false)
    private Usuario usuarioProfesor;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    public Grupo() {
    }

    public Grupo(Long id) {
        this.id = id;
    }

    public GrupoDTO obtenerGrupoDTO() {
        String nombreProfesor = null;
        if (usuarioProfesor != null) {
            nombreProfesor = usuarioProfesor.getNombreCompleto();
        }

        return new GrupoDTO(id, nombre, null, null, institucionEducativaId, usuarioProfesorId, activo, nombreProfesor);
    }

    public IdLabelDTO obtenerIdLabelDTO() {
        return new IdLabelDTO(id, nombre);
    }

}