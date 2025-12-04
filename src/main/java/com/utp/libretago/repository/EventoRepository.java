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

    /**
     * Cuenta los eventos enviados por día en un rango de fechas para una institución.
     *
     * @param institucionId identificador de la institución educativa.
     * @param startDate     fecha de inicio del rango.
     * @param endDate       fecha de fin del rango.
     * @return lista de estadísticas con fecha y conteo.
     */
    @Query("SELECT new com.utp.libretago.classes.dto.EventoStatsDTO(CAST(e.fechaCreacion AS LocalDate), COUNT(DISTINCT e.id)) "
            + "FROM Evento e "
            + "JOIN e.eventoGrupos eg "
            + "JOIN eg.grupo g "
            + "WHERE g.institucionEducativaId = :institucionId "
            + "AND e.fechaCreacion BETWEEN :startDate AND :endDate "
            + "GROUP BY CAST(e.fechaCreacion AS LocalDate) "
            + "ORDER BY CAST(e.fechaCreacion AS LocalDate) ASC")
    List<com.utp.libretago.classes.dto.EventoStatsDTO> countEventosPorDia(
            @Param("institucionId") Long institucionId,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Cuenta los eventos enviados por día en un rango de fechas para todas las instituciones.
     * Consulta optimizada que retorna todos los resultados en una sola ejecución.
     *
     * @param startDate fecha de inicio del rango.
     * @param endDate   fecha de fin del rango.
     * @return lista de estadísticas con institución, fecha y conteo.
     */
    @Query("SELECT new com.utp.libretago.classes.dto.EventoStatsInstitucionDTO(g.institucionEducativaId, CAST(e.fechaCreacion AS LocalDate), COUNT(DISTINCT e.id)) "
            + "FROM Evento e "
            + "JOIN e.eventoGrupos eg "
            + "JOIN eg.grupo g "
            + "WHERE e.fechaCreacion BETWEEN :startDate AND :endDate "
            + "GROUP BY g.institucionEducativaId, CAST(e.fechaCreacion AS LocalDate) "
            + "ORDER BY g.institucionEducativaId ASC, CAST(e.fechaCreacion AS LocalDate) ASC")
    List<com.utp.libretago.classes.dto.EventoStatsInstitucionDTO> countEventosPorDiaTodasInstituciones(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);
}
