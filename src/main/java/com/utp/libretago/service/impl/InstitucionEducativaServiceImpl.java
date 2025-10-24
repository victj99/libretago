package com.utp.libretago.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.filtros.FiltroInstitucionEducativa;
import com.utp.libretago.entity.InstitucionEducativa;
import com.utp.libretago.repository.InstitucionEducativaRepository;
import com.utp.libretago.service.InstitucionEducativaService;
import com.vaadin.hilla.exception.EndpointException;

@Service
public class InstitucionEducativaServiceImpl implements InstitucionEducativaService {

    @Autowired
    private InstitucionEducativaRepository institucionRepository;

    @Override
    public Page<InstitucionEducativa> buscarPorFiltros(FiltroInstitucionEducativa filtro, Pageable pageable) {
        return institucionRepository.findAll(filtro.generarFiltro(), pageable);
    }

    @Override
    public Optional<InstitucionEducativa> obtenerPorId(Long id) {
        return institucionRepository.findById(id);
    }

    @Override
    public InstitucionEducativa crearInstitucion(InstitucionEducativa institucion) {
        return institucionRepository.save(institucion);
    }

    @Override
    public InstitucionEducativa actualizarInstitucion(Long id, InstitucionEducativa institucion) {
        Optional<InstitucionEducativa> existing = institucionRepository.findById(id);

        if (!existing.isPresent()) {
            throw new EndpointException("La institución educativa no existe");
        }

        InstitucionEducativa e = existing.get();
        BeanUtils.copyProperties(institucion, e, "id", "fechaCreacion");

        return institucionRepository.save(e);
    }

    @Override
    @Transactional
    public int inactivarById(Long id) {
        return institucionRepository.inactivarInstitucionPorId(id);
    }

    @Override
    public List<LabelValueDTO> listarInstitucionesPorNombre(Pageable pageable, String nombre) {
        var datos = institucionRepository.findByNombreLikeIgnoreCase("%" + nombre + "%", pageable);

        return datos.stream().map(item -> {
            return new LabelValueDTO(item.getNombre(), item.getId().toString());
        }).toList();
    }
}
