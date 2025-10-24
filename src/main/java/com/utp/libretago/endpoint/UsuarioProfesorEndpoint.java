package com.utp.libretago.endpoint;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.dto.UsuarioDTO;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;
import com.utp.libretago.classes.filtros.FiltroUsuario;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.entity.Rol;
import com.utp.libretago.service.UsuarioService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

@Endpoint
@RolesAllowed(RolesEnum.COLEGIO)
public class UsuarioProfesorEndpoint {

    @Autowired
    private UsuarioService usuarioService;

    @NonNull
    public Page<@NonNull UsuarioInstitucionDTO> buscarPorFiltros(FiltroUsuario filtro, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "usuarioColegioId");

        filtro.setRolId(Rol.ID_PROFESOR);
        return usuarioService.buscarUsuarioInstitucionPorFiltros(filtro, pageable);
    }

    public UsuarioInstitucionDTO obtenerUsuario(Long id) {
        var ie = usuarioService.obtenerPorId(id);

        if (ie.isPresent()) {
            return ie.get();
        }

        return null;
    }

    public Long crearUsuario(UsuarioDTO data) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var usuarioInstitucionDTO = new UsuarioInstitucionDTO();

        BeanUtils.copyProperties(data, usuarioInstitucionDTO);
        usuarioInstitucionDTO.setInstitucionEducativaId(appUser.getInstitucionEducativaId());

        var usuario = usuarioService.crearUsuario(usuarioInstitucionDTO, Rol.ID_PROFESOR);

        return usuario.getId();
    }

    public Long editarUsuario(Long id, UsuarioDTO data) {
        var usuario = usuarioService.actualizarUsuario(id, data);

        return usuario.getId();
    }

    public int inactivarUsuario(Long id) {
        return usuarioService.inactivarById(id);
    }

    @NonNull
    public List<@NonNull LabelValueDTO> listarUsuarios(Pageable pageable, String nombre) {
        FiltroUsuario filtro = new FiltroUsuario();
        filtro.setNombreCompleto(nombre);
        filtro.setRolId(Rol.ID_PROFESOR);

        return usuarioService.listarUsuariosPorNombre(pageable, filtro);
    }

    public ExcelValidadoDTO<@NonNull UsuarioDTO> validarArchivo(MultipartFile file) throws IOException {
        return usuarioService.validarArchivo(file);
    }
}
