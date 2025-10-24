package com.utp.libretago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import com.utp.libretago.entity.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long>, JpaSpecificationExecutor<Notificacion> {

    @Modifying
    @Query("UPDATE Notificacion n SET n.activo = false WHERE n.id = :id")
    int inactivarNotificacionPorId(@Param("id") Long id);

    @Query("SELECT DISTINCT n FROM Notificacion n JOIN n.grupos g WHERE g.id IN (?1) AND n.activo = true AND n.estado = '"
            + Notificacion.ESTADO_APROBADO + "'")
    Page<Notificacion> findByGrupoIds(List<Long> grupoIds, Pageable pageable);
}