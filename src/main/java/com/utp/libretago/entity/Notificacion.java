package com.utp.libretago.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.utp.libretago.classes.dto.IdLabelDTO;
import com.utp.libretago.classes.dto.NotificacionDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "notificacion")
public class Notificacion {

    public static final String ESTADO_PENDIENTE = "P";
    public static final String ESTADO_APROBADO = "A";
    public static final String ESTADO_RECHAZADO = "R";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, columnDefinition = "text")
    private String detalle;

    @ManyToOne
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;

    @ManyToMany
    @JoinTable(name = "notificacion_grupo", joinColumns = @JoinColumn(name = "notificacion_id"), inverseJoinColumns = @JoinColumn(name = "grupo_id"))
    private Set<Grupo> grupos = new HashSet<>();

    @Column(length = 1, nullable = false)
    private String estado = "P";

    @ManyToOne
    @JoinColumn(name = "usuario_evaluador_id")
    private Usuario usuarioEvaluador;

    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    public NotificacionDTO obtenerNotificacionDTO() {
        NotificacionDTO notificacionDTO = new NotificacionDTO();
        notificacionDTO.setId(id);
        notificacionDTO.setTitulo(titulo);
        notificacionDTO.setDetalle(detalle);
        notificacionDTO.setUsuarioCreadorId(usuarioCreador.getId());
        notificacionDTO.setUsuarioCreadorNombre(usuarioCreador.getNombreCompleto());
        notificacionDTO.setEstado(estado);

        if (usuarioEvaluador != null) {
            notificacionDTO.setUsuarioEvaluadorId(usuarioEvaluador.getId());
            notificacionDTO.setUsuarioEvaluadorNombre(usuarioEvaluador.getNombreCompleto());
        }
        notificacionDTO.setFechaEvaluacion(fechaEvaluacion);
        notificacionDTO.setActivo(activo);

        return notificacionDTO;
    }

    public NotificacionDTO obtenerNotificacionConGruposDTO() {
        NotificacionDTO notificacionDTO = obtenerNotificacionDTO();
        // Map groups
        if (grupos != null && !grupos.isEmpty()) {
            for (Grupo item : grupos) {
                notificacionDTO.getGrupos().add(new IdLabelDTO(item.getId(), item.getNombre()));
            }
        }

        return notificacionDTO;
    }
}