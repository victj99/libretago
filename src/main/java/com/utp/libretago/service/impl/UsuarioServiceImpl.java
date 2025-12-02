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

import com.utp.libretago.classes.dto.ColegioStatsDTO;
import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.dto.ProfesorStatsDTO;
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

/**
 * Implementación del servicio {@link UsuarioService}. Gestiona las operaciones relacionadas con los usuarios del
 * sistema, incluyendo creación, actualización, búsqueda, inactivación y validación de usuarios a partir de archivos
 * Excel.
 *
 * <p>
 * Este servicio también maneja la relación entre usuarios y sus instituciones educativas, además de la asignación de
 * roles según el tipo de usuario (profesor, apoderado, colegio, etc.).
 * </p>
 *
 * @author Roberto
 * @version 1.0
 * @since 2025-10-28
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioInstitucionRepository usuarioInstitucionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    // Constantes para las columnas del archivo Excel
    private static final int COL_DNI = 0;
    private static final int COL_NOMBRES_COMPLETOS = 1;
    private static final int COL_CORREO = 2;
    private static final int COL_TELEFONO = 3;
    private static final int COL_ERRORES = 4;

    /**
     * Busca usuarios-institución según filtros aplicados.
     * 
     * @param filtro   Objeto con criterios de búsqueda.
     * @param pageable Configuración de paginación.
     * @return Página de usuarios en formato {@link UsuarioInstitucionDTO}.
     */
    @Override
    public Page<UsuarioInstitucionDTO> buscarUsuarioInstitucionPorFiltros(FiltroUsuario filtro, Pageable pageable) {
        var datos = usuarioInstitucionRepository.findAll(filtro.generarFiltroUsuarioInstitucion(), pageable);

        return datos.map(item -> item.obtenerUsuarioInstitucionDTO());
    }

    /**
     * Lista usuarios según su nombre, aplicando filtros y paginación.
     * 
     * @param pageable Configuración de paginación.
     * @param filtro   Criterios de búsqueda.
     * @return Lista de usuarios con formato {@link LabelValueDTO}.
     */
    @Override
    public List<LabelValueDTO> listarUsuariosPorNombre(Pageable pageable, FiltroUsuario filtro) {
        var datos = usuarioInstitucionRepository.findAll(filtro.generarFiltroUsuarioInstitucion(), pageable);

        return datos.stream().map(item -> {
            var usuarioColegio = item.getUsuarioColegio();
            return new LabelValueDTO(usuarioColegio.getNombreCompleto(), usuarioColegio.getId().toString());
        }).toList();
    }

    /**
     * Obtiene un usuario-institución por su ID.
     * <p>
     * Si el usuario es profesor, busca la relación específica con la institución del contexto de seguridad actual para
     * obtener el estado activo correcto. Si tiene múltiples instituciones y no es profesor, devuelve la primera.
     * </p>
     * 
     * @param id ID del usuario.
     * @return Optional con {@link UsuarioInstitucionDTO} si existe, vacío si no.
     */
    @Override
    public Optional<UsuarioInstitucionDTO> obtenerPorId(Long id) {
        var usuarioInstituciones = usuarioInstitucionRepository.findByUsuarioColegioId(id);

        if (usuarioInstituciones.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(usuarioInstituciones.get(0).obtenerUsuarioInstitucionDTO());
    }

    /**
     * Obtiene el ID de la institución educativa asociada a un usuario. Si tiene múltiples, devuelve el primero.
     * 
     * @param idUsuario ID del usuario.
     * @return ID de la institución educativa o {@code null} si no existe.
     */
    @Override
    public Long obtenerIdColegioPorIdUsuario(Long idUsuario) {
        var ids = usuarioInstitucionRepository.findIdColegioByIdUsuario(idUsuario);
        return (ids != null && !ids.isEmpty()) ? ids.get(0) : null;
    }

    @Override
    public List<UsuarioInstitucionDTO> listarInstitucionesPorUsuario(Long idUsuario) {
        var usuarioInstituciones = usuarioInstitucionRepository.findByUsuarioColegioId(idUsuario);
        return usuarioInstituciones.stream().map(UsuarioInstitucion::obtenerUsuarioInstitucionDTO).toList();
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param nombreUsuario Nombre de usuario.
     * @return Objeto {@link Usuario} si existe.
     */
    @Override
    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        var usuario = usuarioRepository.findByNombreUsuario(nombreUsuario);
        return usuario;
    }

    /**
     * Busca un usuario por nombre de usuario e incluye sus roles.
     * 
     * @param nombreUsuario Nombre de usuario.
     * @return Usuario con roles cargados.
     */
    @Override
    public Usuario obtenerPorNombreUsuarioConRoles(String nombreUsuario) {
        var usuario = usuarioRepository.findByNombreUsuarioFetchRoles(nombreUsuario);
        return usuario;
    }

    /**
     * Crea un nuevo usuario o le asigna un nuevo rol si ya existe. También asocia el usuario a una institución educativa si
     * corresponde.
     * 
     * @param usuarioDTO Datos del usuario a crear.
     * @param rolId      ID del rol a asignar.
     * @return Usuario creado en formato {@link UsuarioDTO}.
     */
    @Override
    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO, Long rolId) {

        if (rolId == null) {
            throw new EndpointException("Debe especificarse el rol del usuario");
        }

        var existente = usuarioRepository.findByNombreUsuario(usuarioDTO.getNombreUsuario());

        if (existente != null) {
            // Si ya existe y el rol no es de apoderado o profesor, lanzar error
            if (!rolId.equals(Rol.ID_APODERADO) && !rolId.equals(Rol.ID_PROFESOR)) {
                throw new EndpointException("El nombre de usuario ya existe");
            }

            // Agregar el nuevo rol solo si el usuario no lo tiene
            if (existente.getRoles().stream().noneMatch(rol -> rol.getId().equals(rolId))) {
                existente.getRoles().add(new Rol(rolId));
                existente = usuarioRepository.save(existente);
            }

            // Si es profesor, verificar y crear relación con la institución actual
            if (rolId.equals(Rol.ID_PROFESOR) && usuarioDTO instanceof UsuarioInstitucionDTO) {
                var ieId = ((UsuarioInstitucionDTO) usuarioDTO).getInstitucionEducativaId();
                if (ieId != null) {
                    var instituciones = usuarioInstitucionRepository.findIdColegioByIdUsuario(existente.getId());
                    if (!instituciones.contains(ieId)) {
                        usuarioInstitucionRepository.save(new UsuarioInstitucion(existente.getId(), ieId));
                    }
                }
            }

            usuarioDTO.setId(existente.getId());
            return usuarioDTO;
        } else {
            // Crear un nuevo usuario con contraseña igual a su nombre de usuario
            var usuarioEntity = usuarioDTO.obtenerUsuario();
            usuarioEntity.setContrasenia(passwordEncoder.encode(usuarioDTO.getNombreUsuario()));
            // Asociar a institución si aplica
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

    /**
     * Actualiza los datos de un usuario existente.
     * <p>
     * Si el usuario es profesor, actualiza el campo activo solo en la relación usuario-institución basándose en la
     * institución del contexto de seguridad, no en la entidad Usuario.
     * </p>
     * 
     * @param id      ID del usuario a actualizar.
     * @param usuario Datos nuevos del usuario.
     * @return Usuario actualizado en formato {@link UsuarioDTO}.
     */
    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuario) {
        Optional<Usuario> existente = usuarioRepository.findById(id);
        if (!existente.isPresent())
            return null;

        Usuario e = existente.get();
        boolean esProfesor = e.getRoles().stream().anyMatch(r -> r.getId().equals(Rol.ID_PROFESOR));
        boolean esColegio = e.getRoles().stream().anyMatch(r -> r.getId().equals(Rol.ID_COLEGIO));

        if (esProfesor || esColegio) {
            // Si es profesor, no actualizar el campo activo en Usuario, solo en UsuarioInstitucion
            BeanUtils.copyProperties(usuario, e, "id", "fechaCreacion", "contrasenia", "activo");

            if (esColegio)
                e.setActivo(usuario.getActivo());

            usuarioRepository.save(e);

            // Actualizar el campo activo en usuario_institucion
            if (usuario.getActivo() != null) {
                usuarioInstitucionRepository.actualizarActivoByUsuarioIdAndInstitucionId(id, usuario.getActivo());
            }
        } else {
            // Para otros roles, actualizar todo incluyendo activo en Usuario
            BeanUtils.copyProperties(usuario, e, "id", "fechaCreacion", "contrasenia");
            usuarioRepository.save(e);
        }

        return usuario;
    }

    /**
     * Inactiva un usuario según su ID. Si es profesor o colegio, inactiva la relación usuario-institución. Si no es
     * profesor ni colegio, inactiva el usuario globalmente.
     * 
     * @param id ID del usuario.
     * @return Número de registros afectados.
     */
    @Override
    @Transactional
    public int inactivarById(Long id) {
        var usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty())
            return 0;

        var usuario = usuarioOpt.get();
        boolean esProfesor = usuario.getRoles().stream().anyMatch(r -> r.getId().equals(Rol.ID_PROFESOR));
        boolean esColegio = usuario.getRoles().stream().anyMatch(r -> r.getId().equals(Rol.ID_COLEGIO));

        if (esProfesor || esColegio) {
            if (esColegio)
                usuarioRepository.inactivarUsuarioPorId(id);
            // Obtener institución actual del contexto (quien realiza la acción)
            return usuarioInstitucionRepository.inactivarByUsuarioIdAndInstitucionId(id);
        }

        return usuarioRepository.inactivarUsuarioPorId(id);
    }

    /**
     * Valida un archivo Excel que contiene usuarios, verificando formato y datos. Si existen errores, se genera un archivo
     * con las observaciones.
     * 
     * @param file Archivo Excel cargado.
     * @return Resultado de la validación, incluyendo lista de usuarios válidos o archivo de errores.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
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
                // Registrar errores o usuario válido
                String error = validarUsuario(usuario);
                if (error != null) {
                    tieneErrores = true;
                    errores.put(i, error.replace(". ", ".\n"));
                } else {
                    usuariosValidos.add(usuario);
                }
            }
            // Si hay errores, generar archivo Excel con los detalles
            if (tieneErrores) {
                archivoErroresId = ExcelUtils.generarArchivoErrores(workbook, errores, COL_ERRORES);
                usuariosValidos.clear();
            }
        }

        return new ExcelValidadoDTO<>(archivoErroresId, usuariosValidos);
    }

    /**
     * Extrae los datos de un usuario desde una fila del Excel.
     * 
     * @param fila Fila del archivo Excel.
     * @return Objeto {@link UsuarioDTO} con los datos extraídos.
     */
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

    /**
     * Valida los campos de un usuario cargado desde Excel.
     * 
     * @param usuario Usuario a validar.
     * @return Mensaje de error si existe, o {@code null} si es válido.
     */
    private String validarUsuario(UsuarioDTO usuario) {
        String error = "";
        // Validar campos obligatorios
        String errNombres = ExcelUtils.validarLargoCampo(usuario.getNombreCompleto(), "Nombres", 255, true);
        if (errNombres != null)
            error += errNombres;

        String errDni = ExcelUtils.validarLargoCampo(usuario.getNombreUsuario(), "DNI", 9, true);
        if (errDni != null)
            error += errDni;

        if (usuario.getId() != null) {
            return error.length() > 0 ? error : null;
        }
        // Validar campos opcionales
        String errCorreo = ExcelUtils.validarCorreo(usuario.getEmail());
        if (errCorreo != null)
            error += errCorreo;

        String errTelefono = ExcelUtils.validarLargoCampo(usuario.getTelefono(), "Teléfono", 20, false);
        if (errTelefono != null)
            error += errTelefono;

        return error.length() > 0 ? error : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProfesorStatsDTO obtenerEstadisticasProfesores(Long institucionEducativaId) {
        long activeCount = usuarioInstitucionRepository.countByInstitucionIdAndActivoAndRolProfesor(institucionEducativaId, true);
        long inactiveCount = usuarioInstitucionRepository.countByInstitucionIdAndActivoAndRolProfesor(institucionEducativaId, false);
        return new ProfesorStatsDTO(activeCount, inactiveCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColegioStatsDTO obtenerEstadisticasColegios() {
        long activeCount = usuarioRepository.countByRolIdAndActivo(Rol.ID_COLEGIO, true);
        long inactiveCount = usuarioRepository.countByRolIdAndActivo(Rol.ID_COLEGIO, false);
        return new ColegioStatsDTO(activeCount, inactiveCount);
    }
}
