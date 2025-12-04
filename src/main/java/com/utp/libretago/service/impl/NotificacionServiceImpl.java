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
import org.springframework.transaction.annotation.Transactional;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.NotificacionDTO;
import com.utp.libretago.classes.dto.NotificationStatsDTO;
import com.utp.libretago.classes.dto.NotificationStatsInstitucionDTO;
import com.utp.libretago.classes.dto.NotificationStatsMultiLineDTO;
import com.utp.libretago.classes.filtros.FiltroNotificacion;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.entity.Grupo;
import com.utp.libretago.entity.InstitucionEducativa;
import com.utp.libretago.entity.Notificacion;
import com.utp.libretago.entity.NotificacionGrupo;
import com.utp.libretago.entity.Usuario;
import com.utp.libretago.repository.AlumnoGrupoRepository;
import com.utp.libretago.repository.GrupoRepository;
import com.utp.libretago.repository.InstitucionEducativaRepository;
import com.utp.libretago.repository.NotificacionGrupoRepository;
import com.utp.libretago.repository.NotificacionRepository;
import com.utp.libretago.service.FirebaseMessageService;
import com.utp.libretago.service.NotificacionService;
import com.utp.libretago.service.TokenDispositivoService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.exception.EndpointException;

/**
 * Implementación del servicio {@link NotificacionService} para la gestión de notificaciones. Incluye creación,
 * actualización, búsqueda, inactivación y envío de notificaciones mediante Firebase Cloud Messaging (FCM).
 * 
 * @author Roberto
 * @version 1.0
 * @since 2025-10-28
 */
@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private NotificacionGrupoRepository notificacionGrupoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private TokenDispositivoService tokenDispositivoService;

    @Autowired
    private FirebaseMessageService fcmService;

    @Autowired
    private AlumnoGrupoRepository alumnoGrupoRepository;

    @Autowired
    private InstitucionEducativaRepository institucionEducativaRepository;

    /**
     * Busca notificaciones aplicando filtros con paginación.
     * 
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
     * 
     * @param id ID de la notificación.
     * @return Optional con la notificación en formato {@link NotificacionDTO}.
     */
    @Override
    public Optional<NotificacionDTO> obtenerPorId(Long id) {
        // Buscar por ID y convertir a DTO con grupos asociados
        Optional<Notificacion> notificacionOpt = notificacionRepository.findById(id);
        if (!notificacionOpt.isPresent()) {
            return Optional.empty();
        }

        Notificacion notificacion = notificacionOpt.get();
        // Obtener los IDs de grupos asociados
        List<Long> grupoIds = notificacionGrupoRepository.listarGrupoIdsPorNotificacionId(id);
        // Cargar las entidades Grupo completas
        List<Grupo> grupos = grupoRepository.findAllById(grupoIds);

        return Optional.of(notificacion.obtenerNotificacionConGruposDTO(grupos));
    }

    /**
     * Crea una nueva notificación. Si el usuario autenticado es del rol "COLEGIO", la notificación se aprueba
     * automáticamente y se envía vía Firebase.
     * 
     * @param notificacionDTO Datos de la notificación a crear.
     * @return Notificación creada en formato {@link NotificacionDTO}.
     */
    @Override
    public NotificacionDTO crearNotificacion(NotificacionDTO notificacionDTO) {
        // Obtener usuario autenticado
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        notificacionDTO.setUsuarioCreadorId(appUser.getId());
        // Si el usuario es COLEGIO, se aprueba automáticamente
        if (appUser.hasRole(RolesEnum.COLEGIO)) {
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

        // Guardar las relaciones con grupos en la tabla intermedia
        if (notificacionDTO.getGrupos() != null && !notificacionDTO.getGrupos().isEmpty()) {
            for (var grupoDTO : notificacionDTO.getGrupos()) {
                NotificacionGrupo notificacionGrupo = new NotificacionGrupo(notificacion.getId(), grupoDTO.value());
                notificacionGrupoRepository.save(notificacionGrupo);
            }
        }

        if (notificacion.getEstado().equals(Notificacion.ESTADO_APROBADO)) {
            // Send notification to all associated groups
            enviarNotificacionFirebase(notificacion.getId(), appUser.getInstitucionEducativaId());
        }

        return notificacion.obtenerNotificacionDTO();
    }

    /**
     * Actualiza una notificación existente. Solo los usuarios con rol "COLEGIO" pueden modificar notificaciones aprobadas.
     * 
     * @param id              ID de la notificación a actualizar.
     * @param notificacionDTO Datos actualizados.
     * @return Notificación actualizada en formato {@link NotificacionDTO}.
     */
    @Override
    @Transactional
    public NotificacionDTO actualizarNotificacion(Long id, NotificacionDTO notificacionDTO) {
        Optional<Notificacion> existing = notificacionRepository.findById(id);
        // Si no existe, se retorna null
        if (!existing.isPresent())
            return null;
        // Obtener usuario autenticado
        Notificacion notificacion = existing.get();

        // Validar permisos: un profesor no puede modificar notificaciones aprobadas
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean esColegio = appUser.hasRole(RolesEnum.COLEGIO);

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

        // Actualizar grupos: eliminar relaciones existentes y crear nuevas
        notificacionGrupoRepository.eliminarPorNotificacionId(id);
        if (notificacionDTO.getGrupos() != null && !notificacionDTO.getGrupos().isEmpty()) {
            for (var grupoDTO : notificacionDTO.getGrupos()) {
                NotificacionGrupo notificacionGrupo = new NotificacionGrupo(id, grupoDTO.value());
                notificacionGrupoRepository.save(notificacionGrupo);
            }
        }

        // Enviar FCM si se acaba de aprobar (P → A)
        if (estadoAnterior.equals(Notificacion.ESTADO_PENDIENTE) && notificacion.getEstado().equals(Notificacion.ESTADO_APROBADO)) {
            enviarNotificacionFirebase(id, appUser.getInstitucionEducativaId());
        }

        return notificacion.obtenerNotificacionDTO();
    }

    /**
     * Inactiva una notificación según su identificador.
     * 
     * @param id ID de la notificación a inactivar.
     * @return Número de registros afectados.
     */
    @Override
    public int inactivarById(Long id) {
        // Marca la notificación como inactiva en la base de datos
        return notificacionRepository.inactivarNotificacionPorId(id);
    }

    /**
     * Lista las notificaciones visibles para un apoderado. Se ordenan por defecto de forma descendente (más recientes
     * primero).
     * 
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
     * Lista las notificaciones creadas por un usuario específico.
     * 
     * @param usuarioCreadorId ID del usuario creador.
     * @param pageable         Configuración de paginación.
     * @return Página de notificaciones en formato {@link NotificacionDTO}.
     */
    @Override
    public Page<NotificacionDTO> listarNotificacionesPorUsuarioCreadorId(Long usuarioCreadorId, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");
        return notificacionRepository.findByUsuarioCreadorIdAndActivoTrue(usuarioCreadorId, pageable).map(Notificacion::obtenerNotificacionDTO);
    }

    /**
     * Envía una notificación aprobada a los dispositivos asociados a los grupos correspondientes.
     * 
     * @param notificacionId         ID de la notificación a enviar.
     * @param institucionEducativaId ID de la institución educativa.
     */
    private void enviarNotificacionFirebase(Long notificacionId, Long institucionEducativaId) {
        // Cargar la notificación
        Optional<Notificacion> notificacionOpt = notificacionRepository.findById(notificacionId);
        if (!notificacionOpt.isPresent()) {
            return;
        }

        Notificacion notificacion = notificacionOpt.get();
        Set<String> tokensSet = new HashSet<>();

        // Obtener los IDs de grupos asociados a la notificación
        List<Long> grupoIds = notificacionGrupoRepository.listarGrupoIdsPorNotificacionId(notificacionId);

        // Recorrer los grupos asociados a la notificación
        if (grupoIds != null && !grupoIds.isEmpty()) {
            for (Long grupoId : grupoIds) {
                // Obtener tokens de los dispositivos de los alumnos
                List<String> tokens = tokenDispositivoService.listarTokensPorGrupoId(grupoId, institucionEducativaId);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NotificationStatsDTO> obtenerEstadisticasNotificaciones(Long institucionId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(2);
        return notificacionRepository.countNotificacionesPorDia(institucionId, startDate, endDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NotificationStatsMultiLineDTO> obtenerEstadisticasNotificacionesTodasInstituciones() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(2);

        // Obtener todas las instituciones activas
        List<InstitucionEducativa> instituciones = institucionEducativaRepository.findAll()
                .stream()
                .filter(i -> Boolean.TRUE.equals(i.getActivo()))
                .toList();

        // Obtener todas las estadísticas en una sola consulta optimizada
        List<NotificationStatsInstitucionDTO> allStats = notificacionRepository
                .countNotificacionesPorDiaTodasInstituciones(startDate, endDate);

        // Agrupar estadísticas por institucionId
        Map<Long, List<NotificationStatsDTO>> statsPorInstitucion = allStats.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        NotificationStatsInstitucionDTO::institucionId,
                        java.util.stream.Collectors.mapping(
                                stat -> new NotificationStatsDTO(stat.date(), stat.count()),
                                java.util.stream.Collectors.toList())));

        // Construir la respuesta incluyendo instituciones sin notificaciones
        List<NotificationStatsMultiLineDTO> resultado = new ArrayList<>();
        for (InstitucionEducativa inst : instituciones) {
            List<NotificationStatsDTO> stats = statsPorInstitucion.getOrDefault(inst.getId(), new ArrayList<>());
            resultado.add(new NotificationStatsMultiLineDTO(inst.getNombre(), inst.getId(), stats));
        }

        return resultado;
    }
}
