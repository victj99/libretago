package com.utp.libretago.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.AlumnoDTO;
import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.dto.UsuarioDTO;
import com.utp.libretago.classes.filtros.FiltroAlumno;
import com.utp.libretago.entity.Alumno;
import com.utp.libretago.entity.Rol;
import com.utp.libretago.repository.AlumnoRepository;

import com.utp.libretago.service.AlumnoService;
import com.utp.libretago.service.UsuarioService;
import com.utp.libretago.utils.ExcelUtils;

@Service
public class AlumnoServiceImpl implements AlumnoService {

    private static final int COL_NOMBRES = 0;
    private static final int COL_APELLIDOS = 1;
    private static final int COL_CODIGO_ALUMNO = 2;
    private static final int COL_TELEFONO = 3;
    private static final int COL_CORREO = 4;
    private static final int COL_DNI_APODERADO = 5;
    private static final int COL_NOMBRE_APODERADO = 6;
    private static final int COL_ERRORES = 7;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public Page<AlumnoDTO> buscarAlumnosPorFiltros(FiltroAlumno filtro, Pageable pageable) {
        var datos = alumnoRepository.findAll(filtro.generarFiltroAlumno(), pageable);

        return datos.map(item -> item.obtenerAlumnoDTO());
    }

    @Override
    public List<Alumno2DTO> listarAlumnosPorCodigos(List<String> codigos, Long institucionEducativaId) {
        var alumnos = alumnoRepository.findByCodigoAlumnoIn(codigos, institucionEducativaId);

        return alumnos.stream().map(item -> item.obtenerAlumno2DTO()).toList();
    }

    @Override
    public Optional<AlumnoDTO> obtenerPorId(Long id) {
        var alumnoOpt = alumnoRepository.findById(id);

        if (!alumnoOpt.isPresent()) {
            return Optional.empty();
        }

        var alumno = alumnoOpt.get();

        if (alumno.getUsuarioApoderadoId() == null) {
            return Optional.empty();
        }

        return Optional.of(alumno.obtenerAlumnoDTO());
    }

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

    @Override
    @Transactional
    public AlumnoDTO actualizarAlumno(Long id, AlumnoDTO alumno) {
        Optional<Alumno> existing = alumnoRepository.findById(id);

        if (!existing.isPresent())
            return null;

        Alumno e = existing.get();
        BeanUtils.copyProperties(alumno, e, "id", "fechaCreacion");
        alumnoRepository.save(e);

        return alumno;
    }

    @Override
    @Transactional
    public int inactivarById(Long id) {
        return alumnoRepository.inactivarAlumnoPorId(id);
    }

    @Override
    public ExcelValidadoDTO<AlumnoDTO> validarArchivo(MultipartFile file) throws IOException {
        List<AlumnoDTO> alumnosValidos = new ArrayList<>();
        Map<Integer, String> errores = new HashMap<>();
        String archivoErroresId = null;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean tieneErrores = false;

            // Procesar filas (saltamos la primera fila ya que es la cabecera)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row fila = sheet.getRow(i);
                if (fila == null)
                    continue;

                // Extraer datos
                AlumnoDTO alumno = extraerDatosAlumno(fila);

                // Validar datos
                String error = validarAlumno(alumno);
                if (error != null) {
                    tieneErrores = true;
                    errores.put(i, error.replace(". ", ".\n"));
                } else {
                    alumnosValidos.add(alumno);
                }
            }

            if (tieneErrores) {
                archivoErroresId = ExcelUtils.generarArchivoErrores(workbook, errores, COL_ERRORES);
                alumnosValidos.clear();
            }
        }

        return new ExcelValidadoDTO<>(archivoErroresId, alumnosValidos);
    }

    private String validarAlumno(AlumnoDTO alumno) {
        String error = "";

        // Validar campos requeridos y formatos
        String errNombres = ExcelUtils.validarLargoCampo(alumno.nombres(), "Nombres", 255, true);
        if (errNombres != null)
            error += errNombres;

        String errApellidos = ExcelUtils.validarLargoCampo(alumno.apellidos(), "Apellidos", 255, true);
        if (errApellidos != null)
            error += errApellidos;

        String errCodigo = ExcelUtils.validarLargoCampo(alumno.codigoAlumno(), "Código de alumno", 5, true);
        if (errCodigo != null)
            error += errCodigo;

        // Si el alumno existe ya no es necesario validar los campos del apoderado
        if (alumno.id() != null) {
            return error.length() > 0 ? error : null;
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

        return error.length() > 0 ? error : null;
    }

    private AlumnoDTO extraerDatosAlumno(Row fila) {
        String codigoAlumno = ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_CODIGO_ALUMNO));
        var idExistente = alumnoRepository.findIdByCodigoAlumno(codigoAlumno);

        return new AlumnoDTO(idExistente != null ? idExistente : null, //
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_NOMBRES)), //
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_APELLIDOS)), //
                codigoAlumno, //
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_TELEFONO)), //
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_CORREO)), //
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_DNI_APODERADO)), //
                ExcelUtils.getValorCeldaComoTexto(fila.getCell(COL_NOMBRE_APODERADO)), //
                true);
    }
}
