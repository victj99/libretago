package com.utp.libretago.classes.dto;

import com.utp.libretago.entity.Usuario;
import com.utp.libretago.utils.MensajesValidacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 50, message = MensajesValidacion.LARGO_MAXIMO)
    private String nombreUsuario;

    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    private String nombreCompleto;

    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    private String email;

    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    private String telefono;

    private Boolean activo;

    public UsuarioDTO() {
    }

    public Usuario obtenerUsuario() {
        var usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setActivo(activo);

        return usuario;
    }

    public UsuarioDTO(AlumnoDTO alumnoDTO) {
        this.nombreUsuario = alumnoDTO.dniCeApoderado();
        this.nombreCompleto = alumnoDTO.nombreCompletoApoderado();
        this.email = alumnoDTO.email();
        this.telefono = alumnoDTO.telefono();
    }
}
