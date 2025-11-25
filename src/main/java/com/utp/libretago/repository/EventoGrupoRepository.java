package com.utp.libretago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.utp.libretago.entity.EventoGrupo;
import com.utp.libretago.entity.EventoGrupoId;

/**
 * Repositorio para la gestión de la entidad {@link EventoGrupo}.
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
public interface EventoGrupoRepository extends JpaRepository<EventoGrupo, EventoGrupoId>, JpaSpecificationExecutor<EventoGrupo> {

    @Query("SELECT eg.grupoId FROM EventoGrupo eg WHERE eg.eventoId = :eventoId")
    List<Long> listarGrupoIdsPorEventoId(@Param("eventoId") Long eventoId);

    @Modifying
    @Query("DELETE FROM EventoGrupo eg WHERE eg.eventoId = :eventoId")
    void eliminarPorEventoId(@Param("eventoId") Long eventoId);
}
