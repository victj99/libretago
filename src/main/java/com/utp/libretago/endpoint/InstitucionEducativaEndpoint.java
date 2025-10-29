package com.utp.libretago.endpoint;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.filtros.FiltroInstitucionEducativa;
import com.utp.libretago.entity.InstitucionEducativa;
import com.utp.libretago.service.InstitucionEducativaService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

/**
 * Endpoint para la gestión de Instituciones Educativas. Permite buscar, crear, actualizar, inactivar y listar
 * instituciones educativas. Solo accesible por usuarios con rol {@link RolesEnum#ADMIN}.
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */

@Endpoint
@RolesAllowed(RolesEnum.ADMIN)
public class InstitucionEducativaEndpoint {
    @Autowired
    private InstitucionEducativaService institucionEducativaService;

    /**
     * Busca instituciones educativas aplicando filtros específicos y paginación. Este método permite consultar las
     * instituciones educativas según criterios definidos en {@link FiltroInstitucionEducativa}, retornando los resultados
     * paginados y ordenados por defecto en orden descendente por su ID.
     * 
     * @param filtro   Objeto que contiene los criterios de búsqueda como nombre, código UGEL u otros parámetros
     *                 configurables.
     * @param pageable Configuración de paginación y ordenamiento de los resultados.
     * @return Página de {@link InstitucionEducativa} que cumple con los filtros aplicados.
     */
    @NonNull
    public Page<@NonNull InstitucionEducativa> buscarPorFiltros(FiltroInstitucionEducativa filtro, Pageable pageable) {
        // Aplica un ordenamiento por defecto descendente según el campo "id" si no se especifica otro
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");
        // Ejecuta la búsqueda con los filtros aplicados y devuelve los resultados paginados
        return institucionEducativaService.buscarPorFiltros(filtro, pageable);
    }

    /**
     * Obtiene una institución educativa por su ID. Este método busca una institución educativa en la base de datos
     * utilizando el servicio {@link institucionEducativaService}. Si la institución existe, devuelve el objeto
     * correspondiente; de lo contrario, retorna {@code null}.
     * 
     * @param id Identificador único de la institución educativa a buscar.
     * @return La {@link InstitucionEducativa} correspondiente si se encuentra; {@code null} si no existe.
     */

    public InstitucionEducativa obtenerInstitucion(Long id) {
        // Intenta obtener la institución por su ID usando el servicio
        var ie = institucionEducativaService.obtenerPorId(id);
        // Devuelve la institución si está presente, de lo contrario retorna null
        if (ie.isPresent()) {
            // Devuelve la institución si está presente
            return ie.get();
        }
        // Retorna null si no se encontró
        return null;

    }

    /**
     * Crea una nueva institución educativa en el sistema.
     * 
     * @param data Objeto {@link InstitucionEducativa} con los datos de la institución a crear.
     * @return ID de la institución recién creada.
     */
    public Long crearInstitucion(InstitucionEducativa data) {
        // Llamamos al servicio para crear la institución en la base de datos.
        var institucion = institucionEducativaService.crearInstitucion(data);
        // Retornamos el ID generado de la nueva institución.
        return institucion.getId();
    }

    /**
     * Actualiza los datos de una institución educativa existente.
     * 
     * @param id   ID de la institución que se desea actualizar.
     * @param data Objeto {@link InstitucionEducativa} con los datos actualizados.
     * @return ID de la institución actualizada.
     */
    public Long editarInstitucion(Long id, InstitucionEducativa data) {
        // Llamamos al servicio para actualizar la institución en la base de datos.
        // El servicio busca la institución por ID, aplica los cambios y retorna la entidad actualizada.
        var institucion = institucionEducativaService.actualizarInstitucion(id, data);
        // Retornamos el ID de la institución ya actualizada.
        return institucion.getId();
    }

    /**
     * Inactiva una institución educativa en el sistema. Este método no elimina la institución, sino que la marca como
     * inactiva, evitando que se use en futuras operaciones.
     * 
     * @param id ID de la institución a inactivar.
     * @return Número de registros afectados (1 si se inactivó correctamente, 0 si no se encontró).
     */
    public int inactivarInstitucion(Long id) {
        // Llamamos al servicio para marcar la institución como inactiva.
        // El servicio realiza un update sobre el registro correspondiente en la base de datos.
        // Retorna 1 si se afectó el registro, 0 si no existe.
        return institucionEducativaService.inactivarById(id);
    }

    /**
     * Lista instituciones educativas filtradas por nombre con paginación.
     *
     * Este método permite obtener un conjunto de instituciones educativas aplicando:
     * <ul>
     * <li>Filtro por nombre (opcional).</li>
     * <li>Paginación según los parámetros recibidos en {@link Pageable}.</li>
     * <li>Transformación de los resultados a {@link LabelValueDTO} para uso en interfaces o selectores.</li>
     * </ul>
     * 
     * @param pageable Configuración de paginación y ordenamiento.
     * @param nombre   Filtro opcional para buscar instituciones por nombre.
     * @return Lista de {@link LabelValueDTO} representando las instituciones encontradas.
     */
    @NonNull
    public List<@NonNull LabelValueDTO> listarInstituciones(Pageable pageable, String nombre) {
        // Llamamos al servicio que aplica filtros y paginación.
        // El servicio se encarga de mapear los resultados a LabelValueDTO.
        return institucionEducativaService.listarInstitucionesPorNombre(pageable, nombre);
    }
}
