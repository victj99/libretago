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
import com.utp.libretago.classes.dto.EventoDTO;
import com.utp.libretago.classes.dto.EventoStatsDTO;
import com.utp.libretago.classes.dto.EventoStatsInstitucionDTO;
import com.utp.libretago.classes.dto.EventoStatsMultiLineDTO;
import com.utp.libretago.classes.filtros.FiltroEvento;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.entity.Grupo;
import com.utp.libretago.entity.InstitucionEducativa;
import com.utp.libretago.entity.Evento;
import com.utp.libretago.entity.EventoGrupo;
import com.utp.libretago.entity.Usuario;
import com.utp.libretago.repository.AlumnoGrupoRepository;
import com.utp.libretago.repository.GrupoRepository;
import com.utp.libretago.repository.InstitucionEducativaRepository;
import com.utp.libretago.repository.EventoGrupoRepository;
import com.utp.libretago.repository.EventoRepository;
import com.utp.libretago.service.FirebaseMessageService;
import com.utp.libretago.service.EventoService;
import com.utp.libretago.service.TokenDispositivoService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.exception.EndpointException;

/**
 * Implementación del servicio {@link EventoService} para la gestión de eventos.
 * 
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
@Service
public class EventoServiceImpl implements EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EventoGrupoRepository eventoGrupoRepository;

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

    @Override
    public Page<EventoDTO> buscarEventosPorFiltros(FiltroEvento filtro, Pageable pageable) {
        var datos = eventoRepository.findAll(filtro.generarFiltroEvento(), pageable);
        return datos.map(Evento::obtenerEventoDTO);
    }

    @Override
    public Optional<EventoDTO> obtenerPorId(Long id) {
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (!eventoOpt.isPresent()) {
            return Optional.empty();
        }

        Evento evento = eventoOpt.get();
        List<Long> grupoIds = eventoGrupoRepository.listarGrupoIdsPorEventoId(id);
        List<Grupo> grupos = grupoRepository.findAllById(grupoIds);

        return Optional.of(evento.obtenerEventoConGruposDTO(grupos));
    }

    @Override
    public EventoDTO crearEvento(EventoDTO eventoDTO) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        eventoDTO.setUsuarioCreadorId(appUser.getId());

        if (appUser.hasRole(RolesEnum.COLEGIO)) {
            eventoDTO.setEstado(Evento.ESTADO_APROBADO);
            eventoDTO.setUsuarioEvaluadorId(appUser.getId());
        }

        Evento evento = eventoDTO.obtenerEvento();

        if (evento.getUsuarioEvaluador() != null) {
            evento.setFechaEvaluacion(LocalDateTime.now());
        }

        evento = eventoRepository.save(evento);

        if (eventoDTO.getGrupos() != null && !eventoDTO.getGrupos().isEmpty()) {
            for (var grupoDTO : eventoDTO.getGrupos()) {
                EventoGrupo eventoGrupo = new EventoGrupo(evento.getId(), grupoDTO.value());
                eventoGrupoRepository.save(eventoGrupo);
            }
        }

        if (evento.getEstado().equals(Evento.ESTADO_APROBADO)) {
            enviarNotificacionFirebase(evento.getId(), appUser.getInstitucionEducativaId());
        }

        return evento.obtenerEventoDTO();
    }

    @Override
    @Transactional
    public EventoDTO actualizarEvento(Long id, EventoDTO eventoDTO) {
        Optional<Evento> existing = eventoRepository.findById(id);
        if (!existing.isPresent())
            return null;

        Evento evento = existing.get();

        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean esColegio = appUser.hasRole(RolesEnum.COLEGIO);

        if (!esColegio && evento.getEstado().equals(Evento.ESTADO_APROBADO)) {
            throw new EndpointException("No tiene permiso para modificar el evento");
        }

        String estadoAnterior = evento.getEstado();
        BeanUtils.copyProperties(eventoDTO, evento, "id", "fechaCreacion", "fechaEvaluacion", "grupos");

        if (estadoAnterior.equals(Evento.ESTADO_PENDIENTE)) {
            evento.setUsuarioEvaluador(new Usuario(appUser.getId()));
            evento.setFechaEvaluacion(LocalDateTime.now());
        }

        evento = eventoRepository.save(evento);

        eventoGrupoRepository.eliminarPorEventoId(id);
        if (eventoDTO.getGrupos() != null && !eventoDTO.getGrupos().isEmpty()) {
            for (var grupoDTO : eventoDTO.getGrupos()) {
                EventoGrupo eventoGrupo = new EventoGrupo(id, grupoDTO.value());
                eventoGrupoRepository.save(eventoGrupo);
            }
        }

        if (estadoAnterior.equals(Evento.ESTADO_PENDIENTE) && evento.getEstado().equals(Evento.ESTADO_APROBADO)) {
            enviarNotificacionFirebase(id, appUser.getInstitucionEducativaId());
        }

        return evento.obtenerEventoDTO();
    }

    @Override
    public int inactivarById(Long id) {
        return eventoRepository.inactivarEventoPorId(id);
    }

    @Override
    public Page<EventoDTO> listarEventosPorApoderadoId(Long apoderadoId, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");

        List<Long> grupoIds = alumnoGrupoRepository.listarGrupoIdsPorApoderadoId(apoderadoId);
        if (grupoIds == null || grupoIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Evento> eventos = eventoRepository.findByGrupoIds(grupoIds, pageable);
        if (eventos.isEmpty()) {
            return Page.empty(pageable);
        }

        return eventos.map(Evento::obtenerEventoDTO);
    }

    @Override
    public Page<EventoDTO> listarEventosPorUsuarioCreadorId(Long usuarioCreadorId, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");
        return eventoRepository.findByUsuarioCreadorIdAndActivoTrue(usuarioCreadorId, pageable).map(Evento::obtenerEventoDTO);
    }

    private void enviarNotificacionFirebase(Long eventoId, Long institucionEducativaId) {
        Optional<Evento> eventoOpt = eventoRepository.findById(eventoId);
        if (!eventoOpt.isPresent()) {
            return;
        }

        Evento evento = eventoOpt.get();
        Set<String> tokensSet = new HashSet<>();

        List<Long> grupoIds = eventoGrupoRepository.listarGrupoIdsPorEventoId(eventoId);

        if (grupoIds != null && !grupoIds.isEmpty()) {
            for (Long grupoId : grupoIds) {
                List<String> tokens = tokenDispositivoService.listarTokensPorGrupoId(grupoId, institucionEducativaId);
                if (tokens != null && !tokens.isEmpty()) {
                    tokensSet.addAll(tokens);
                }
            }
        }

        if (!tokensSet.isEmpty()) {
            fcmService.sendNotification(evento.getTitulo(), evento.getDetalle(), Map.of("eventoId", evento.getId().toString()),
                    new ArrayList<>(tokensSet));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventoStatsDTO> obtenerEstadisticasEventos(Long institucionId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(2);
        return eventoRepository.countEventosPorDia(institucionId, startDate, endDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventoStatsMultiLineDTO> obtenerEstadisticasEventosTodasInstituciones() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(2);

        // Obtener todas las instituciones activas
        List<InstitucionEducativa> instituciones = institucionEducativaRepository.findAll()
                .stream()
                .filter(i -> Boolean.TRUE.equals(i.getActivo()))
                .toList();

        // Obtener todas las estadísticas en una sola consulta optimizada
        List<EventoStatsInstitucionDTO> allStats = eventoRepository
                .countEventosPorDiaTodasInstituciones(startDate, endDate);

        // Agrupar estadísticas por institucionId
        Map<Long, List<EventoStatsDTO>> statsPorInstitucion = allStats.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        EventoStatsInstitucionDTO::institucionId,
                        java.util.stream.Collectors.mapping(
                                stat -> new EventoStatsDTO(stat.date(), stat.count()),
                                java.util.stream.Collectors.toList())));

        // Construir la respuesta incluyendo instituciones sin eventos
        List<EventoStatsMultiLineDTO> resultado = new ArrayList<>();
        for (InstitucionEducativa inst : instituciones) {
            List<EventoStatsDTO> stats = statsPorInstitucion.getOrDefault(inst.getId(), new ArrayList<>());
            resultado.add(new EventoStatsMultiLineDTO(inst.getNombre(), inst.getId(), stats));
        }

        return resultado;
    }
}
