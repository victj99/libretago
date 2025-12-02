package com.utp.libretago.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa la relación entre un {@link Usuario} (tipo colegio)
 * y una {@link InstitucionEducativa} dentro del sistema Libretago.
 * <p>
 * Corresponde a la tabla intermedia <strong>usuario_institucion</strong>,
 * la cual define qué colegios administran o están vinculados a cada institución educativa.
 * </p>
 *
 * <p>
 * Utiliza {@link UsuarioInstitucionId} como clave primaria compuesta para
 * establecer la relación entre ambos identificadores.
 * </p>
 *
 * @see Usuario
 * @see InstitucionEducativa
 * @see UsuarioInstitucionId
 * @see UsuarioInstitucionDTO
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Setter
@Table(name = "usuario_institucion")
@IdClass(UsuarioInstitucionId.class)
public class UsuarioInstitucion {

    /** Identificador del usuario colegio asociado a la institución. */
    @Id
    @Column(name = "usuario_colegio_id")
    private Long usuarioColegioId;

    /** Identificador de la institución educativa asociada al usuario colegio. */
    @Id
    @Column(name = "institucion_educativa_id")
    private Long institucionEducativaId;

    /** Indica si la relación usuario-institución está activa. Por defecto es {@code true}. */
    @Column(nullable = false)
    private Boolean activo = true;

    /**
     * Relación con el usuario colegio.
     * <p>
     * Está marcada con {@link JsonIgnore} para evitar recursividad en la serialización JSON.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "usuario_colegio_id", insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuarioColegio;

    /**
     * Relación con la institución educativa.
     * <p>
     * También marcada con {@link JsonIgnore} para evitar bucles en la serialización.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "institucion_educativa_id", insertable = false, updatable = false)
    @JsonIgnore
    private InstitucionEducativa institucionEducativa;

    /** Constructor por defecto requerido por JPA. */
    public UsuarioInstitucion() {
    }

    /**
     * Constructor que permite inicializar una relación entre usuario colegio e institución.
     *
     * @param usuarioColegioId       identificador del usuario colegio.
     * @param institucionEducativaId identificador de la institución educativa.
     */
    public UsuarioInstitucion(Long usuarioColegioId, Long institucionEducativaId) {
        this.usuarioColegioId = usuarioColegioId;
        this.institucionEducativaId = institucionEducativaId;
        this.activo = true;
    }

    /**
     * Inicializa los valores por defecto antes de persistir la relación.
     * <p>
     * Se ejecuta automáticamente al insertar un nuevo registro.
     * </p>
     */
    @PrePersist
    private void prePersist() {
        if (activo == null) {
            activo = true;
        }
    }

    /**
     * Convierte la entidad {@link UsuarioInstitucion} en un objeto {@link UsuarioInstitucionDTO}.
     * <p>
     * Este método es útil para exponer datos combinados de usuario e institución
     * sin necesidad de múltiples consultas o relaciones anidadas.
     * </p>
     *
     * @return un objeto {@link UsuarioInstitucionDTO} con la información esencial.
     */
    public UsuarioInstitucionDTO obtenerUsuarioInstitucionDTO() {
        return new UsuarioInstitucionDTO(
                usuarioColegioId,
                usuarioColegio.getNombreUsuario(),
                usuarioColegio.getNombreCompleto(),
                usuarioColegio.getEmail(),
                usuarioColegio.getTelefono(),
                activo,
                institucionEducativa.getId(),
                institucionEducativa.getNombre()
        );
    }
}
