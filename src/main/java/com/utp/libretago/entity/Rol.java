package com.utp.libretago.entity;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * Entidad que representa un rol de usuario dentro del sistema Libretago.
 * <p>
 * Los roles determinan los permisos y el nivel de acceso de los usuarios,
 * como colegio, profesor o apoderado.
 * </p>
 *
 * <p>
 * Los identificadores constantes definidos en esta clase facilitan la
 * referencia estática a roles comunes en el sistema.
 * </p>
 *
 * @see Usuario
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Table(name = "rol")
public class Rol {

    // -------------------------------------------------------------------------
    // Constantes de roles predefinidos
    // -------------------------------------------------------------------------

    /** Identificador del rol "Colegio". */
    public static final Long ID_COLEGIO = 2L;

    /** Identificador del rol "Profesor". */
    public static final Long ID_PROFESOR = 3L;

    /** Identificador del rol "Apoderado". */
    public static final Long ID_APODERADO = 4L;

    // -------------------------------------------------------------------------
    // Atributos de la entidad
    // -------------------------------------------------------------------------

    /** Identificador único del rol. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del rol.
     * <p>
     * Debe ser único y no nulo. Ejemplos: <em>"COLEGIO"</em>, <em>"PROFESOR"</em>, <em>"APODERADO"</em>.
     * </p>
     */
    @Column(nullable = false, length = 50, unique = true)
    private String nombre;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    /** Constructor vacío requerido por JPA. */
    public Rol() {
    }

    /**
     * Constructor que permite inicializar un rol con su identificador.
     *
     * @param id identificador único del rol.
     */
    public Rol(Long id) {
        this.id = id;
    }
}
