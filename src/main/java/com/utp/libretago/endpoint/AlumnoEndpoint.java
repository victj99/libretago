package com.utp.libretago.endpoint;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.AlumnoDTO;
import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.dto.StudentStatsDTO;
import com.utp.libretago.classes.filtros.FiltroAlumno;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.service.AlumnoService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

/**
 * Endpoint que expone las operaciones disponibles para la gestión de alumnos.
 * <p>
 * Este endpoint está restringido al rol {@link RolesEnum#COLEGIO} y permite realizar operaciones de búsqueda, creación,
 * edición, inactivación y validación de datos de alumnos. Utiliza {@link AlumnoService} como capa de negocio.
 *
 * Integra con la seguridad de la aplicación mediante {@link AppUser} para obtener la institución educativa asociada al
 * usuario autenticado. Este endpoint está restringido al rol {@link RolesEnum#COLEGIO} y permite realizar operaciones
 * de búsqueda, creación, edición, inactivación y validación de datos de alumnos. Utiliza {@link AlumnoService} como
 * capa de negocio.
 *
 * Integra con la seguridad de la aplicación mediante {@link AppUser} para obtener la institución educativa asociada al
 * usuario autenticado.
 *
 * @author Jhon Peña
 * @version 1.0
 * @since 2025-10-27
 */
@Endpoint
@RolesAllowed({ RolesEnum.COLEGIO })
public class AlumnoEndpoint {

    @Autowired
    private AlumnoService alumnoService;

    /**
     * Busca alumnos aplicando los filtros definidos y devuelve los resultados de manera paginada.
     * <p>
     * Si el objeto {@link Pageable} no incluye orden, se aplicará una ordenación descendente por el campo <code>id</code>.
     * </p>
     *
     * @param filtro   objeto {@link FiltroAlumno} con los criterios de búsqueda.
     * @param pageable información de paginación y orden.
     * @return una página de {@link AlumnoDTO} con los alumnos que cumplen los filtros.
     */
    @NonNull
    public Page<@NonNull AlumnoDTO> buscarPorFiltros(FiltroAlumno filtro, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");

        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        filtro.setInstitucionEducativaId(appUser.getInstitucionEducativaId());

        return alumnoService.buscarAlumnosPorFiltros(filtro, pageable);
    }

    /**
     * Obtiene la información de un alumno a partir de su identificador.
     *
     * @param id identificador único del alumno.
     * @return un {@link AlumnoDTO} con la información del alumno, o {@code null} si no existe o está inactivo.
     */
    public AlumnoDTO obtenerAlumno(Long id) {
        var ie = alumnoService.obtenerPorId(id);
        return ie.orElse(null);
    }

    /**
     * Crea un nuevo alumno asociado a la institución educativa del usuario autenticado.
     *
     * @param data objeto {@link AlumnoDTO} con los datos del alumno a registrar.
     * @return el identificador del alumno recién creado.
     */
    public Long crearAlumno(AlumnoDTO data) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var institucion = alumnoService.crearAlumno(data, appUser.getInstitucionEducativaId());
        return institucion.id();
    }

    /**
     * Actualiza los datos de un alumno existente.
     *
     * @param id   identificador del alumno que se desea editar.
     * @param data objeto {@link AlumnoDTO} con los nuevos datos.
     * @return el identificador del alumno actualizado.
     */
    public Long editarAlumno(Long id, AlumnoDTO data) {
        var alumno = alumnoService.actualizarAlumno(id, data);
        return alumno.id();
    }

    /**
     * Inactiva un alumno, cambiando su estado a <code>activo = false</code>.
     *
     * @param id identificador del alumno a inactivar.
     * @return número de registros modificados (1 si fue exitoso, 0 si no existe).
     */
    public int inactivarAlumno(Long id) {
        return alumnoService.inactivarById(id);
    }

    /**
     * Lista alumnos activos cuyos códigos se encuentren en una lista y que pertenezcan a la institución del usuario
     * autenticado.
     *
     * @param codigosAlumno lista de códigos de alumnos.
     * @return lista de {@link Alumno2DTO} con la información básica de los alumnos.
     */
    @NonNull
    public List<@NonNull Alumno2DTO> listarAlumnosPorCodigo(List<String> codigosAlumno) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return alumnoService.listarAlumnosPorCodigos(codigosAlumno, appUser.getInstitucionEducativaId());
    }

    /**
     * Valida el contenido de un archivo Excel con datos de alumnos.
     * <p>
     * Este método revisa la estructura, formato y consistencia de los datos, devolviendo los registros válidos o un archivo
     * con los errores detectados.
     * </p>
     *
     * @param file archivo Excel cargado por el usuario.
     * @return un objeto {@link ExcelValidadoDTO} con los resultados de la validación.
     * @throws Exception si ocurre un error durante la lectura o validación del archivo.
     */
    public ExcelValidadoDTO<@NonNull AlumnoDTO> validarArchivo(@NonNull MultipartFile file) throws Exception {
        return alumnoService.validarArchivo(file);
    }

    /**
     * Obtiene estadísticas de alumnos activos e inactivos para la institución del usuario actual.
     *
     * @return DTO con los conteos de alumnos activos e inactivos.
     */
    public @NonNull StudentStatsDTO obtenerEstadisticas() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return alumnoService.obtenerEstadisticas(appUser.getInstitucionEducativaId());
    }
}
