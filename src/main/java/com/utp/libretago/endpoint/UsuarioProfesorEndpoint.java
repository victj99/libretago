package com.utp.libretago.endpoint;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.dto.ProfesorStatsDTO;
import com.utp.libretago.classes.dto.UsuarioDTO;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;
import com.utp.libretago.classes.filtros.FiltroUsuario;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.entity.Rol;
import com.utp.libretago.service.UsuarioService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Endpoint encargado de gestionar a los usuarios con rol de <b>PROFESOR</b>.
 *
 * Solo los usuarios con rol <b>COLEGIO</b> pueden acceder a este endpoint. Permite realizar operaciones CRUD y
 * consultas filtradas sobre los profesores de una institución educativa.
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */

@Endpoint
@RolesAllowed(RolesEnum.COLEGIO)
public class UsuarioProfesorEndpoint {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Busca profesores según los filtros aplicados y la configuración de paginación.
     * 
     * @param filtro   Filtros de búsqueda (nombre, estado, etc.).
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página con los profesores que cumplen los filtros.
     */

    @NonNull
    // Aplica orden descendente por defecto (por ID)
    public Page<@NonNull UsuarioInstitucionDTO> buscarPorFiltros(FiltroUsuario filtro, Pageable pageable) {
        // Aplica orden descendente por defecto (por ID)
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "usuarioColegioId");

        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Filtra solo usuarios con rol de profesor
        filtro.setRolId(Rol.ID_PROFESOR);
        filtro.setInstitucionEducativaId(appUser.getInstitucionEducativaId());

        // Retorna la lista paginada de profesores
        return usuarioService.buscarUsuarioInstitucionPorFiltros(filtro, pageable);
    }

    /**
     * Obtiene la información detallada de un profesor a partir de su ID
     * 
     * @param id ID del profesor a consultar.
     * @return Objeto {@link UsuarioInstitucionDTO} con los datos del profesor, o {@code null} si no existe.
     */
    public UsuarioInstitucionDTO obtenerUsuario(Long id) {
        // Busca el usuario por su ID
        var ie = usuarioService.obtenerPorId(id);
        // Devuelve el usuario si existe
        if (ie.isPresent()) {
            return ie.get();
        }
        // Si no existe, retorna null
        return null;
    }

    /**
     * Crea un nuevo usuario con rol de profesor dentro de la institución del colegio autenticado.
     * 
     * @param data Datos del nuevo profesor a registrar.
     * @return ID del profesor recién creado.
     */
    public Long crearUsuario(UsuarioDTO data) {
        // Obtiene el usuario (colegio) autenticado
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Crea un nuevo DTO que asocia al usuario con la institución educativa
        var usuarioInstitucionDTO = new UsuarioInstitucionDTO();
        // Copia las propiedades del DTO original al nuevo objeto
        BeanUtils.copyProperties(data, usuarioInstitucionDTO);
        // Asigna la institución educativa correspondiente al usuario autenticado
        usuarioInstitucionDTO.setInstitucionEducativaId(appUser.getInstitucionEducativaId());
        // Crea el nuevo profesor
        var usuario = usuarioService.crearUsuario(usuarioInstitucionDTO, Rol.ID_PROFESOR);
        // Devuelve el identificador del nuevo usuario creado
        return usuario.getId();
    }

    /**
     * Actualiza los datos de un usuario existente (profesor).
     * 
     * @param id   identificador del usuario a editar
     * @param data datos actualizados del usuario
     * @return ID del usuario actualizado
     */
    public Long editarUsuario(Long id, UsuarioDTO data) {
        // Actualiza el usuario con los datos recibidos
        var usuario = usuarioService.actualizarUsuario(id, data);
        // Devuelve el identificador del usuario actualizado
        return usuario.getId();
    }

    /**
     * Inactiva un usuario por su identificador.
     *
     * En lugar de eliminarlo permanentemente, su estado se cambia a inactivo.
     * 
     * @param id identificador del usuario a inactivar
     * @return número de registros afectados (1 si se inactivó correctamente)
     */
    public int inactivarUsuario(Long id) {
        // Llama al servicio para cambiar el estado del usuario a inactivo
        return usuarioService.inactivarById(id);
    }

    /**
     * Lista usuarios con rol de profesor según el nombre indicado.
     *
     * Se aplica paginación y filtros por nombre y rol.
     * 
     * @param pageable configuración de paginación
     * @param nombre   nombre o parte del nombre del usuario a buscar
     * @return lista de usuarios como {@link LabelValueDTO}
     */
    @NonNull
    public List<@NonNull LabelValueDTO> listarUsuarios(Pageable pageable, String nombre) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Crea el filtro de búsqueda
        FiltroUsuario filtro = new FiltroUsuario();
        filtro.setNombreCompleto(nombre);
        // Limita la búsqueda a usuarios con rol de profesor
        filtro.setRolId(Rol.ID_PROFESOR);
        filtro.setInstitucionEducativaId(appUser.getInstitucionEducativaId());

        // Llama al servicio para obtener los resultados paginados
        return usuarioService.listarUsuariosPorNombre(pageable, filtro);
    }

    /**
     * Valida un archivo Excel que contiene información de usuarios.
     *
     * Revisa el formato, estructura y datos del archivo antes de importar los registros.
     * 
     * @param file archivo Excel a validar
     * @return objeto {@link ExcelValidadoDTO} que contiene los datos validados y los errores encontrados
     * @throws IOException si ocurre un error al leer el archivo
     */
    public ExcelValidadoDTO<@NonNull UsuarioDTO> validarArchivo(MultipartFile file) throws IOException {
        // Envía el archivo al servicio para validación de estructura y contenido
        return usuarioService.validarArchivo(file);
    }

    /**
     * Obtiene estadísticas de profesores activos e inactivos para la institución del usuario actual.
     *
     * @return DTO con los conteos de profesores activos e inactivos.
     */
    public ProfesorStatsDTO obtenerEstadisticas() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioService.obtenerEstadisticasProfesores(appUser.getInstitucionEducativaId());
    }
}
