package com.utp.libretago.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.GrupoDTO;
import com.utp.libretago.classes.dto.IdLabelDTO;
import com.utp.libretago.classes.filtros.FiltroGrupo;
import com.utp.libretago.entity.AlumnoGrupo;
import com.utp.libretago.entity.Grupo;
import com.utp.libretago.repository.AlumnoGrupoRepository;
import com.utp.libretago.repository.GrupoRepository;
import com.utp.libretago.service.GrupoService;
/**
 * Implementación del servicio {@link GrupoService} para la gestión de grupos.
 * Permite buscar, crear, actualizar e inactivar grupos, así como gestionar alumnos asociados.
  * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
@Service
public class GrupoServiceImpl implements GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private AlumnoGrupoRepository alumnoGrupoRepository;
    /**
         * Busca grupos según los filtros proporcionados con paginación.
         * @param filtros  Filtros de búsqueda.
         * @param pageable Configuración de paginación.
         * @return Página de grupos en formato {@link GrupoDTO}.
     */
    @Override
    public Page<GrupoDTO> buscarGruposPorFiltros(FiltroGrupo filtros, Pageable pageable) {
        return grupoRepository.findAll(filtros.generarFiltroGrupo(), pageable).map(item -> item.obtenerGrupoDTO());
    }

    /**
         * Lista grupos por nombre usando filtros y paginación.
         * @param pageable Configuración de paginación.
         * @param filtros  Filtros de búsqueda.
         * @return Lista de grupos en formato {@link IdLabelDTO}.
     */
    @Override
    public List<IdLabelDTO> listarGruposPorNombre(Pageable pageable, FiltroGrupo filtros) {
        return grupoRepository.findAll(filtros.generarFiltroGrupo(), pageable).map(item -> item.obtenerIdLabelDTO()).toList();
    }
    /**
         * Obtiene un grupo por su ID.
         * @param id ID del grupo.
         * @return Optional con {@link GrupoDTO} si existe, vacío si no.
     */
    @Override
    public Optional<GrupoDTO> obtenerPorId(Long id) {
        var grupo = grupoRepository.findById(id);

        if (!grupo.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(grupo.get().obtenerGrupoDTO());
    /**
         * Crea un nuevo grupo y registra los alumnos asociados.
         * @param grupoDTO Datos del grupo a crear.
         * @return Grupo creado en formato {@link GrupoDTO}.
     */
    @Override
    public GrupoDTO crearGrupo(GrupoDTO grupoDTO) {
        var grupo = grupoRepository.save(grupoDTO.obtenerGrupo());

        registrarAlumnosGrupo(grupoDTO.getAlumnosNuevos(), grupo.getId());

        return grupo.obtenerGrupoDTO();
    }
    
    /**
         * Actualiza un grupo existente y gestiona los alumnos nuevos y eliminados.
         * @param id ID del grupo a actualizar.
         * @param grupoDTO Datos del grupo actualizados.
         * @return Grupo actualizado en formato {@link GrupoDTO}.
     */
    @Override
    public GrupoDTO actualizarGrupo(Long id, GrupoDTO grupoDTO) {
        Optional<Grupo> existing = grupoRepository.findById(id);
        if (!existing.isPresent())
            return null;

        Grupo e = existing.get();
        BeanUtils.copyProperties(grupoDTO, e, "id", "fechaCreacion");
        grupoRepository.save(e);

        if (grupoDTO.getAlumnosEliminadosIds() != null && !grupoDTO.getAlumnosEliminadosIds().isEmpty()) {
            var listaIds = grupoDTO.getAlumnosEliminadosIds().stream().map(item -> {
                return new AlumnoGrupo(item, id);
            }).toList();

            alumnoGrupoRepository.deleteAll(listaIds);
        }

        registrarAlumnosGrupo(grupoDTO.getAlumnosNuevos(), id);

        return grupoDTO;
    }
    /**
     * Registra los alumnos asociados a un grupo.
     * @param alumnos  Lista de alumnos a registrar.
     * @param grupoId  ID del grupo.
     */
    private void registrarAlumnosGrupo(List<Alumno2DTO> alumnos, Long grupoId) {
        if (alumnos == null || alumnos.isEmpty())
            return;

        var alumnosGrupo = alumnos.stream().map(item -> {
            return new AlumnoGrupo(item.id(), grupoId);
        }).toList();

        alumnoGrupoRepository.saveAll(alumnosGrupo);
    }

    /**
     * Inactiva un grupo según su ID.
     * @param id ID del grupo a inactivar.
     * @return Número de registros afectados.
     */
    @Override
    @Transactional
    public int inactivarById(Long id) {
        return grupoRepository.inactivarGrupoPorId(id);
    }
    /**
         * Lista los alumnos asociados a un grupo específico dentro de una institución.
         * @param grupoId ID del grupo.
         * @param institucionEducativaId ID de la institución educativa.
         * @return Lista de alumnos en formato {@link Alumno2DTO}.
     */
    @Override
    public List<Alumno2DTO> listarAlumnosPorGrupoId(Long grupoId, Long institucionEducativaId) {
        var alumnos = alumnoGrupoRepository.listarAlumnosPorGrupoId(grupoId, institucionEducativaId).stream().map(item -> {
            return item.obtenerAlumno2DTO();
        }).toList();

        return alumnos;
    }
}
