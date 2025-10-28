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

/**
 * Endpoint para la administración de notificaciones.
 * <p>
 * Permite a usuarios con los roles {@link RolesEnum#COLEGIO} y {@link RolesEnum#PROFESOR}
 * realizar operaciones CRUD sobre las notificaciones del sistema.
 * </p>
 *
 * <p>Expone métodos para buscar, obtener, crear, editar e inactivar notificaciones.</p>
 * 
 * <p>Usa el servicio {@link NotificacionService} para la lógica de negocio.</p>
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-27
 */

@Endpoint
@RolesAllowed({ RolesEnum.COLEGIO, RolesEnum.PROFESOR })
public class AdministrarNotificacionEndpoint {
    
    /** Servicio que maneja la lógica de negocio de las notificaciones. */
    @Autowired
    private NotificacionService notificacionService;
     /**
         * Busca notificaciones según los filtros especificados y la configuración de paginación.
         * @param filtro   objeto con los criterios de búsqueda
         * @param pageable configuración de paginación y ordenamiento
         * @return una página de {@link NotificacionDTO} que cumplen con los filtros
     */
    
    @NonNull
    public Page<@NonNull NotificacionDTO> buscarNotificacionesPorFiltros(FiltroNotificacion filtro, Pageable pageable) {
        // Aplica orden descendente por defecto según el campo "id"
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");
        // Llama al servicio para realizar la búsqueda con filtros
        return notificacionService.buscarNotificacionesPorFiltros(filtro, pageable);
    }
         /**
             * Obtiene una notificación por su identificador.
             * @param id identificador de la notificación
             * @return un {@link Optional} con el {@link NotificacionDTO} encontrado, o vacío si no existe
         */
    public Optional<NotificacionDTO> obtenerNotificacion(Long id) {
        return notificacionService.obtenerPorId(id);
    }
    /**
         * Crea una nueva notificación en el sistema.
         * @param notificacionDTO datos de la notificación a crear
         * @return el ID de la notificación creada
     */
    public Long crearNotificacion(NotificacionDTO notificacionDTO) {
        var notificacion = notificacionService.crearNotificacion(notificacionDTO);
        return notificacion.getId();
    }

    /**
         * Actualiza los datos de una notificación existente.
         * @param identificador de la notificación a actualizar
         * @param notificacionDTO datos actualizados de la notificación
         * @return el ID de la notificación actualizada
     */
    
    public Long editarNotificacion(Long id, NotificacionDTO notificacionDTO) {
        var notificacion = notificacionService.actualizarNotificacion(id, notificacionDTO);
        return notificacion.getId();
    }
    /**
         * Inactiva una notificación por su identificador.
         * <p>
         * En lugar de eliminar el registro, este método cambia su estado a inactivo.
         * </p>
         * @param id identificador de la notificación a inactivar
         * @return número de registros afectados (normalmente 1 si se realizó con éxito)
     */
    public int inactivarById(Long id) {
        return notificacionService.inactivarById(id);
    }
}
