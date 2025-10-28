package com.utp.libretago.entity;

import java.time.LocalDateTime;

import com.utp.libretago.classes.dto.GrupoDTO;
import com.utp.libretago.classes.dto.IdLabelDTO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa un grupo dentro de una institución educativa
 * en el sistema Libretago.
 * <p>
 * Los grupos agrupan alumnos y están asociados a una institución educativa
 * y a un usuario profesor responsable.
 * </p>
 *
 * <p>
 * Incluye métodos de conversión a objetos DTO para su uso en la capa de
 * presentación o transporte de datos.
 * </p>
 *
 * @see InstitucionEducativa
 * @see Usuario
 * @see GrupoDTO
 * @see IdLabelDTO
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Setter
@Table(name = "grupo")
public class Grupo {

    /** Identificador único del grupo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del grupo. */
    @Column(nullable = false, length = 255)
    private String nombre;

    /** Identificador de la institución educativa a la que pertenece el grupo. */
    @Column(name = "institucion_educativa_id")
    private Long institucionEducativaId;

    /**
     * Relación con la institución educativa asociada.
     * <p>
     * Carga diferida (lazy) para optimizar rendimiento.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institucion_educativa_id", nullable = false, insertable = false, updatable = false)
    private InstitucionEducativa institucionEducativa;

    /** Identificador del usuario profesor asignado al grupo. */
    @Column(name = "usuario_profesor_id")
    private Long usuarioProfesorId;

    /**
     * Relación con el profesor (usuario) asignado al grupo.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_profesor_id", insertable = false, updatable = false)
    private Usuario usuarioProfesor;

    /** Indica si el grupo está activo. Por defecto es {@code true}. */
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha y hora en que el grupo fue creado. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Inicializa los valores por defecto antes de persistir el grupo.
     * <p>
     * Se ejecuta automáticamente al insertar un nuevo registro.
     * </p>
     */
    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    /** Constructor vacío requerido por JPA. */
    public Grupo() {
    }

    /**
     * Constructor que permite inicializar un grupo con solo su identificador.
     *
     * @param id identificador único del grupo.
     */
    public Grupo(Long id) {
        this.id = id;
    }

    /**
     * Convierte la entidad {@link Grupo} a un objeto {@link GrupoDTO}.
     * <p>
     * Incluye información del profesor si está disponible.
     * </p>
     *
     * @return un objeto {@link GrupoDTO} con los datos del grupo.
     */
    public GrupoDTO obtenerGrupoDTO() {
        String nombreProfesor = null;
        if (usuarioProfesor != null) {
            nombreProfesor = usuarioProfesor.getNombreCompleto();
        }

        return new GrupoDTO(id, nombre, null, null, institucionEducativaId, usuarioProfesorId, activo, nombreProfesor);
    }

    /**
     * Convierte la entidad {@link Grupo} a un objeto {@link IdLabelDTO}.
     * <p>
     * Ideal para listas desplegables o representaciones ligeras.
     * </p>
     *
     * @return un {@link IdLabelDTO} con el identificador y nombre del grupo.
     */
    public IdLabelDTO obtenerIdLabelDTO() {
        return new IdLabelDTO(id, nombre);
    }
}
