package com.utp.libretago.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa el token de un dispositivo asociado a un usuario dentro del sistema Libretago.
 * <p>
 * Los tokens de dispositivo se utilizan para identificar y autenticar
 * las notificaciones o sesiones de los usuarios en distintos dispositivos.
 * </p>
 *
 * <p>
 * Cada token pertenece a un usuario propietario y almacena la fecha de creación
 * para control y auditoría.
 * </p>
 *
 * @see Usuario
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Setter
@Table(name = "token_dispositivo")
public class TokenDispositivo {

    /** Identificador único del token de dispositivo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Token único que identifica el dispositivo del usuario.
     * <p>
     * Puede corresponder a un token de mensajería (por ejemplo, Firebase Cloud Messaging)
     * o a un identificador de sesión persistente.
     * </p>
     */
    @Column(nullable = false, length = 255)
    private String token;

    /**
     * Usuario propietario del token de dispositivo.
     * <p>
     * Relación muchos a uno con {@link Usuario}.
     * Se carga de forma diferida (lazy loading) para optimizar el rendimiento.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_propietario_id", nullable = false)
    private Usuario usuarioPropietario;

    /**
     * Fecha y hora en que el token fue registrado en el sistema.
     * <p>
     * Se asigna automáticamente en el momento de creación.
     * </p>
     */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
