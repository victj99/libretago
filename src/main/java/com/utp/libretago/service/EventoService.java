package com.utp.libretago.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.dto.EventoDTO;
import com.utp.libretago.classes.filtros.FiltroEvento;

import java.util.Optional;

/**
 * Interfaz que define los métodos del servicio para la gestión de eventos dentro del sistema.
 *
 * Permite realizar operaciones CRUD (crear, leer, actualizar, eliminar) sobre la entidad {@link Evento}.
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
public interface EventoService {
    Page<EventoDTO> buscarEventosPorFiltros(FiltroEvento filtro, Pageable pageable);

    Optional<EventoDTO> obtenerPorId(Long id);

    EventoDTO crearEvento(EventoDTO eventoDTO);

    EventoDTO actualizarEvento(Long id, EventoDTO eventoDTO);

    int inactivarById(Long id);

    Page<EventoDTO> listarEventosPorApoderadoId(Long apoderadoId, Pageable pageable);

    Page<EventoDTO> listarEventosPorUsuarioCreadorId(Long usuarioCreadorId, Pageable pageable);

    /**
     * Obtiene estadísticas de eventos enviados por día en los últimos 2 meses.
     *
     * @param institucionId identificador de la institución educativa.
     * @return lista de estadísticas.
     */
    java.util.List<com.utp.libretago.classes.dto.EventoStatsDTO> obtenerEstadisticasEventos(Long institucionId);

    /**
     * Obtiene estadísticas de eventos enviados por día en los últimos 2 meses
     * para todas las instituciones educativas (vista de administrador).
     *
     * @return lista de estadísticas por institución.
     */
    java.util.List<com.utp.libretago.classes.dto.EventoStatsMultiLineDTO> obtenerEstadisticasEventosTodasInstituciones();
}
