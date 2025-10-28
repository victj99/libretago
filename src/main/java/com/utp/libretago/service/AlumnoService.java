package com.utp.libretago.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.AlumnoDTO;
import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.filtros.FiltroAlumno;

/**
 * Servicio que define las operaciones de negocio relacionadas con los alumnos.
 * <p>
 * Esta interfaz abstrae la lógica necesaria para gestionar alumnos dentro del
 * sistema Libretago, incluyendo filtrado, creación, actualización,
 * inactivación y validación de datos provenientes de archivos Excel.
 * </p>
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface AlumnoService {

    /**
     * Busca alumnos según los filtros establecidos y devuelve los resultados de forma paginada.
     *
     * @param filtro objeto {@link FiltroAlumno} que contiene los criterios de búsqueda
     *               (como nombre, institución, estado, etc.).
     * @param pageable objeto {@link Pageable} que define la paginación y orden de los resultados.
     * @return una página de {@link AlumnoDTO} que cumplen con los criterios especificados.
     */
    Page<AlumnoDTO> buscarAlumnosPorFiltros(FiltroAlumno filtro, Pageable pageable);

    /**
     * Lista alumnos activos cuyos códigos se encuentren dentro de una lista determinada
     * y pertenezcan a una institución educativa específica.
     *
     * @param codigos lista de códigos de alumnos.
     * @param institucionEducativaId identificador de la institución educativa.
     * @return lista de {@link Alumno2DTO} con los alumnos encontrados.
     */
    List<Alumno2DTO> listarAlumnosPorCodigos(List<String> codigos, Long institucionEducativaId);

    /**
     * Obtiene un alumno específico según su identificador único.
     *
     * @param id identificador del alumno.
     * @return un {@link Optional} que contiene el {@link AlumnoDTO} si existe,
     *         o vacío si no se encontró.
     */
    Optional<AlumnoDTO> obtenerPorId(Long id);

    /**
     * Crea un nuevo alumno en la base de datos, asociado a una institución educativa.
     *
     * @param alumno objeto {@link AlumnoDTO} con los datos del alumno a registrar.
     * @param institucionId identificador de la institución educativa.
     * @return el {@link AlumnoDTO} recién creado.
     */
    AlumnoDTO crearAlumno(AlumnoDTO alumno, Long institucionId);

    /**
     * Actualiza la información de un alumno existente.
     *
     * @param id identificador del alumno a actualizar.
     * @param alumno objeto {@link AlumnoDTO} con los nuevos datos.
     * @return el {@link AlumnoDTO} actualizado.
     */
    AlumnoDTO actualizarAlumno(Long id, AlumnoDTO alumno);

    /**
     * Inactiva (deshabilita) un alumno según su identificador.
     *
     * @param id identificador del alumno a inactivar.
     * @return número de registros afectados (1 si fue exitoso, 0 si no existe).
     */
    int inactivarById(Long id);

    /**
     * Valida el contenido de un archivo Excel con información de alumnos.
     * <p>
     * Este método revisa formato, duplicados y consistencia de datos antes de importar.
     * </p>
     *
     * @param file archivo Excel cargado por el usuario.
     * @return un objeto {@link ExcelValidadoDTO} que contiene la lista de alumnos válidos
     *         y los errores detectados.
     * @throws IOException si ocurre un error al leer el archivo.
     */
    ExcelValidadoDTO<AlumnoDTO> validarArchivo(MultipartFile file) throws IOException;
}
