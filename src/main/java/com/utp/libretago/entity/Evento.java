package com.utp.libretago.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.utp.libretago.classes.dto.EventoDTO;
import com.utp.libretago.classes.dto.IdLabelDTO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa un evento dentro del sistema Libretago.
 * <p>
 * Los eventos son similares a las notificaciones pero incluyen una fecha específica de realización.
 * </p>
 *
 * <p>
 * La entidad gestiona información como el título, descripción, fecha del evento, estado, evaluador asignado y fechas de
 * creación o evaluación.
 * </p>
 *
 * <p>
 * Se relaciona con las entidades {@link Usuario} y {@link Grupo}.
 * </p>
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Getter
@Setter
@Table(name = "evento")
public class Evento {

    // Constantes de estado
    public static final String ESTADO_PENDIENTE = "P";
    public static final String ESTADO_APROBADO = "A";
    public static final String ESTADO_RECHAZADO = "R";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título descriptivo del evento. */
    @Column(nullable = false, length = 255)
    private String titulo;

    /** Detalle o descripción completa del evento. */
    @Column(nullable = false, columnDefinition = "text")
    private String detalle;

    /** Fecha en la que se realizará el evento. */
    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    /**
     * Usuario que creó el evento.
     * <p>
     * Relación muchos a uno con {@link Usuario}. No puede ser nula.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;

    /**
     * Estado del evento.
     * <p>
     * Puede ser:
     * <ul>
     * <li><strong>P</strong> – Pendiente</li>
     * <li><strong>E</strong> – Evaluado</li>
     * <li><strong>C</strong> – Cancelado</li>
     * </ul>
     * Valor por defecto: <strong>P</strong>.
     * </p>
     */
    @Column(length = 1, nullable = false)
    private String estado = ESTADO_PENDIENTE;

    /**
     * Usuario asignado como evaluador del evento (opcional).
     */
    @ManyToOne
    @JoinColumn(name = "usuario_evaluador_id")
    private Usuario usuarioEvaluador;

    /** Fecha en la que el evento fue evaluado (si aplica). */
    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;

    /** Indica si el evento está activo. Por defecto es {@code true}. */
    @Column(nullable = false)
    private Boolean activo = true;

    /** Fecha y hora en la que se registró el evento. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<EventoGrupo> eventoGrupos;

    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    public EventoDTO obtenerEventoDTO() {
        EventoDTO eventoDTO = new EventoDTO();
        eventoDTO.setId(id);
        eventoDTO.setTitulo(titulo);
        eventoDTO.setDetalle(detalle);
        eventoDTO.setFechaEvento(fechaEvento);
        eventoDTO.setUsuarioCreadorId(usuarioCreador.getId());
        eventoDTO.setUsuarioCreadorNombre(usuarioCreador.getNombreCompleto());
        eventoDTO.setEstado(estado);

        if (usuarioEvaluador != null) {
            eventoDTO.setUsuarioEvaluadorId(usuarioEvaluador.getId());
            eventoDTO.setUsuarioEvaluadorNombre(usuarioEvaluador.getNombreCompleto());
        }

        eventoDTO.setFechaEvaluacion(fechaEvaluacion);
        eventoDTO.setActivo(activo);

        return eventoDTO;
    }

    public EventoDTO obtenerEventoConGruposDTO(List<Grupo> grupos) {
        EventoDTO eventoDTO = obtenerEventoDTO();

        if (grupos != null && !grupos.isEmpty()) {
            for (Grupo item : grupos) {
                eventoDTO.getGrupos().add(new IdLabelDTO(item.getId(), item.getNombre()));
            }
        }

        return eventoDTO;
    }
}
