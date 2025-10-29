package com.utp.libretago.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.dto.UsuarioDTO;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;
import com.utp.libretago.classes.filtros.FiltroUsuario;
import com.utp.libretago.entity.Usuario;

/**
 * Servicio para la gestión de usuarios dentro del sistema LibreTaGo.
 * 
 * Provee métodos para registrar, actualizar, validar, buscar y administrar usuarios, así como para interactuar con su
 * información institucional y roles asociados.
 *
 * Esta interfaz define la lógica del dominio de usuarios, mientras que las implementaciones concretas se encargan de
 * interactuar con los repositorios y manejar la lógica de negocio correspondiente.
 * 
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
public interface UsuarioService {

    /**
     * Valida el contenido de un archivo Excel que contiene datos de usuarios. Este método revisa la estructura y los datos
     * del archivo subido para verificar que cumplan con los criterios esperados antes de importarlos al sistema.
     *
     * @param file el archivo Excel cargado mediante un formulario.
     * @return un objeto {@link ExcelValidadoDTO} que contiene los registros validados y los errores encontrados durante la
     *         validación.
     * @throws IOException si ocurre un error al leer el archivo.
     */
    ExcelValidadoDTO<UsuarioDTO> validarArchivo(MultipartFile file) throws IOException;

    /**
     * Busca usuarios y su información institucional según los filtros especificados.
     * 
     * @param filtro   los criterios de búsqueda encapsulados en un objeto {@link FiltroUsuario}.
     * @param pageable la información de paginación y ordenamiento.
     * @return una página de {@link UsuarioInstitucionDTO} que coincide con los filtros.
     */
    Page<UsuarioInstitucionDTO> buscarUsuarioInstitucionPorFiltros(FiltroUsuario filtro, Pageable pageable);

    /**
     * Lista usuarios según su nombre o criterios definidos en los filtros. Este método devuelve una lista optimizada para
     * controles de selección o autocompletado, utilizando el formato {@link LabelValueDTO}.
     *
     * @param pageable la información de paginación.
     * @param filtros  los criterios de búsqueda aplicables.
     * @return una lista de {@link LabelValueDTO} con los nombres y valores de los usuarios.
     */

    List<LabelValueDTO> listarUsuariosPorNombre(Pageable pageable, FiltroUsuario filtros);

    /**
     * Obtiene un usuario junto con su información institucional por su identificador único.
     * 
     * @param id el identificador del usuario.
     * @return un {@link Optional} que contiene el {@link UsuarioInstitucionDTO} si existe o vacío si no se encuentra el
     *         usuario.
     */
    Optional<UsuarioInstitucionDTO> obtenerPorId(Long id);

    /**
     * Obtiene el identificador del colegio (institución educativa) al que pertenece un usuario.
     * 
     * @param idUsuario el identificador del usuario.
     * @return el identificador de la institución educativa asociada.
     */
    Long obtenerIdColegioPorIdUsuario(Long idUsuario);

    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param nombreUsuario el nombre de usuario a buscar.
     * @return el {@link Usuario} correspondiente si existe, o {@code null} en caso contrario.
     */
    Usuario obtenerPorNombreUsuario(String nombreUsuario);

    /**
     * Busca un usuario por su nombre de usuario, incluyendo los roles asociados.
     * 
     * @param nombreUsuario el nombre de usuario a buscar.
     * @return el {@link Usuario} con los roles cargados si existe, o {@code null} si no se encuentra.
     */
    Usuario obtenerPorNombreUsuarioConRoles(String nombreUsuario);

    /**
     * Crea un nuevo usuario en el sistema y le asigna un rol específico.
     * 
     * @param usuario el objeto {@link UsuarioDTO} con la información del nuevo usuario.
     * @param rolId   el identificador del rol que se asignará al usuario.
     * @return el {@link UsuarioDTO} creado con sus datos persistidos.
     */
    UsuarioDTO crearUsuario(UsuarioDTO usuario, Long rolId);

    /**
     * Actualiza la información de un usuario existente.
     * 
     * @param id      el identificador del usuario a actualizar.
     * @param usuario el objeto {@link UsuarioDTO} con los nuevos valores.
     * @return el {@link UsuarioDTO} actualizado.
     */
    UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuario);

    /**
     * Inactiva un usuario según su identificador. El usuario no se elimina físicamente del sistema, sino que se marca como
     * inactivo para preservar la trazabilidad de los registros.
     * 
     * @param id el identificador del usuario a inactivar.
     * @return el número de registros afectados (normalmente 1 si la operación fue exitosa).
     */
    int inactivarById(Long id);
}
