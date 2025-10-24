package com.utp.libretago.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.dto.NotificacionDTO;
import com.utp.libretago.classes.filtros.FiltroNotificacion;

import java.util.Optional;

public interface NotificacionService {
    Page<NotificacionDTO> buscarNotificacionesPorFiltros(FiltroNotificacion filtro, Pageable pageable);

    Optional<NotificacionDTO> obtenerPorId(Long id);

    NotificacionDTO crearNotificacion(NotificacionDTO notificacionDTO);

    NotificacionDTO actualizarNotificacion(Long id, NotificacionDTO notificacionDTO);

    int inactivarById(Long id);

    // Listar notificaciones relacionadas a un apoderado (por los grupos de sus alumnos)
    org.springframework.data.domain.Page<NotificacionDTO> listarNotificacionesPorApoderadoId(Long apoderadoId, org.springframework.data.domain.Pageable pageable);
}
