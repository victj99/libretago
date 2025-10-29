package com.utp.libretago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.Usuario;

/**
 * Repositorio para la gestión de la entidad {@link Usuario}.
 *
 * Extiende {@link JpaRepository} y {@link JpaSpecificationExecutor} para proporcionar operaciones CRUD, paginación,
 * ordenamiento y consultas dinámicas sobre los usuarios registrados en el sistema.
 *
 *
 * Este repositorio permite realizar búsquedas personalizadas relacionadas con:
 * <ul>
 * <li>Autenticación de usuarios mediante su nombre de usuario.</li>
 * <li>Obtención de usuarios junto con sus roles asociados (optimización con <i>fetch join</i>).</li>
 * <li>Inactivación lógica de usuarios sin eliminarlos físicamente de la base de datos.</li>
 * <li>Listar usuarios por rol, aprovechando la relación many-to-many con {@code Rol}.</li>
 * </ul>
 *
 *
 * @see Usuario
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    /**
     * Busca un usuario por su nombre de usuario.
     * <p>
     * Este método es utilizado comúnmente durante el proceso de autenticación para recuperar los datos básicos del usuario.
     * </p>
     *
     * @param nombreUsuario nombre único del usuario.
     * @return el usuario correspondiente, o {@code null} si no se encuentra.
     */
    Usuario findByNombreUsuario(String nombreUsuario);

    /**
     * Recupera un usuario junto con sus roles asociados en una sola consulta.
     * <p>
     * Utiliza un <i>fetch join</i> para evitar el problema de la carga perezosa (LazyInitializationException) al acceder a
     * los roles fuera del contexto de persistencia.
     * </p>
     *
     * @param nombreUsuario nombre único del usuario.
     * @return el usuario con sus roles completamente cargados, o {@code null} si no se encuentra.
     */
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.nombreUsuario = ?1")
    Usuario findByNombreUsuarioFetchRoles(String nombreUsuario);

    /**
     * Inactiva lógicamente a un usuario estableciendo su campo {@code activo} en {@code false}.
     * <p>
     * Esta operación no elimina el registro de la base de datos, preservando así su historial y relaciones asociadas.
     * </p>
     *
     * @param id identificador único del usuario a inactivar.
     * @return número de registros afectados (normalmente 1).
     */
    @Modifying
    @Query("UPDATE Usuario i SET i.activo = false WHERE i.id = ?1")
    int inactivarUsuarioPorId(Long id);

    /**
     * Busca todos los usuarios que posean un rol específico.
     * <p>
     * La búsqueda se realiza a través de la relación many-to-many entre {@code Usuario} y {@code Rol}.
     * </p>
     *
     * @param rolId identificador del rol.
     * @return lista de usuarios asociados al rol indicado.
     */
    List<Usuario> findByRolesId(Long rolId);
}
