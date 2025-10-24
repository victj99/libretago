package com.utp.libretago.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.GrupoDTO;
import com.utp.libretago.classes.dto.IdLabelDTO;
import com.utp.libretago.classes.filtros.FiltroGrupo;

public interface GrupoService {
    Page<GrupoDTO> buscarGruposPorFiltros(FiltroGrupo filtros, Pageable pageable);

    List<IdLabelDTO> listarGruposPorNombre(Pageable pageable, FiltroGrupo filtros);

    Optional<GrupoDTO> obtenerPorId(Long id);

    GrupoDTO crearGrupo(GrupoDTO grupoDTO);

    GrupoDTO actualizarGrupo(Long id, GrupoDTO grupoDTO);

    int inactivarById(Long id);

    List<Alumno2DTO> listarAlumnosPorGrupoId(Long grupoId, Long institucionEducativaId);
}
