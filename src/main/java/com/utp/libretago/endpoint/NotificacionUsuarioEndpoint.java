package com.utp.libretago.endpoint;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.NotificacionDTO;
import com.utp.libretago.service.NotificacionService;
import com.utp.libretago.config.security.AppUser;
import com.vaadin.hilla.Endpoint;
import jakarta.annotation.security.RolesAllowed;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

@Endpoint
@RolesAllowed({ RolesEnum.APODERADO })
public class NotificacionUsuarioEndpoint {

    @Autowired
    private NotificacionService notificacionService;

    @NonNull
    public Page<@NonNull NotificacionDTO> listarNotificacionesUsuario(Pageable pageable) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificacionService.listarNotificacionesPorApoderadoId(appUser.getId(), pageable);
    }
}
