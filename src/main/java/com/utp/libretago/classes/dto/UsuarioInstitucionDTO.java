package com.utp.libretago.classes.dto;

import com.utp.libretago.utils.MensajesValidacion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioInstitucionDTO extends UsuarioDTO {

    @NotNull(message = MensajesValidacion.CAMPO_REQUERIDO)
    private Long institucionEducativaId;

    // Campos de detalle
    String nombreInstitucion;

    public UsuarioInstitucionDTO() {
    }

    public UsuarioInstitucionDTO(Long id, String nombreUsuario, String nombreCompleto, String email, String telefono, Boolean activo,
            Long idInstitucionEducativa, String nombreInstitucion) {
        super(id, nombreUsuario, nombreCompleto, email, telefono, activo);
        this.institucionEducativaId = idInstitucionEducativa;
        this.nombreInstitucion = nombreInstitucion;
    }

}
