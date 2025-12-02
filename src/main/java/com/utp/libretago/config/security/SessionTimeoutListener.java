package com.utp.libretago.config.security;

import com.utp.libretago.classes.RolesEnum;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

/**
 * Listener que ajusta el tiempo de expiración de la sesión según el rol del usuario.
 * <p>
 * Si el usuario tiene rol PROFESOR o APODERADO, la sesión se extiende a 15 días. De lo contrario, se mantiene el tiempo
 * por defecto.
 * </p>
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
@Component
public class SessionTimeoutListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final int QUINCE_DIAS_EN_SEGUNDOS = 15 * 24 * 60 * 60;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();

        if (authentication == null) {
            return;
        }

        boolean debeExtenderSesion = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_" + RolesEnum.PROFESOR) || role.equals("ROLE_" + RolesEnum.APODERADO));

        if (debeExtenderSesion) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attr != null) {
                HttpSession session = attr.getRequest().getSession(false);
                if (session != null) {
                    session.setMaxInactiveInterval(QUINCE_DIAS_EN_SEGUNDOS);
                }
            }
        }
    }
}
