package com.utp.libretago.entity;

import java.time.LocalDateTime;

import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.AlumnoDTO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa a un alumno dentro del sistema Libretago.
 * <p>
 * Esta clase mapea la tabla <strong>alumno</strong> en la base de datos
 * e incluye los datos personales, de institución educativa y del apoderado
 * asociado al alumno.
 * </p>
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Setter
@Table(name = "alumno")
public class Alumno {

    /** Identificador único del alumno. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombres del alumno. No puede ser nulo. */
    @Column(nullable = false, length = 255)
    private String nombres;

    /** Apellidos del alumno. No puede ser nulo. */
    @Column(nullable = false, length = 255)
    private String apellidos;

    /** Código institucional asignado al alumno. */
    @Column(name = "codigo_alumno", nullable = false, length = 50)
    private String codigoAlumno;

    /** Identificador de la institución educativa a la que pertenece el alumno. */
    @Column(name = "institucion_educativa_id", nullable = false)
    private Long institucionEducativaId;

    /** Identificador del usuario apoderado relacionado (opcional). */
    @Column(name = "usuario_apoderado_id")
    private Long usuarioApoderadoId;

    /**
     * Relación con el apoderado (usuario).
     * <p>
     * Se carga de manera perezosa (lazy) para optimizar rendimiento.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_apoderado_id", insertable = false, updatable = false)
    private Usuario usuarioApoderado;

    /** Indica si el alumno está activo. Por defecto es {@code true}. */
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha y hora de creación del registro. Se asigna automáticamente. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Método ejecutado antes de insertar un nuevo registro.
     * <p>
     * Inicializa los valores por defecto de {@code activo} y {@code fechaCreacion}.
     * </p>
     */
    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    /**
     * Convierte la entidad {@link Alumno} en un objeto {@link AlumnoDTO}.
     * <p>
     * Este método incluye datos del apoderado si existen.
     * </p>
     *
     * @return un objeto {@link AlumnoDTO} con los datos del alumno.
     */
    public AlumnoDTO obtenerAlumnoDTO() {
        String email = null;
        String telefono = null;
        String dni = null;
        String nombreApoderado = null;

        if (usuarioApoderado != null) {
            email = usuarioApoderado.getEmail();
            telefono = usuarioApoderado.getTelefono();
            dni = usuarioApoderado.getNombreUsuario();
            nombreApoderado = usuarioApoderado.getNombreCompleto();
        }

        return new AlumnoDTO(id, nombres, apellidos, codigoAlumno, telefono, email, dni, nombreApoderado, activo);
    }

    /**
     * Convierte la entidad {@link Alumno} en un objeto {@link Alumno2DTO},
     * con información simplificada (sin datos del apoderado).
     *
     * @return un objeto {@link Alumno2DTO} con los datos básicos del alumno.
     */
    public Alumno2DTO obtenerAlumno2DTO() {
        return new Alumno2DTO(id, nombres, apellidos, codigoAlumno);
    }
}
