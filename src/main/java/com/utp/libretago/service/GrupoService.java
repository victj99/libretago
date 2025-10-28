package com.utp.libretago.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.GrupoDTO;
import com.utp.libretago.classes.dto.IdLabelDTO;
import com.utp.libretago.classes.filtros.FiltroGrupo;
/**
 * Servicio para la gestión de grupos dentro de la institución educativa.
 * Permite buscar, crear, actualizar, inactivar grupos y listar alumnos asociados.
 * 
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
public interface GrupoService {
    /**
         * Busca grupos según los filtros proporcionados con paginación.
         * @param filtros  Filtros de búsqueda de tipo {@link FiltroGrupo}.
         * @param pageable Configuración de paginación.
         * @return Página de grupos que cumplen los filtros.
     */
    Page<GrupoDTO> buscarGruposPorFiltros(FiltroGrupo filtros, Pageable pageable);
    /**
         * Lista grupos por nombre usando filtros y paginación.
         * @param pageable Configuración de paginación.
         * @param filtros  Filtros de búsqueda.
         * @return Lista de grupos en formato {@link IdLabelDTO}.
     */
    List<IdLabelDTO> listarGruposPorNombre(Pageable pageable, FiltroGrupo filtros);
    /**
         * Obtiene un grupo por su ID.
         * @param id Identificador del grupo.
         * @return Optional con el grupo encontrado o vacío si no existe.
     */
    Optional<GrupoDTO> obtenerPorId(Long id);
    /**
         * Crea un nuevo grupo.
         * @param grupoDTO Datos del grupo a crear.
         * @return Grupo creado con su ID generado.
     */
    GrupoDTO crearGrupo(GrupoDTO grupoDTO);
    /**
         * Actualiza un grupo existente.
         * @param ID del grupo a actualizar.
         * @param grupoDTO Datos actualizados del grupo.
         * @return Grupo actualizado.
     */
    GrupoDTO actualizarGrupo(Long id, GrupoDTO grupoDTO);
    /**
         * Inactiva un grupo según su ID.
         * @param id ID del grupo a inactivar.
         * @return Número de registros afectados (generalmente 1 si se inactivó correctamente).
     */
    int inactivarById(Long id);
    /**
         * Lista los alumnos asociados a un grupo específico dentro de una institución.
         * @param grupoId, ID del grupo.
         * @param institucionEducativaId ID de la institución educativa a la que pertenece el grupo.
         * @return Lista de alumnos en formato {@link Alumno2DTO}.
     */
    List<Alumno2DTO> listarAlumnosPorGrupoId(Long grupoId, Long institucionEducativaId);
}
