package com.utp.libretago.service.impl;

import java.io.IOException;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.utp.libretago.classes.dto.*;
import com.utp.libretago.classes.filtros.FiltroAlumno;
import com.utp.libretago.entity.Alumno;
import com.utp.libretago.entity.Rol;
import com.utp.libretago.repository.AlumnoRepository;
import com.utp.libretago.service.AlumnoService;
import com.utp.libretago.service.UsuarioService;
import com.utp.libretago.utils.ExcelUtils;

/**
 * Implementación del servicio {@link AlumnoService} que gestiona las operaciones de negocio relacionadas con los
 * alumnos.
 * <p>
 * Incluye funcionalidades como filtrado, creación, actualización, inactivación y validación de datos provenientes de
 * archivos Excel.
 * </p>
 *
 * <p>
 * Este servicio interactúa con {@link AlumnoRepository} para operaciones de persistencia y con {@link UsuarioService}
 * para la gestión de apoderados.
 * </p>
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
@Service
public class AlumnoServiceImpl implements AlumnoService {

    // --- Constantes de posición de columnas en el archivo Excel ---
    private static final int COL_NOMBRES = 0;
    private static final int COL_APELLIDOS = 1;
    private static final int COL_CODIGO_ALUMNO = 2;
    private static final int COL_TELEFONO = 3;
    private static final int COL_CORREO = 4;
    private static final int COL_DNI_APODERADO = 5;
    private static final int COL_NOMBRE_APODERADO = 6;
    private static final int COL_ERRORES = 7;

    // --- Dependencias inyectadas ---
    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private UsuarioService usuarioService;

    // -------------------------------------------------------------------------
    // MÉTODOS PÚBLICOS (INTERFAZ)
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<AlumnoDTO> buscarAlumnosPorFiltros(FiltroAlumno filtro, Pageable pageable) {
        var datos = alumnoRepository.findAll(filtro.generarFiltroAlumno(), pageable);
        return datos.map(Alumno::obtenerAlumnoDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Alumno2DTO> listarAlumnosPorCodigos(List<String> codigos, Long institucionEducativaId) {
        var alumnos = alumnoRepository.findByCodigoAlumnoIn(codigos, institucionEducativaId);
        return alumnos.stream().map(Alumno::obtenerAlumno2DTO).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AlumnoDTO> obtenerPorId(Long id) {
        var alumnoOpt = alumnoRepository.findById(id);

        if (alumnoOpt.isEmpty() || alumnoOpt.get().getUsuarioApoderadoId() == null) {
            return Optional.empty();
        }

        return Optional.of(alumnoOpt.get().obtenerAlumnoDTO());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AlumnoDTO crearAlumno(AlumnoDTO alumnoDTO, Long institucionEducativaId) {
        var alumno = alumnoDTO.obtenerAlumno();
        alumno.setInstitucionEducativaId(institucionEducativaId);

        var usuario = usuarioService.obtenerPorNombreUsuario(alumnoDTO.dniCeApoderado());

        if (usuario == null) {
            var usuarioDTO = usuarioService.crearUsuario(new UsuarioDTO(alumnoDTO), Rol.ID_APODERADO);
            usuario = usuarioDTO.obtenerUsuario();
        }

        alumno.setUsuarioApoderadoId(usuario.getId());
        alumno = alumnoRepository.save(alumno);

        return alumnoDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AlumnoDTO actualizarAlumno(Long id, AlumnoDTO alumno) {
        Optional<Alumno> existing = alumnoRepository.findById(id);

        if (existing.isEmpty()) {
            return null;
        }

        Alumno e = existing.get();
        BeanUtils.copyProperties(alumno, e, "id", "fechaCreacion");
        alumnoRepository.save(e);

        return alumno;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public int inactivarById(Long id) {
        return alumnoRepository.inactivarAlumnoPorId(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExcelValidadoDTO<AlumnoDTO> validarArchivo(MultipartFile file) throws IOException {
        List<AlumnoDTO> alumnosValidos = new ArrayList<>();
        Map<Integer, String> errores = new HashMap<>();
        String archivoErroresId = null;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean tieneErrores = false;

            // Recorre todas las filas del Excel (omitimos la cabecera)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row fila = sheet.getRow(i);
                if (fila == null)
                    continue;

                AlumnoDTO alumno = extraerDatosAlumno(fila);

                String error = validarAlumno(alumno);
                if (error != null) {
                    tieneErrores = true;
                    errores.put(i, error.replace(". ", ".\n"));
                } else {
                    alumnosValidos.add(alumno);
                }
            }

            // Genera un archivo de errores si existen registros inválidos
            if (tieneErrores) {
                archivoErroresId = ExcelUtils.generarArchivoErrores(workbook, errores, COL_ERRORES);
                alumnosValidos.clear();
            }
        }

        return new ExcelValidadoDTO<>(archivoErroresId, alumnosValidos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StudentStatsDTO obtenerEstadisticas(Long institucionEducativaId) {
        long activeCount = alumnoRepository.countByInstitucionEducativaIdAndActivo(institucionEducativaId, true);
        long inactiveCount = alumnoRepository.countByInstitucionEducativaIdAndActivo(institucionEducativaId, false);
        return new StudentStatsDTO(activeCount, inactiveCount);
    }

    // -------------------------------------------------------------------------
    // MÉTODOS PRIVADOS (UTILITARIOS INTERNOS)
    // -------------------------------------------------------------------------

    /**
     * Valida los campos de un alumno leído desde el archivo Excel.
     *
     * @param alumno objeto {@link AlumnoDTO} que contiene los datos a validar.
     * @return cadena con los errores encontrados o {@code null} si no hay errores.
     */
    private String validarAlumno(AlumnoDTO alumno) {
        String error = "";

        String errNombres = ExcelUtils.validarLargoCampo(alumno.nombres(), "Nombres", 255, true);
        if (errNombres != null)
            error += errNombres;

        String errApellidos = ExcelUtils.validarLargoCampo(alumno.apellidos(), "Apellidos", 255, true);
        if (errApellidos != null)
            error += errApellidos;

        String errCodigo = ExcelUtils.validarLargoCampo(alumno.codigoAlumno(), "Código de alumno", 5, true);
        if (errCodigo != null)
            error += errCodigo;

        // Si el alumno ya existe, no se validan datos del apoderado
        if (alumno.id() != null) {
            return error.isEmpty() ? null : error;
        }

        String errTelefono = ExcelUtils.validarLargoCampo(alumno.telefono(), "Teléfono", 20, false);
        if (errTelefono != null)
            error += errTelefono;

        String errCorreo = ExcelUtils.validarCorreo(alumno.email());
        if (errCorreo != null)
            error += errCorreo;

        String errDni = ExcelUtils.validarLargoCampo(alumno.dniCeApoderado(), "DNI/CE del apoderado", 9, true);
        if (errDni != null)
            error += errDni;

        String errNombreApoderado = ExcelUtils.validarLargoCampo(alumno.nombreCompletoApoderado(), "Nombre del apoderado", 255, true);
        if (errNombreApoderado != null)
            error += errNombreApoderado;

        return error.isEmpty() ? null : error;
    }

    /**
     * Extrae los datos de una fila del archivo Excel y los convierte en un {@link AlumnoDTO}.
     *
     * @param fila fila del archivo Excel que contiene la información del alumno.
     * @return un nuevo objeto {@link AlumnoDTO} con los datos extraídos.
     */
    private AlumnoDTO extraerDatosAlumno(Row fila) {
        String codigoAlumno = ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_CODIGO_ALUMNO));
        var idExistente = alumnoRepository.findIdByCodigoAlumno(codigoAlumno);

        return new AlumnoDTO(idExistente != null ? idExistente : null, ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_NOMBRES)),
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_APELLIDOS)), codigoAlumno,
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_TELEFONO)), ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_CORREO)),
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_DNI_APODERADO)),
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_NOMBRE_APODERADO)), true);
    }
}
