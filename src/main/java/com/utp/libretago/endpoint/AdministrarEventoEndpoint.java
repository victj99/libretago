package com.utp.libretago.endpoint;

import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.EventoDTO;
import com.utp.libretago.classes.filtros.FiltroEvento;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.service.EventoService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

/**
 * Endpoint para la administración de eventos.
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */

@Endpoint
@RolesAllowed({ RolesEnum.COLEGIO, RolesEnum.PROFESOR })
public class AdministrarEventoEndpoint {

    @Autowired
    private EventoService eventoService;

    /**
     * Busca eventos aplicando filtros y paginación, restringiendo los resultados a la institución educativa del usuario
     * autenticado.
     *
     * @param filtro   criterios de búsqueda para eventos
     * @param pageable configuración de paginación y ordenamiento
     * @return página de {@link EventoDTO} que cumplen con los filtros
     */
    @NonNull
    public Page<@NonNull EventoDTO> buscarEventosPorFiltros(FiltroEvento filtro, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");

        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        filtro.setInstitucionEducativaId(appUser.getInstitucionEducativaId());

        return eventoService.buscarEventosPorFiltros(filtro, pageable);
    }

    /**
     * Obtiene un evento específico por su identificador.
     *
     * @param id identificador único del evento
     * @return {@link Optional} con el evento encontrado, o vacío si no existe
     */
    public Optional<EventoDTO> obtenerEvento(Long id) {
        return eventoService.obtenerPorId(id);
    }

    /**
     * Crea un nuevo evento en el sistema.
     *
     * @param eventoDTO datos del evento a crear
     * @return identificador del evento creado
     */
    public Long crearEvento(EventoDTO eventoDTO) {
        var evento = eventoService.crearEvento(eventoDTO);
        return evento.getId();
    }

    /**
     * Actualiza un evento existente con nuevos datos.
     *
     * @param id        identificador del evento a actualizar
     * @param eventoDTO datos actualizados del evento
     * @return identificador del evento actualizado
     */
    public Long editarEvento(Long id, EventoDTO eventoDTO) {
        var evento = eventoService.actualizarEvento(id, eventoDTO);
        return evento.getId();
    }

    /**
     * Inactiva lógicamente un evento (no lo elimina físicamente).
     *
     * @param id identificador del evento a inactivar
     * @return número de registros afectados (generalmente 1)
     */
    public int inactivarById(Long id) {
        return eventoService.inactivarById(id);
    }
}
