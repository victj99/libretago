package com.utp.libretago.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa la clave primaria compuesta de la entidad {@link UsuarioInstitucion}.
 * <p>
 * Define los identificadores combinados de {@code usuarioColegioId} e
 * {@code institucionEducativaId}, que juntos conforman la clave única
 * en la tabla intermedia <strong>usuario_institucion</strong>.
 * </p>
 *
 * <p>
 * Implementa {@link Serializable} y sobrescribe correctamente
 * {@link #equals(Object)} y {@link #hashCode()} para garantizar el correcto
 * funcionamiento de la persistencia con JPA.
 * </p>
 *
 * @see UsuarioInstitucion
 * @see Usuario
 * @see InstitucionEducativa
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Getter
@Setter
public class UsuarioInstitucionId implements Serializable {

    /** Identificador de la institución educativa asociada. */
    private Long institucionEducativaId;

    /** Identificador del usuario colegio asociado. */
    private Long usuarioColegioId;

    /** Constructor vacío requerido por JPA. */
    public UsuarioInstitucionId() {
    }

    /**
     * Constructor que inicializa los campos de la clave compuesta.
     *
     * @param institucionEducativaId identificador de la institución educativa.
     * @param usuarioColegioId identificador del usuario colegio.
     */
    public UsuarioInstitucionId(Long institucionEducativaId, Long usuarioColegioId) {
        this.institucionEducativaId = institucionEducativaId;
        this.usuarioColegioId = usuarioColegioId;
    }

    /**
     * Compara dos objetos {@link UsuarioInstitucionId} para determinar si son iguales.
     * <p>
     * Dos claves son iguales si tienen el mismo {@code institucionEducativaId}
     * y {@code usuarioColegioId}.
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
        UsuarioInstitucionId that = (UsuarioInstitucionId) o;
        return Objects.equals(institucionEducativaId, that.institucionEducativaId)
                && Objects.equals(usuarioColegioId, that.usuarioColegioId);
    }

    /**
     * Calcula el código hash de la clave compuesta.
     * <p>
     * Permite la correcta identificación del objeto en colecciones basadas en hash,
     * como {@link java.util.HashSet} o {@link java.util.HashMap}.
     * </p>
     *
     * @return valor hash calculado en base a los identificadores.
     */
    @Override
    public int hashCode() {
        return Objects.hash(institucionEducativaId, usuarioColegioId);
    }
}
