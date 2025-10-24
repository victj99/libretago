package com.utp.libretago.classes.dto;

import java.util.List;

import com.utp.libretago.entity.Grupo;
import com.utp.libretago.utils.MensajesValidacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GrupoDTO {

    private Long id;

    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 50, message = MensajesValidacion.LARGO_MAXIMO)
    private String nombre;

    private List<Alumno2DTO> alumnosNuevos;
    private List<Long> alumnosEliminadosIds;

    private Long institucionEducativaId;
    private Long usuarioProfesorId;
    private Boolean activo;

    // Campos para la grilla
    private String nombreProfesor;

    public GrupoDTO() {
    }

    public Grupo obtenerGrupo() {
        var grupo = new Grupo();
        grupo.setId(id);
        grupo.setNombre(nombre);
        grupo.setInstitucionEducativaId(institucionEducativaId);
        grupo.setUsuarioProfesorId(usuarioProfesorId);
        grupo.setActivo(activo);

        return grupo;
    }
}
