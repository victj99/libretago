package com.utp.libretago.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.utp.libretago.entity.Evento;

/**
 * Repositorio para la gestión de la entidad {@link Evento}.
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {

    @Modifying
    @Query("UPDATE Evento e SET e.activo = false WHERE e.id = :id")
    int inactivarEventoPorId(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Evento e JOIN EventoGrupo eg ON eg.eventoId = e.id WHERE eg.grupoId IN (?1) AND e.activo = true AND e.estado = '"
            + Evento.ESTADO_APROBADO + "'")
    Page<Evento> findByGrupoIds(List<Long> grupoIds, Pageable pageable);

    Page<Evento> findByUsuarioCreadorIdAndActivoTrue(Long usuarioCreadorId, Pageable pageable);
}
