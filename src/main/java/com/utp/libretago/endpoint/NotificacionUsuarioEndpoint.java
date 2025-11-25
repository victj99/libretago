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

/**
 * Endpoint responsable de gestionar las notificaciones visibles para los usuarios con rol APODERADO.
 * <p>
 * Este endpoint permite que un apoderado consulte las notificaciones que le han sido asignadas, aplicando filtros de
 * paginación y ordenamiento.
 * </p>
 *
 * <p>
 * <b>Seguridad:</b> Solo los usuarios con el rol {@link RolesEnum#APODERADO} pueden acceder a este endpoint.
 * </p>
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */

@Endpoint
// Solo los apoderados pueden acceder a este endpoint
@RolesAllowed({ RolesEnum.APODERADO, RolesEnum.PROFESOR })
public class NotificacionUsuarioEndpoint {

    /**
     * Servicio encargado de la lógica de negocio relacionada con notificaciones. Se utiliza para obtener las notificaciones
     * asociadas a un apoderado.
     */
    @Autowired
    private NotificacionService notificacionService;

    /**
     * Obtiene la lista de notificaciones asociadas al usuario apoderado actualmente autenticado.
     *
     * Este método:
     * <ul>
     * <li>Recupera el usuario activo desde el contexto de seguridad.</li>
     * <li>Consulta las notificaciones vinculadas a su identificador.</li>
     * <li>Devuelve los resultados paginados según la configuración del parámetro {@code pageable}.</li>
     * </ul>
     *
     * @param pageable Configuración de paginación y ordenamiento.
     * @param tipo     Tipo de notificaciones a listar: "RECIBIDAS" (por defecto) o "ENVIADAS".
     * @return Página de {@link NotificacionDTO} correspondientes al apoderado autenticado.
     */

    @NonNull
    public Page<@NonNull NotificacionDTO> listarNotificacionesUsuario(Pageable pageable, String tipo) {
        // Obtenemos el usuario autenticado desde el contexto de seguridad
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ("ENVIADAS".equalsIgnoreCase(tipo) && appUser.hasRole(RolesEnum.PROFESOR)) {
            return notificacionService.listarNotificacionesPorUsuarioCreadorId(appUser.getId(), pageable);
        }

        // Llamamos al servicio para obtener las notificaciones paginadas del apoderado
        return notificacionService.listarNotificacionesPorApoderadoId(appUser.getId(), pageable);
    }

    /**
     * Crea una nueva notificación en el sistema.
     * 
     * @param notificacionDTO datos de la notificación a crear
     * @return el ID de la notificación creada
     */
    @RolesAllowed({ RolesEnum.PROFESOR })
    public Long crearNotificacion(NotificacionDTO notificacionDTO) {
        var notificacion = notificacionService.crearNotificacion(notificacionDTO);
        return notificacion.getId();
    }
}
