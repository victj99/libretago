package com.utp.libretago.entity;

import java.time.LocalDateTime;

import com.utp.libretago.utils.MensajesValidacion;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa una institución educativa dentro del sistema Libretago.
 * <p>
 * Esta entidad almacena información general como el nombre, dirección, teléfono
 * y código UGEL de cada institución registrada. También incluye el estado de
 * actividad y la fecha de creación del registro.
 * </p>
 *
 * <p>
 * Se aplican validaciones mediante anotaciones de Jakarta Validation y mensajes
 * personalizados definidos en {@link MensajesValidacion}.
 * </p>
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Setter
@Table(name = "institucion_educativa")
public class InstitucionEducativa {

    /** Identificador único de la institución educativa. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la institución educativa.
     * <p>
     * No puede estar vacío y tiene un máximo de 255 caracteres.
     * </p>
     */
    @Column(nullable = false, length = 255)
    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    private String nombre;

    /** Dirección de la institución educativa (opcional). */
    @Column(length = 255)
    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    private String direccion;

    /** Número telefónico de contacto (opcional). */
    @Column(length = 20)
    private String telefono;

    /**
     * Código UGEL de la institución educativa.
     * <p>
     * Campo obligatorio de hasta 50 caracteres.
     * </p>
     */
    @Column(name = "codigo_ugel", nullable = false, length = 50)
    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 50, message = MensajesValidacion.LARGO_MAXIMO)
    private String codigoUgel;

    /** Indica si la institución educativa está activa. Por defecto es {@code true}. */
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha y hora en que la institución fue registrada en el sistema. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Inicializa los valores por defecto antes de insertar el registro.
     * <p>
     * Se ejecuta automáticamente antes de la persistencia.
     * </p>
     */
    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    /** Constructor por defecto requerido por JPA. */
    public InstitucionEducativa() {
    }

    /**
     * Constructor que permite inicializar una institución con su identificador.
     *
     * @param id identificador único de la institución educativa.
     */
    public InstitucionEducativa(Long id) {
        this.id = id;
    }
}
