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
/**
    * Implementación del servicio {@link InstitucionEducativaService}.
    * Gestiona la creación, actualización, inactivación y consulta de instituciones educativas.
    * @author Roberto Anton
    * @version 1.0
    * @since 2025-10-28
 */
@Service
public class InstitucionEducativaServiceImpl implements InstitucionEducativaService {

    @Autowired
    private InstitucionEducativaRepository institucionRepository;
    
        /**
         * Busca instituciones educativas según los filtros proporcionados con paginación.
         */
    @Override
    public Page<InstitucionEducativa> buscarPorFiltros(FiltroInstitucionEducativa filtro, Pageable pageable) {
        // Generar el filtro y consultar con paginación
        return institucionRepository.findAll(filtro.generarFiltro(), pageable);
    }
    /**
         * Obtiene una institución educativa por su ID.
     */
    @Override
    public Optional<InstitucionEducativa> obtenerPorId(Long id) {
        // Buscar por ID y devolver Optional
        return institucionRepository.findById(id);
    }
    /**
         * Crea una nueva institución educativa.
     */
    @Override
    public InstitucionEducativa crearInstitucion(InstitucionEducativa institucion) {
        // Guardar la institución en la base de datos
        return institucionRepository.save(institucion);
    }
    
    /**
         * Actualiza una institución educativa existente.
     */
    @Override
    public InstitucionEducativa actualizarInstitucion(Long id, InstitucionEducativa institucion) {
        Optional<InstitucionEducativa> existing = institucionRepository.findById(id);
         // Si no existe la institución, lanzar excepción
        if (!existing.isPresent()) {
            throw new EndpointException("La institución educativa no existe");
        }

        InstitucionEducativa e = existing.get();
        // Copiar propiedades de la entidad recibida a la existente, excepto id y fechaCreacion
        BeanUtils.copyProperties(institucion, e, "id", "fechaCreacion");
        // Guardar los cambios
        return institucionRepository.save(e);
    }
    
    /**
         * Inactiva una institución educativa por su ID.
     */
    @Override
    @Transactional
    public int inactivarById(Long id) {
        // Llamar al repositorio para actualizar el estado a inactivo
        return institucionRepository.inactivarInstitucionPorId(id);
    }

    /**
         * Lista instituciones cuyo nombre coincida parcialmente con el texto dado.
     */
    @Override
    public List<LabelValueDTO> listarInstitucionesPorNombre(Pageable pageable, String nombre) {
        // Buscar instituciones por nombre con LIKE ignorando mayúsculas/minúsculas
        var datos = institucionRepository.findByNombreLikeIgnoreCase("%" + nombre + "%", pageable);
        
        // Convertir a lista de DTOs LabelValueDTO para mostrar en interfaces
        return datos.stream().map(item -> {
            return new LabelValueDTO(item.getNombre(), item.getId().toString());
        }).toList();
    }
}
