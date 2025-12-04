package com.utp.libretago.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.dto.NotificacionDTO;
import com.utp.libretago.classes.filtros.FiltroNotificacion;

import java.util.List;
import java.util.Optional;

public interface NotificacionService {
    Page<NotificacionDTO> buscarNotificacionesPorFiltros(FiltroNotificacion filtro, Pageable pageable);

    Optional<NotificacionDTO> obtenerPorId(Long id);

    NotificacionDTO crearNotificacion(NotificacionDTO notificacionDTO);

    NotificacionDTO actualizarNotificacion(Long id, NotificacionDTO notificacionDTO);

    int inactivarById(Long id);

    // Listar notificaciones relacionadas a un apoderado (por los grupos de sus alumnos)
    Page<NotificacionDTO> listarNotificacionesPorApoderadoId(Long apoderadoId, Pageable pageable);

    // Listar notificaciones creadas por un usuario (Profesor)
    /**
     * Lista las notificaciones creadas por un usuario específico.
     * 
     * @param usuarioCreadorId ID del usuario creador.
     * @param pageable         Configuración de paginación.
     * @return Página de notificaciones en formato {@link NotificacionDTO}.
     */
    Page<NotificacionDTO> listarNotificacionesPorUsuarioCreadorId(Long usuarioCreadorId, Pageable pageable);

    /**
     * Obtiene estadísticas de notificaciones enviadas por día en los últimos 2 meses.
     *
     * @param institucionId identificador de la institución educativa.
     * @return lista de estadísticas.
     */
    List<com.utp.libretago.classes.dto.NotificationStatsDTO> obtenerEstadisticasNotificaciones(Long institucionId);

    /**
     * Obtiene estadísticas de notificaciones enviadas por día en los últimos 2 meses
     * para todas las instituciones educativas (vista de administrador).
     *
     * @return lista de estadísticas por institución.
     */
    List<com.utp.libretago.classes.dto.NotificationStatsMultiLineDTO> obtenerEstadisticasNotificacionesTodasInstituciones();
}
