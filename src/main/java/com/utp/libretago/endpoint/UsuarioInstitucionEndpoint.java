package com.utp.libretago.endpoint;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;
import com.utp.libretago.classes.filtros.FiltroUsuario;
import com.utp.libretago.entity.Rol;
import com.utp.libretago.service.UsuarioService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

@Endpoint
@RolesAllowed(RolesEnum.ADMIN)
public class UsuarioInstitucionEndpoint {

    @Autowired
    private UsuarioService usuarioService;

    @NonNull
    public Page<@NonNull UsuarioInstitucionDTO> buscarPorFiltros(FiltroUsuario filtro, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "usuarioColegioId");
        filtro.setRolId(Rol.ID_COLEGIO);
        return usuarioService.buscarUsuarioInstitucionPorFiltros(filtro, pageable);
    }

    public UsuarioInstitucionDTO obtenerUsuario(Long id) {
        var ie = usuarioService.obtenerPorId(id);

        if (ie.isPresent()) {
            return ie.get();
        }

        return null;
    }

    public Long crearUsuario(UsuarioInstitucionDTO data) {
        var usuario = usuarioService.crearUsuario(data, Rol.ID_COLEGIO);

        return usuario.getId();
    }

    public Long editarUsuario(Long id, UsuarioInstitucionDTO data) {
        var usuario = usuarioService.actualizarUsuario(id, data);

        return usuario.getId();
    }

    public int inactivarUsuario(Long id) {
        return usuarioService.inactivarById(id);
    }
}
