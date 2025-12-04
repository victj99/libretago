package com.utp.libretago.classes.dto;

import java.time.LocalDate;

/**
 * DTO que representa las estadísticas de eventos por día.
 *
 * @param date  fecha de las estadísticas.
 * @param count cantidad de eventos en esa fecha.
 */
public record EventoStatsDTO(LocalDate date, long count) {
}
