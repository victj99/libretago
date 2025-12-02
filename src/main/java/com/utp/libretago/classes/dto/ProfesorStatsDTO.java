package com.utp.libretago.classes.dto;

/**
 * DTO que representa las estadísticas de profesores activos e inactivos
 * de una institución educativa.
 *
 * @param activeCount   cantidad de profesores activos.
 * @param inactiveCount cantidad de profesores inactivos.
 */
public record ProfesorStatsDTO(long activeCount, long inactiveCount) {
}
