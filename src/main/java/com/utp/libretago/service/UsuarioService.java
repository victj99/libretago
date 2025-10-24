package com.utp.libretago.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.dto.UsuarioDTO;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;
import com.utp.libretago.classes.filtros.FiltroUsuario;
import com.utp.libretago.entity.Usuario;

public interface UsuarioService {

    ExcelValidadoDTO<UsuarioDTO> validarArchivo(MultipartFile file) throws IOException;

    Page<UsuarioInstitucionDTO> buscarUsuarioInstitucionPorFiltros(FiltroUsuario filtro, Pageable pageable);

    List<LabelValueDTO> listarUsuariosPorNombre(Pageable pageable, FiltroUsuario filtros);

    Optional<UsuarioInstitucionDTO> obtenerPorId(Long id);

    Long obtenerIdColegioPorIdUsuario(Long idUsuario);

    Usuario obtenerPorNombreUsuario(String nombreUsuario);

    Usuario obtenerPorNombreUsuarioConRoles(String nombreUsuario);

    UsuarioDTO crearUsuario(UsuarioDTO usuario, Long rolId);

    UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuario);

    int inactivarById(Long id);
}
