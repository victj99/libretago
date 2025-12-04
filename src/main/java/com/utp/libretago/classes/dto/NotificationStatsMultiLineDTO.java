package com.utp.libretago.classes.dto;

import java.util.List;

/**
 * DTO que representa las estadísticas de notificaciones de una institución
 * educativa para ser mostradas en un gráfico de líneas múltiples (Line Race).
 *
 * @param institucionNombre nombre de la institución educativa.
 * @param institucionId     identificador de la institución educativa.
 * @param stats             lista de estadísticas por día.
 */
public record NotificationStatsMultiLineDTO(
        String institucionNombre,
        Long institucionId,
        List<NotificationStatsDTO> stats) {
}
