package com.utp.libretago.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.filtros.FiltroInstitucionEducativa;
import com.utp.libretago.entity.InstitucionEducativa;

public interface InstitucionEducativaService {

    Page<InstitucionEducativa> buscarPorFiltros(FiltroInstitucionEducativa filtro, Pageable pageable);

    Optional<InstitucionEducativa> obtenerPorId(Long id);

    InstitucionEducativa crearInstitucion(InstitucionEducativa institucion);

    InstitucionEducativa actualizarInstitucion(Long id, InstitucionEducativa institucion);

    int inactivarById(Long id);

    List<LabelValueDTO> listarInstitucionesPorNombre(Pageable pageable, String nombre);
}
