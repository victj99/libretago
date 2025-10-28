package com.utp.libretago.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.NotificacionDTO;
import com.utp.libretago.classes.filtros.FiltroNotificacion;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.entity.Notificacion;
import com.utp.libretago.entity.Usuario;
import com.utp.libretago.repository.AlumnoGrupoRepository;
import com.utp.libretago.repository.NotificacionRepository;
import com.utp.libretago.service.FirebaseMessageService;
import com.utp.libretago.service.NotificacionService;
import com.utp.libretago.service.TokenDispositivoService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.exception.EndpointException;

/**
     * Implementación del servicio {@link NotificacionService} para la gestión de notificaciones.
     * Incluye creación, actualización, búsqueda, inactivación y envío de notificaciones 
     * mediante Firebase Cloud Messaging (FCM).
     * @author Roberto 
     * @version 1.0
     * @since 2025-10-28
 */
@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private TokenDispositivoService tokenDispositivoService;

    @Autowired
    private FirebaseMessageService fcmService;

    @Autowired
    private AlumnoGrupoRepository alumnoGrupoRepository;
    
    /**
         * Busca notificaciones aplicando filtros con paginación.
         * @param filtro   Filtros de búsqueda.
         * @param pageable Configuración de paginación.
         * @return Página de resultados en formato {@link NotificacionDTO}.
     */
    @Override
    public Page<NotificacionDTO> buscarNotificacionesPorFiltros(FiltroNotificacion filtro, Pageable pageable) {
        // Aplicar filtros dinámicos y mapear a DTO
        return notificacionRepository.findAll(filtro.generarFiltroNotificacion(), pageable).map(Notificacion::obtenerNotificacionDTO);
    }

    /**
         * Obtiene una notificación por su identificador.
         * @param id ID de la notificación.
         * @return Optional con la notificación en formato {@link NotificacionDTO}.
     */
    @Override
    public Optional<NotificacionDTO> obtenerPorId(Long id) {
        // Buscar por ID y convertir a DTO con grupos asociados
        return notificacionRepository.findById(id).map(Notificacion::obtenerNotificacionConGruposDTO);
    }
    /**
         * Crea una nueva notificación. Si el usuario autenticado es del rol "COLEGIO",
         * la notificación se aprueba automáticamente y se envía vía Firebase.
         * @param notificacionDTO Datos de la notificación a crear.
         * @return Notificación creada en formato {@link NotificacionDTO}.
     */
    @Override
    public NotificacionDTO crearNotificacion(NotificacionDTO notificacionDTO) {
        // Obtener usuario autenticado
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        notificacionDTO.setUsuarioCreadorId(appUser.getId());
        // Si el usuario es COLEGIO, se aprueba automáticamente
        if (appUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + RolesEnum.COLEGIO))) {
            notificacionDTO.setEstado(Notificacion.ESTADO_APROBADO);
            notificacionDTO.setUsuarioEvaluadorId(appUser.getId());
        }
        // Convertir el DTO en entidad
        Notificacion notificacion = notificacionDTO.obtenerNotificacion();
         // Registrar fecha de evaluación si ya fue aprobada
        if (notificacion.getUsuarioEvaluador() != null) {
            notificacion.setFechaEvaluacion(LocalDateTime.now());
        }
        // Guardar la nueva notificación
        notificacion = notificacionRepository.save(notificacion);

        if (notificacion.getEstado().equals(Notificacion.ESTADO_APROBADO)) {
            // Send notification to all associated groups
            enviarNotificacionFirebase(notificacion, appUser.getInstitucionEducativaId());
        }

        return notificacion.obtenerNotificacionDTO();
    }
    
    /**
     * Actualiza una notificación existente.
     * Solo los usuarios con rol "COLEGIO" pueden modificar notificaciones aprobadas.
     * @param id              ID de la notificación a actualizar.
     * @param notificacionDTO Datos actualizados.
     * @return Notificación actualizada en formato {@link NotificacionDTO}.
     */
    @Override
    public NotificacionDTO actualizarNotificacion(Long id, NotificacionDTO notificacionDTO) {
        Optional<Notificacion> existing = notificacionRepository.findById(id);
        // Si no existe, se retorna null
        if (!existing.isPresent())
            return null;
          // Obtener usuario autenticado
        Notificacion notificacion = existing.get();

        // Validar permisos: un profesor no puede modificar notificaciones aprobadas
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean esColegio = appUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + RolesEnum.COLEGIO));

        if (!esColegio && notificacion.getEstado().equals(Notificacion.ESTADO_APROBADO)) {
            throw new EndpointException("No tiene permiso para modificar la notificación");
        }

        String estadoAnterior = notificacion.getEstado();
        // Copiar propiedades excepto las que no deben alterarse
        BeanUtils.copyProperties(notificacionDTO, notificacion, "id", "fechaCreacion", "fechaEvaluacion", "grupos");

        // Si el estado cambió, registrar quién lo evaluó y cuándo
        if (estadoAnterior.equals(Notificacion.ESTADO_PENDIENTE)) {
            notificacion.setUsuarioEvaluador(new Usuario(appUser.getId()));
            notificacion.setFechaEvaluacion(LocalDateTime.now());
        }

        notificacion = notificacionRepository.save(notificacion);

        // Enviar FCM si se acaba de aprobar (P → A)
        if (estadoAnterior.equals(Notificacion.ESTADO_PENDIENTE) && notificacion.getEstado().equals(Notificacion.ESTADO_APROBADO)) {
            enviarNotificacionFirebase(notificacion, appUser.getInstitucionEducativaId());
        }

        return notificacion.obtenerNotificacionDTO();
    }
    /**
         * Inactiva una notificación según su identificador.
         * @param id ID de la notificación a inactivar.
         * @return Número de registros afectados.
     */
    @Override
    public int inactivarById(Long id) {
         // Marca la notificación como inactiva en la base de datos
        return notificacionRepository.inactivarNotificacionPorId(id);
    }
    
    /**
         * Lista las notificaciones visibles para un apoderado.
         * Se ordenan por defecto de forma descendente (más recientes primero).
         * @param apoderadoId ID del apoderado.
         * @param pageable    Configuración de paginación.
         * @return Página de notificaciones en formato {@link NotificacionDTO}.
     */
    @Override
    public Page<NotificacionDTO> listarNotificacionesPorApoderadoId(Long apoderadoId, Pageable pageable) {
        // Ordenar por defecto descendente
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");

        // Obtenemos los grupos a los que pertenece el alumno del apoderado
        List<Long> grupoIds = alumnoGrupoRepository.listarGrupoIdsPorApoderadoId(apoderadoId);
        if (grupoIds == null || grupoIds.isEmpty()) {
            return Page.empty(pageable);
        }
        
        // Buscar notificaciones vinculadas a esos grupos
        Page<Notificacion> notificaciones = notificacionRepository.findByGrupoIds(grupoIds, pageable);
        if (notificaciones.isEmpty()) {
            return Page.empty(pageable);
        }
        
        // Convertir las entidades en DTOs
        return notificaciones.map(Notificacion::obtenerNotificacionDTO);
    }

    /**
         * Envía una notificación aprobada a los dispositivos asociados a los grupos correspondientes.
         * @param notificacion           Entidad {@link Notificacion} a enviar.
         * @param institucionEducativaId ID de la institución educativa.
     */
    private void enviarNotificacionFirebase(Notificacion notificacion, Long institucionEducativaId) {
        Set<String> tokensSet = new HashSet<>();
        // Recorrer los grupos asociados a la notificación
        if (notificacion.getGrupos() != null) {
            for (var item : notificacion.getGrupos()) {
                 // Obtener tokens de los dispositivos de los alumnos
                List<String> tokens = tokenDispositivoService.listarTokensPorGrupoId(item.getId(), institucionEducativaId);
                if (tokens != null && !tokens.isEmpty()) {
                    // Evitar duplicados
                    tokensSet.addAll(tokens);
                }
            }
        }
        
        // Enviar notificación si existen tokens
        if (!tokensSet.isEmpty()) {
            // Título del mensaje
            fcmService.sendNotification(notificacion.getTitulo(), 
                    // Contenido del mensaje
                    notificacion.getDetalle(), 
                    // Datos adicionales
                    Map.of("notificacionId", notificacion.getId().toString()),
                    // Conversión del Set a lista
                    new ArrayList<>(tokensSet));
        }
    }
}
