package com.utp.libretago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.NotificacionGrupo;
import com.utp.libretago.entity.NotificacionGrupoId;

/**
 * Repositorio para la gestión de la entidad {@link NotificacionGrupo}.
 * <p>
 * Proporciona métodos personalizados para consultar relaciones entre notificaciones y grupos dentro del sistema.
 * </p>
 *
 * <p>
 * Extiende {@link JpaRepository}, lo que permite realizar operaciones CRUD y consultas derivadas sobre la tabla
 * intermedia <strong>notificacion_grupo</strong>.
 * </p>
 *
 * @see com.utp.libretago.entity.Notificacion
 * @see com.utp.libretago.entity.Grupo
 * @see NotificacionGrupoId
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
public interface NotificacionGrupoRepository extends JpaRepository<NotificacionGrupo, NotificacionGrupoId>, JpaSpecificationExecutor<NotificacionGrupo> {

    /**
     * Lista todos los identificadores de grupos asociados a una notificación específica.
     *
     * @param notificacionId identificador de la notificación.
     * @return lista de identificadores de grupos asociados a la notificación.
     */
    @Query("""
            SELECT ng.grupoId
            FROM NotificacionGrupo ng
            WHERE ng.notificacionId = ?1
            """)
    List<Long> listarGrupoIdsPorNotificacionId(Long notificacionId);

    /**
     * Elimina todas las relaciones entre una notificación y sus grupos.
     *
     * @param notificacionId identificador de la notificación.
     */
    @Modifying
    @Query("""
            DELETE FROM NotificacionGrupo ng
            WHERE ng.notificacionId = ?1
            """)
    void eliminarPorNotificacionId(Long notificacionId);
}
