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

    @Override
    public Page<NotificacionDTO> buscarNotificacionesPorFiltros(FiltroNotificacion filtro, Pageable pageable) {
        return notificacionRepository.findAll(filtro.generarFiltroNotificacion(), pageable).map(Notificacion::obtenerNotificacionDTO);
    }

    @Override
    public Optional<NotificacionDTO> obtenerPorId(Long id) {
        return notificacionRepository.findById(id).map(Notificacion::obtenerNotificacionConGruposDTO);
    }

    @Override
    public NotificacionDTO crearNotificacion(NotificacionDTO notificacionDTO) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        notificacionDTO.setUsuarioCreadorId(appUser.getId());

        if (appUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + RolesEnum.COLEGIO))) {
            notificacionDTO.setEstado(Notificacion.ESTADO_APROBADO);
            notificacionDTO.setUsuarioEvaluadorId(appUser.getId());
        }

        Notificacion notificacion = notificacionDTO.obtenerNotificacion();
        if (notificacion.getUsuarioEvaluador() != null) {
            notificacion.setFechaEvaluacion(LocalDateTime.now());
        }

        notificacion = notificacionRepository.save(notificacion);

        if (notificacion.getEstado().equals(Notificacion.ESTADO_APROBADO)) {
            // Send notification to all associated groups
            enviarNotificacionFirebase(notificacion, appUser.getInstitucionEducativaId());
        }

        return notificacion.obtenerNotificacionDTO();
    }

    @Override
    public NotificacionDTO actualizarNotificacion(Long id, NotificacionDTO notificacionDTO) {
        Optional<Notificacion> existing = notificacionRepository.findById(id);

        if (!existing.isPresent())
            return null;

        Notificacion notificacion = existing.get();

        // Validar que PROFESOR no pueda editar notificaciones aprobadas
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean esColegio = appUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + RolesEnum.COLEGIO));

        if (!esColegio && notificacion.getEstado().equals(Notificacion.ESTADO_APROBADO)) {
            throw new EndpointException("No tiene permiso para modificar la notificación");
        }

        String estadoAnterior = notificacion.getEstado();
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

    @Override
    public int inactivarById(Long id) {
        return notificacionRepository.inactivarNotificacionPorId(id);
    }

    @Override
    public Page<NotificacionDTO> listarNotificacionesPorApoderadoId(Long apoderadoId, Pageable pageable) {
        // Ordenar por defecto por id de forma descendente pa que se vean los más nuevos primero
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");

        // Obtenemos los grupos a los que pertenece el alumno del apoderado
        List<Long> grupoIds = alumnoGrupoRepository.listarGrupoIdsPorApoderadoId(apoderadoId);
        if (grupoIds == null || grupoIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Notificacion> notificaciones = notificacionRepository.findByGrupoIds(grupoIds, pageable);
        if (notificaciones.isEmpty()) {
            return Page.empty(pageable);
        }

        return notificaciones.map(Notificacion::obtenerNotificacionDTO);
    }

    private void enviarNotificacionFirebase(Notificacion notificacion, Long institucionEducativaId) {
        Set<String> tokensSet = new HashSet<>();

        if (notificacion.getGrupos() != null) {
            for (var item : notificacion.getGrupos()) {
                List<String> tokens = tokenDispositivoService.listarTokensPorGrupoId(item.getId(), institucionEducativaId);
                if (tokens != null && !tokens.isEmpty()) {
                    tokensSet.addAll(tokens);
                }
            }
        }

        if (!tokensSet.isEmpty()) {
            fcmService.sendNotification(notificacion.getTitulo(), //
                    notificacion.getDetalle(), //
                    Map.of("notificacionId", notificacion.getId().toString()), //
                    new ArrayList<>(tokensSet));
        }
    }
}
