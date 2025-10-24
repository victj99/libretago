package com.utp.libretago.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.dto.UsuarioDTO;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;
import com.utp.libretago.classes.filtros.FiltroUsuario;
import com.utp.libretago.entity.Rol;
import com.utp.libretago.entity.Usuario;
import com.utp.libretago.entity.UsuarioInstitucion;
import com.utp.libretago.repository.UsuarioInstitucionRepository;
import com.utp.libretago.repository.UsuarioRepository;
import com.utp.libretago.service.UsuarioService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.utp.libretago.utils.ExcelUtils;
import com.vaadin.hilla.exception.EndpointException;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioInstitucionRepository usuarioInstitucionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int COL_DNI = 0;
    private static final int COL_NOMBRES_COMPLETOS = 1;
    private static final int COL_CORREO = 2;
    private static final int COL_TELEFONO = 3;
    private static final int COL_ERRORES = 4;

    @Override
    public Page<UsuarioInstitucionDTO> buscarUsuarioInstitucionPorFiltros(FiltroUsuario filtro, Pageable pageable) {
        var datos = usuarioInstitucionRepository.findAll(filtro.generarFiltroUsuarioInstitucion(), pageable);

        return datos.map(item -> item.obtenerUsuarioInstitucionDTO());
    }

    @Override
    public List<LabelValueDTO> listarUsuariosPorNombre(Pageable pageable, FiltroUsuario filtro) {
        var datos = usuarioInstitucionRepository.findAll(filtro.generarFiltroUsuarioInstitucion(), pageable);

        return datos.stream().map(item -> {
            var usuarioColegio = item.getUsuarioColegio();
            return new LabelValueDTO(usuarioColegio.getNombreCompleto(), usuarioColegio.getId().toString());
        }).toList();
    }

    @Override
    public Optional<UsuarioInstitucionDTO> obtenerPorId(Long id) {
        var usuarioInstitucion = usuarioInstitucionRepository.findByUsuarioColegioId(id);

        if (!usuarioInstitucion.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(usuarioInstitucion.get().obtenerUsuarioInstitucionDTO());
    }

    @Override
    public Long obtenerIdColegioPorIdUsuario(Long idUsuario) {
        return usuarioInstitucionRepository.findIdColegioByIdUsuario(idUsuario);
    }

    @Override
    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        var usuario = usuarioRepository.findByNombreUsuario(nombreUsuario);
        return usuario;
    }

    @Override
    public Usuario obtenerPorNombreUsuarioConRoles(String nombreUsuario) {
        var usuario = usuarioRepository.findByNombreUsuarioFetchRoles(nombreUsuario);
        return usuario;
    }

    @Override
    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO, Long rolId) {

        if (rolId == null) {
            throw new EndpointException("Debe especificarse el rol del usuario");
        }

        var existente = usuarioRepository.findByNombreUsuario(usuarioDTO.getNombreUsuario());

        if (existente != null) {
            if (!rolId.equals(Rol.ID_APODERADO) && !rolId.equals(Rol.ID_PROFESOR)) {
                throw new EndpointException("El nombre de usuario ya existe");
            }

            // Agregar el nuevo rol solo si el usuario no lo tiene
            if (existente.getRoles().stream().noneMatch(rol -> rol.getId().equals(rolId))) {
                existente.getRoles().add(new Rol(rolId));
                existente = usuarioRepository.save(existente);
            }

            usuarioDTO.setId(existente.getId());
            return usuarioDTO;
        } else {
            var usuarioEntity = usuarioDTO.obtenerUsuario();
            usuarioEntity.setContrasenia(passwordEncoder.encode(usuarioDTO.getNombreUsuario()));
            if (rolId != null) {
                usuarioEntity.getRoles().add(new Rol(rolId));
            }

            usuarioEntity = usuarioRepository.save(usuarioEntity);
            usuarioDTO.setId(usuarioEntity.getId());

            if (usuarioDTO instanceof UsuarioInstitucionDTO) {
                var ieId = ((UsuarioInstitucionDTO) usuarioDTO).getInstitucionEducativaId();
                usuarioInstitucionRepository.save(new UsuarioInstitucion(usuarioEntity.getId(), ieId));
            }
        }

        return usuarioDTO;
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuario) {
        Optional<Usuario> existente = usuarioRepository.findById(id);
        if (!existente.isPresent())
            return null;

        Usuario e = existente.get();
        BeanUtils.copyProperties(usuario, e, "id", "fechaCreacion", "contrasenia");
        usuarioRepository.save(e);

        return usuario;
    }

    @Override
    @Transactional
    public int inactivarById(Long id) {
        return usuarioRepository.inactivarUsuarioPorId(id);
    }

    @Override
    public ExcelValidadoDTO<UsuarioDTO> validarArchivo(MultipartFile file) throws IOException {
        List<UsuarioDTO> usuariosValidos = new ArrayList<>();
        Map<Integer, String> errores = new HashMap<>();
        String archivoErroresId = null;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean tieneErrores = false;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row fila = sheet.getRow(i);
                if (fila == null)
                    continue;

                UsuarioDTO usuario = extraerDatosUsuario(fila);

                String error = validarUsuario(usuario);
                if (error != null) {
                    tieneErrores = true;
                    errores.put(i, error.replace(". ", ".\n"));
                } else {
                    usuariosValidos.add(usuario);
                }
            }

            if (tieneErrores) {
                archivoErroresId = ExcelUtils.generarArchivoErrores(workbook, errores, COL_ERRORES);
                usuariosValidos.clear();
            }
        }

        return new ExcelValidadoDTO<>(archivoErroresId, usuariosValidos);
    }

    private UsuarioDTO extraerDatosUsuario(Row fila) {
        String dni = ExcelUtils.getValorCeldaComoTexto(fila, COL_DNI);
        var usuarioExistente = obtenerPorNombreUsuario(dni);

        var usuario = new UsuarioDTO();
        usuario.setId(usuarioExistente != null ? usuarioExistente.getId() : null);
        usuario.setNombreCompleto(ExcelUtils.getValorCeldaComoTexto(fila, COL_NOMBRES_COMPLETOS));
        usuario.setNombreUsuario(dni);
        usuario.setEmail(ExcelUtils.getValorCeldaComoTexto(fila, COL_CORREO));
        usuario.setTelefono(ExcelUtils.getValorCeldaComoTexto(fila, COL_TELEFONO));
        usuario.setActivo(true);
        return usuario;
    }

    private String validarUsuario(UsuarioDTO usuario) {
        String error = "";

        String errNombres = ExcelUtils.validarLargoCampo(usuario.getNombreCompleto(), "Nombres", 255, true);
        if (errNombres != null)
            error += errNombres;

        String errDni = ExcelUtils.validarLargoCampo(usuario.getNombreUsuario(), "DNI", 9, true);
        if (errDni != null)
            error += errDni;

        if (usuario.getId() != null) {
            return error.length() > 0 ? error : null;
        }

        String errCorreo = ExcelUtils.validarCorreo(usuario.getEmail());
        if (errCorreo != null)
            error += errCorreo;

        String errTelefono = ExcelUtils.validarLargoCampo(usuario.getTelefono(), "Teléfono", 20, false);
        if (errTelefono != null)
            error += errTelefono;

        return error.length() > 0 ? error : null;
    }
}
