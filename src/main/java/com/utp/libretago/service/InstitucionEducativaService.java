package com.utp.libretago.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.filtros.FiltroInstitucionEducativa;
import com.utp.libretago.entity.InstitucionEducativa;
/**
     * Servicio para la gestión de instituciones educativas.
     * Permite buscar, crear, actualizar, inactivar y listar instituciones.
     * @author Roberto Anton
     * @version 1.0
     * @since 2025-10-28
 */
public interface InstitucionEducativaService {
    
    /**
         * Busca instituciones educativas aplicando filtros y paginación.
         * @param filtro Filtros de búsqueda {@link FiltroInstitucionEducativa}.
         * @param pageable Configuración de paginación.
         * @return Página de instituciones educativas.
     */
    Page<InstitucionEducativa> buscarPorFiltros(FiltroInstitucionEducativa filtro, Pageable pageable);

    /**
         * Obtiene una institución educativa por su ID.
         * @param id ID de la institución.
         * @return Optional con la institución educativa si existe, vacío si no.
     */
    Optional<InstitucionEducativa> obtenerPorId(Long id);

    /**
         * Crea una nueva institución educativa.
         * @param institucion Datos de la institución a crear.
         * @return Institución educativa creada.
     */
    InstitucionEducativa crearInstitucion(InstitucionEducativa institucion);
    /**
         * Actualiza una institución educativa existente.
         * @param id ID de la institución a actualizar.
         * @param institucion Datos actualizados de la institución.
         * @return Institución educativa actualizada.
     */
    InstitucionEducativa actualizarInstitucion(Long id, InstitucionEducativa institucion);
    /**
         * Inactiva una institución educativa según su ID.
         * @param id ID de la institución a inactivar.
         * @return Número de registros afectados.
     */
    int inactivarById(Long id);

    /**
         * Lista instituciones educativas por nombre con paginación.
         * @param pageable Configuración de paginación.
         * @param nombre Nombre o parte del nombre a buscar.
         * @return Lista de instituciones en formato {@link LabelValueDTO}.
     */
    List<LabelValueDTO> listarInstitucionesPorNombre(Pageable pageable, String nombre);
}
