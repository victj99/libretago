package com.utp.libretago.classes.dto;

import com.utp.libretago.entity.Alumno;
import com.utp.libretago.utils.MensajesValidacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlumnoDTO(Long id,

        @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO) @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO) String nombres,

        @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO) @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO) String apellidos,

        @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO) @Size(max = 50, message = MensajesValidacion.LARGO_MAXIMO) String codigoAlumno,

        @Size(max = 20, message = MensajesValidacion.LARGO_MAXIMO) String telefono,

        @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO) String email,

        // DNI o Carnet de Extranjeria
        @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO) @Size(max = 9, message = MensajesValidacion.LARGO_MAXIMO) String dniCeApoderado,
        @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO) @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO) String nombreCompletoApoderado,

        Boolean activo) {

    public Alumno obtenerAlumno() {
        var alumno = new Alumno();
        alumno.setId(id);
        alumno.setNombres(nombres);
        alumno.setApellidos(apellidos);
        alumno.setCodigoAlumno(codigoAlumno);
        alumno.setActivo(activo);

        return alumno;
    }
}
