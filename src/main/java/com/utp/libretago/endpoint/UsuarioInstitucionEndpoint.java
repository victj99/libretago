package com.utp.libretago.endpoint;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;
import com.utp.libretago.classes.filtros.FiltroUsuario;
import com.utp.libretago.entity.Rol;
import com.utp.libretago.service.UsuarioService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

/**
 * Endpoint encargado de gestionar los usuarios con rol <b>COLEGIO</b> dentro del sistema.
 *
 * Permite realizar operaciones de búsqueda, creación, edición y obtención de usuarios asociados a instituciones
 * educativas.
 *
 * <b>Seguridad:</b> Solo los usuarios con el rol {@link RolesEnum#ADMIN} pueden acceder a este endpoint.
 * 
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
@Endpoint
@RolesAllowed(RolesEnum.ADMIN)
public class UsuarioInstitucionEndpoint {
    /**
     * Servicio encargado de la lógica de negocio relacionada con la gestión de usuarios. Se utiliza para interactuar con la
     * capa de datos de usuarios tipo "COLEGIO".
     */
    @Autowired
    private UsuarioService usuarioService;

    /**
     * Busca usuarios de tipo COLEGIO aplicando filtros y paginación.
     *
     * Este método ordena los resultados por defecto de forma descendente según el campo <code>usuarioColegioId</code> y
     * filtra automáticamente por el rol correspondiente a COLEGIO.
     * 
     * @param filtro   Filtros aplicables a la búsqueda de usuarios.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página de {@link UsuarioInstitucionDTO} con los resultados encontrados.
     */
    @NonNull
    public Page<@NonNull UsuarioInstitucionDTO> buscarPorFiltros(FiltroUsuario filtro, Pageable pageable) {
        // Aplica ordenamiento descendente por defecto si no se especifica otro campo
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "usuarioColegioId");
        // Forzamos que la búsqueda se limite a usuarios con rol COLEGIO
        filtro.setRolId(Rol.ID_COLEGIO);
        // Retornamos los resultados filtrados desde el servicio
        return usuarioService.buscarUsuarioInstitucionPorFiltros(filtro, pageable);
    }

    /**
     * Obtiene la información detallada de un usuario COLEGIO a partir de su ID.
     * 
     * @param id ID del usuario a consultar.
     * @return Objeto {@link UsuarioInstitucionDTO} con los datos del usuario, o <code>null</code> si no existe.
     */
    public UsuarioInstitucionDTO obtenerUsuario(Long id) {
        // Se busca el usuario en la base de datos según su identificador
        var ie = usuarioService.obtenerPorId(id);
        // Si el usuario existe, se devuelve su información completa
        if (ie.isPresent()) {
            return ie.get();
        }
        // Si no se encontró, se devuelve null
        return null;
    }

    /**
     * Crea un nuevo usuario con rol COLEGIO dentro del sistema.
     * 
     * @param data Objeto {@link UsuarioInstitucionDTO} con la información del usuario a registrar.
     * @return ID del usuario recién creado.
     */
    public Long crearUsuario(UsuarioInstitucionDTO data) {
        // Se crea el usuario asignándole el rol COLEGIO por defecto
        var usuario = usuarioService.crearUsuario(data, Rol.ID_COLEGIO);
        // Retorna el identificador del nuevo usuario
        return usuario.getId();
    }

    /**
     * Actualiza los datos de un usuario COLEGIO existente.
     *
     * @param id   ID del usuario a actualizar.
     * @param data Objeto {@link UsuarioInstitucionDTO} con los datos modificados.
     * @return ID del usuario actualizado.
     */
    public Long editarUsuario(Long id, UsuarioInstitucionDTO data) {
        // Se actualizan los datos del usuario en la base de datos
        var usuario = usuarioService.actualizarUsuario(id, data);
        // Retorna el identificador del usuario actualizado
        return usuario.getId();
    }

    /**
     * Inactiva un usuario COLEGIO dentro del sistema.
     *
     * Este método no elimina al usuario de forma permanente, sino que cambia su estado a inactivo para impedir que acceda
     * al sistema o realice acciones futuras.
     *
     * @param id ID del usuario que se desea inactivar.
     * @return Número de registros afectados (1 si la operación fue exitosa, 0 si no se encontró el usuario).
     */
    public int inactivarUsuario(Long id) {
        // Llama al servicio para cambiar el estado del usuario a inactivo
        return usuarioService.inactivarById(id);
    }
}
