package com.utp.libretago.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa la clave primaria compuesta de la entidad {@link AlumnoGrupo}.
 * <p>
 * Esta clase define los identificadores combinados de un alumno y un grupo,
 * utilizados como clave compuesta para la tabla intermedia
 * <strong>alumno_grupo</strong>.
 * </p>
 *
 * <p>
 * Es esencial que esta clase implemente {@link Serializable} y sobrescriba
 * correctamente los métodos {@link #equals(Object)} y {@link #hashCode()} para
 * garantizar el funcionamiento adecuado de las operaciones JPA.
 * </p>
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Getter
@Setter
public class AlumnoGrupoId implements Serializable {

    /** Identificador del alumno asociado. */
    private Long alumnoId;

    /** Identificador del grupo asociado. */
    private Long grupoId;

    /** Constructor por defecto requerido por JPA. */
    public AlumnoGrupoId() {
    }

    /**
     * Constructor que inicializa los identificadores de alumno y grupo.
     *
     * @param alumnoId identificador del alumno.
     * @param grupoId identificador del grupo.
     */
    public AlumnoGrupoId(Long alumnoId, Long grupoId) {
        this.alumnoId = alumnoId;
        this.grupoId = grupoId;
    }

    /**
     * Compara dos objetos {@link AlumnoGrupoId} para determinar si son iguales.
     * <p>
     * Dos claves son iguales si tienen el mismo {@code alumnoId} y {@code grupoId}.
     * </p>
     *
     * @param o objeto a comparar.
     * @return {@code true} si ambos objetos representan la misma clave, de lo contrario {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AlumnoGrupoId that = (AlumnoGrupoId) o;
        return Objects.equals(alumnoId, that.alumnoId) &&
               Objects.equals(grupoId, that.grupoId);
    }

    /**
     * Calcula el código hash de la clave compuesta.
     * <p>
     * Este método asegura la correcta identificación del objeto en estructuras
     * de datos basadas en hash, como {@link java.util.HashMap}.
     * </p>
     *
     * @return valor hash basado en los identificadores {@code alumnoId} y {@code grupoId}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(alumnoId, grupoId);
    }
}
