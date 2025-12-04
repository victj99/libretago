package com.utp.libretago.classes.dto;

import java.time.LocalDate;

/**
 * DTO auxiliar para recibir la proyección de estadísticas de eventos
 * agrupadas por institución y fecha desde la consulta optimizada.
 *
 * @param institucionId identificador de la institución educativa.
 * @param date          fecha de las estadísticas.
 * @param count         cantidad de eventos en esa fecha.
 */
public record EventoStatsInstitucionDTO(Long institucionId, LocalDate date, long count) {
}
