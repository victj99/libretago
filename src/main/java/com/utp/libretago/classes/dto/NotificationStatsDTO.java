package com.utp.libretago.classes.dto;

import java.time.LocalDate;

public record NotificationStatsDTO(LocalDate date, long count) {
}
