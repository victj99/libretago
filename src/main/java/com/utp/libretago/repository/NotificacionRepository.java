package com.utp.libretago.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.utp.libretago.entity.Notificacion;

/**
 * Repositorio para la gestión de la entidad {@link Notificacion}.
 *
 * Extiende {@link JpaRepository} para proporcionar las operaciones básicas de persistencia (crear, leer, actualizar,
 * eliminar) y {@link JpaSpecificationExecutor} para ejecutar consultas dinámicas mediante especificaciones.
 *
 * Incluye métodos personalizados para:
 * <ul>
 * <li>Inactivar notificaciones de manera lógica (sin eliminarlas físicamente).</li>
 * <li>Listar notificaciones activas y aprobadas asociadas a uno o más grupos.</li>
 * </ul>
 *
 * @see Notificacion
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 * @see org.springframework.data.domain.Page
 * @see org.springframework.data.domain.Pageable
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface NotificacionRepository extends JpaRepository<Notificacion, Long>, JpaSpecificationExecutor<Notificacion> {

    /**
     * Inactiva lógicamente una notificación estableciendo su campo {@code activo} en {@code false}.
     * <p>
     * Este método no elimina el registro de la base de datos, sino que lo marca como inactivo para preservar su historial.
     * </p>
     *
     * @param id identificador único de la notificación a inactivar.
     * @return número de registros afectados (generalmente 1).
     */
    @Modifying
    @Query("UPDATE Notificacion n SET n.activo = false WHERE n.id = :id")
    int inactivarNotificacionPorId(@Param("id") Long id);

    /**
     * Recupera una página de notificaciones activas y aprobadas asociadas a uno o varios grupos.
     * <p>
     * Solo se incluyen notificaciones con estado {@link Notificacion#ESTADO_APROBADO} y con {@code activo = true}.
     * </p>
     *
     * @param grupoIds lista de identificadores de grupos cuyas notificaciones se desean consultar.
     * @param pageable parámetros de paginación y ordenamiento.
     * @return una página de notificaciones aprobadas y activas correspondientes a los grupos indicados.
     */
    @Query("SELECT DISTINCT n " + "FROM Notificacion n " + "JOIN n.grupos g " + "WHERE g.id IN (?1) " + "AND n.activo = true " + "AND n.estado = '"
            + Notificacion.ESTADO_APROBADO + "'")
    Page<Notificacion> findByGrupoIds(List<Long> grupoIds, Pageable pageable);

}
