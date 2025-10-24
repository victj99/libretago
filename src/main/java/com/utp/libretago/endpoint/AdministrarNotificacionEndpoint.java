package com.utp.libretago.endpoint;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.NotificacionDTO;
import com.utp.libretago.classes.filtros.FiltroNotificacion;
import com.utp.libretago.service.NotificacionService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;
import jakarta.annotation.security.RolesAllowed;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Endpoint
@RolesAllowed({ RolesEnum.COLEGIO, RolesEnum.PROFESOR })
public class AdministrarNotificacionEndpoint {

    @Autowired
    private NotificacionService notificacionService;

    @NonNull
    public Page<@NonNull NotificacionDTO> buscarNotificacionesPorFiltros(FiltroNotificacion filtro, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");
        return notificacionService.buscarNotificacionesPorFiltros(filtro, pageable);
    }

    public Optional<NotificacionDTO> obtenerNotificacion(Long id) {
        return notificacionService.obtenerPorId(id);
    }

    public Long crearNotificacion(NotificacionDTO notificacionDTO) {
        var notificacion = notificacionService.crearNotificacion(notificacionDTO);
        return notificacion.getId();
    }

    public Long editarNotificacion(Long id, NotificacionDTO notificacionDTO) {
        var notificacion = notificacionService.actualizarNotificacion(id, notificacionDTO);
        return notificacion.getId();
    }

    public int inactivarById(Long id) {
        return notificacionService.inactivarById(id);
    }
}
