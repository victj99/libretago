package com.utp.libretago.classes.dto;

/**
 * DTO que representa las estadísticas de usuarios con rol COLEGIO
 * activos e inactivos en el sistema.
 *
 * @param activeCount   cantidad de colegios activos.
 * @param inactiveCount cantidad de colegios inactivos.
 */
public record ColegioStatsDTO(long activeCount, long inactiveCount) {
}
