package com.utp.libretago.endpoint;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.EventoDTO;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.service.EventoService;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

/**
 * Endpoint responsable de gestionar los eventos visibles para los usuarios con rol APODERADO.
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */

@Endpoint
@RolesAllowed({ RolesEnum.APODERADO, RolesEnum.PROFESOR })
public class EventoUsuarioEndpoint {

    @Autowired
    private EventoService eventoService;

    /**
     * Lista los eventos visibles para el usuario autenticado según el tipo especificado.
     * <p>
     * Si el tipo es "ENVIADAS" y el usuario es profesor, devuelve los eventos creados por él. De lo contrario (o si es
     * apoderado), devuelve los eventos recibidos a través de los grupos asociados a sus alumnos.
     * </p>
     *
     * @param pageable configuración de paginación y ordenamiento
     * @param tipo     tipo de eventos a listar: "RECIBIDAS" (por defecto) o "ENVIADAS"
     * @return página de {@link EventoDTO} correspondientes al usuario autenticado
     */
    @NonNull
    public Page<@NonNull EventoDTO> listarEventosUsuario(Pageable pageable, String tipo) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ("ENVIADAS".equalsIgnoreCase(tipo) && appUser.hasRole(RolesEnum.PROFESOR)) {
            return eventoService.listarEventosPorUsuarioCreadorId(appUser.getId(), pageable);
        }

        return eventoService.listarEventosPorApoderadoId(appUser.getId(), pageable);
    }

    /**
     * Crea un nuevo evento en el sistema. Solo accesible para usuarios con rol PROFESOR.
     *
     * @param eventoDTO datos del evento a crear
     * @return identificador del evento creado
     */
    @RolesAllowed({ RolesEnum.PROFESOR })
    public Long crearEvento(EventoDTO eventoDTO) {
        var evento = eventoService.crearEvento(eventoDTO);
        return evento.getId();
    }
}
