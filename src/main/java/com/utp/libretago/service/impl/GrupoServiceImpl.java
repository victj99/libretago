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

@Service
public class GrupoServiceImpl implements GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private AlumnoGrupoRepository alumnoGrupoRepository;

    @Override
    public Page<GrupoDTO> buscarGruposPorFiltros(FiltroGrupo filtros, Pageable pageable) {
        return grupoRepository.findAll(filtros.generarFiltroGrupo(), pageable).map(item -> item.obtenerGrupoDTO());
    }

    @Override
    public List<IdLabelDTO> listarGruposPorNombre(Pageable pageable, FiltroGrupo filtros) {
        return grupoRepository.findAll(filtros.generarFiltroGrupo(), pageable).map(item -> item.obtenerIdLabelDTO()).toList();
    }

    @Override
    public Optional<GrupoDTO> obtenerPorId(Long id) {
        var grupo = grupoRepository.findById(id);

        if (!grupo.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(grupo.get().obtenerGrupoDTO());
    }

    @Override
    public GrupoDTO crearGrupo(GrupoDTO grupoDTO) {
        var grupo = grupoRepository.save(grupoDTO.obtenerGrupo());

        registrarAlumnosGrupo(grupoDTO.getAlumnosNuevos(), grupo.getId());

        return grupo.obtenerGrupoDTO();
    }

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

    private void registrarAlumnosGrupo(List<Alumno2DTO> alumnos, Long grupoId) {
        if (alumnos == null || alumnos.isEmpty())
            return;

        var alumnosGrupo = alumnos.stream().map(item -> {
            return new AlumnoGrupo(item.id(), grupoId);
        }).toList();

        alumnoGrupoRepository.saveAll(alumnosGrupo);
    }

    @Override
    @Transactional
    public int inactivarById(Long id) {
        return grupoRepository.inactivarGrupoPorId(id);
    }

    @Override
    public List<Alumno2DTO> listarAlumnosPorGrupoId(Long grupoId, Long institucionEducativaId) {
        var alumnos = alumnoGrupoRepository.listarAlumnosPorGrupoId(grupoId, institucionEducativaId).stream().map(item -> {
            return item.obtenerAlumno2DTO();
        }).toList();

        return alumnos;
    }
}
