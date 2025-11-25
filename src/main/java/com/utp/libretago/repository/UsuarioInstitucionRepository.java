package com.utp.libretago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.UsuarioInstitucion;
import com.utp.libretago.entity.UsuarioInstitucionId;

/**
 * Repositorio para la gestión de la entidad {@link UsuarioInstitucion}.
 *
 * Extiende {@link JpaRepository} y {@link JpaSpecificationExecutor} para proporcionar operaciones CRUD, paginación,
 * ordenamiento y consultas dinámicas sobre la relación entre usuarios del colegio y sus instituciones educativas.
 *
 *
 * Esta tabla intermedia asocia a cada usuario (colegio) con una institución educativa específica, permitiendo
 * determinar el contexto institucional de autenticación o gestión.
 *
 * Métodos personalizados incluidos:
 * <ul>
 * <li>{@link #findByUsuarioColegioId(Long)} — obtiene el vínculo completo entre usuario e institución.</li>
 * <li>{@link #findIdColegioByIdUsuario(Long)} — recupera solo el identificador de la institución asociada al
 * usuario.</li>
 * </ul>
 *
 * @see UsuarioInstitucion
 * @see UsuarioInstitucionId
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface UsuarioInstitucionRepository
        extends JpaRepository<UsuarioInstitucion, UsuarioInstitucionId>, JpaSpecificationExecutor<UsuarioInstitucion> {

    /**
     * Busca la relación {@link UsuarioInstitucion} correspondiente a un usuario del colegio.
     *
     * @param usuarioId identificador único del usuario del colegio.
     * @return una lista de relaciones encontradas.
     */
    @Query("SELECT ui FROM UsuarioInstitucion ui WHERE ui.usuarioColegio.id = ?1")
    java.util.List<UsuarioInstitucion> findByUsuarioColegioId(Long usuarioId);

    /**
     * Recupera el identificador de la institución educativa asociada a un usuario del colegio.
     *
     * @param usuarioId identificador único del usuario del colegio.
     * @return lista de identificadores de las instituciones educativas asociadas.
     */
    @Query("SELECT ui.institucionEducativaId FROM UsuarioInstitucion ui WHERE ui.usuarioColegio.id = ?1")
    java.util.List<Long> findIdColegioByIdUsuario(Long usuarioId);

    /**
     * Elimina la relación entre un usuario y una institución educativa específica.
     * 
     * @param usuarioId     ID del usuario.
     * @param institucionId ID de la institución educativa.
     * @return número de registros eliminados.
     */
    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM UsuarioInstitucion ui WHERE ui.usuarioColegio.id = ?1 AND ui.institucionEducativaId = ?2")
    int deleteByUsuarioIdAndInstitucionId(Long usuarioId, Long institucionId);
}
