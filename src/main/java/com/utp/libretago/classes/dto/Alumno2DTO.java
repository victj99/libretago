package com.utp.libretago.classes.dto;

import com.utp.libretago.utils.MensajesValidacion;

import jakarta.validation.constraints.NotNull;

public record Alumno2DTO(@NotNull(message = MensajesValidacion.CAMPO_REQUERIDO) Long id, String nombres, String apellidos, String codigoAlumno) {
}
