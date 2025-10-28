package com.utp.libretago.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa la relación entre {@link Alumno} y {@link Grupo}.
 * <p>
 * Corresponde a la tabla intermedia <strong>alumno_grupo</strong>, que asocia
 * múltiples alumnos con múltiples grupos (relación muchos a muchos).
 * </p>
 *
 * <p>
 * Utiliza {@link AlumnoGrupoId} como clave compuesta para definir la relación
 * entre ambos identificadores.
 * </p>
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Table(name = "alumno_grupo")
@IdClass(AlumnoGrupoId.class)
@Getter
@Setter
public class AlumnoGrupo {

    /** Identificador del alumno asociado. */
    @Id
    @Column(name = "alumno_id")
    private Long alumnoId;

    /** Identificador del grupo asociado. */
    @Id
    @Column(name = "grupo_id")
    private Long grupoId;

    /**
     * Relación con la entidad {@link Alumno}.
     * <p>
     * Está marcada con {@link JsonIgnore} para evitar recursividad en la
     * serialización JSON.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "alumno_id", insertable = false, updatable = false)
    @JsonIgnore
    private Alumno alumno;

    /**
     * Relación con la entidad {@link Grupo}.
     * <p>
     * También ignorada en JSON para prevenir ciclos de referencia.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "grupo_id", insertable = false, updatable = false)
    @JsonIgnore
    private Grupo grupo;

    /** Constructor por defecto requerido por JPA. */
    public AlumnoGrupo() {
    }

    /**
     * Constructor que permite crear la asociación entre un alumno y un grupo.
     *
     * @param alumnoId identificador del alumno.
     * @param grupoId identificador del grupo.
     */
    public AlumnoGrupo(Long alumnoId, Long grupoId) {
        this.alumnoId = alumnoId;
        this.grupoId = grupoId;
    }
}
