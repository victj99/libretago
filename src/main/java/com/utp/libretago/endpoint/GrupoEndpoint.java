package com.utp.libretago.endpoint;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.GrupoDTO;
import com.utp.libretago.classes.dto.IdLabelDTO;
import com.utp.libretago.classes.filtros.FiltroGrupo;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.service.GrupoService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;


/**
 * Endpoint encargado de la gestión de grupos dentro de una institución educativa.
 * <p>
 * Permite a los usuarios con el rol {@link RolesEnum#COLEGIO} realizar operaciones CRUD
 * sobre grupos, así como listar alumnos asociados o buscar grupos por nombre.
 * </p>
 * 
 * <p>Utiliza el servicio {@link GrupoService} para delegar la lógica de negocio.</p>
 * 
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */


@Endpoint
@RolesAllowed({ RolesEnum.COLEGIO })
public class GrupoEndpoint {
    
    /** Servicio encargado de la lógica de negocio relacionada con grupos. */
    @Autowired
    private GrupoService grupoService;

 /**
         * Busca grupos aplicando filtros y paginación.
         * @param filtro   criterios de búsqueda (nombre, estado, institución, etc.)
         * @param pageable configuración de paginación y ordenamiento
         * @return una página con los resultados de tipo {@link GrupoDTO}
     */
    @NonNull
    public Page<@NonNull GrupoDTO> buscarPorFiltros(FiltroGrupo filtro, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");
        return grupoService.buscarGruposPorFiltros(filtro, pageable);
    }


 /**
         * Obtiene un grupo por su identificador único.
         * @param id identificador del grupo
         * @return el grupo encontrado como {@link GrupoDTO} o {@code null} si no existe
     */
    public GrupoDTO obtenerGrupo(Long id) {
        var grupo = grupoService.obtenerPorId(id);
        if (grupo.isPresent()) {
            return grupo.get();
        }
        return null;
    }
    /**
         * Crea un nuevo grupo en la institución del usuario autenticado.
         * @param data datos del grupo a crear
         * @return el identificador del grupo creado
     */
    public Long crearGrupo(GrupoDTO data) {
        // Obtener el usuario autenticado
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Asignar la institución educativa del usuario al grupo
        data.setInstitucionEducativaId(appUser.getInstitucionEducativaId());
         // Crear el grupo y devolver su ID
        var grupoDTO = grupoService.crearGrupo(data);
        return grupoDTO.getId();
    }
    
    /**
         * Actualiza la información de un grupo existente.
         * @param id   identificador del grupo a actualizar
         * @param data nuevos datos del grupo
         * @return el identificador del grupo actualizado
     */
    public Long editarGrupo(Long id, GrupoDTO data) {
        var grupoDTO = grupoService.actualizarGrupo(id, data);
        return grupoDTO.getId();
    }
    
    /**
         * Inactiva un grupo por su identificador.
         * <p>En lugar de eliminarlo físicamente, su estado se marca como inactivo.</p>
         * @param id identificador del grupo a inactivar
         * @return número de registros afectados (1 si fue exitoso)
     */
    public int inactivarGrupo(Long id) {
        return grupoService.inactivarById(id);
    }
    
    /**
         * Lista los alumnos pertenecientes a un grupo específico.
         * @param grupoId identificador del grupo
         * @return lista de alumnos como {@link Alumno2DTO}
     */
    @NonNull
    public List<@NonNull Alumno2DTO> listarAlumnosPorGrupoId(Long grupoId) {
        // Obtener el usuario autenticado para filtrar por su institución
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Consultar los alumnos del grupo pertenecientes a la misma institución educativa
        return grupoService.listarAlumnosPorGrupoId(grupoId, appUser.getInstitucionEducativaId());
    }
    
    /**
         * Lista los grupos por nombre, filtrando por la institución educativa del usuario autenticado.
         * @param pageable configuración de paginación
         * @param nombre   nombre o parte del nombre del grupo a buscar
         * @return lista de resultados de tipo {@link IdLabelDTO}
     */
    @NonNull
    public List<@NonNull IdLabelDTO> listarGruposPorNombre(Pageable pageable, String nombre) {
        // Obtener el usuario autenticado
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Crear un filtro para la búsqueda por nombre dentro de la institución
        var filtro = new FiltroGrupo();
        filtro.setNombre(nombre);
        filtro.setInstitucionEducativaId(appUser.getInstitucionEducativaId());
        filtro.setActivo(true);
        // Ejecutar la búsqueda de grupos aplicando el filtro y la paginación especificada
        return grupoService.listarGruposPorNombre(pageable, filtro);
    }
}
