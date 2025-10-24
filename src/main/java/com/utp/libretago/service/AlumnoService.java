package com.utp.libretago.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.AlumnoDTO;
import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.filtros.FiltroAlumno;

public interface AlumnoService {
    Page<AlumnoDTO> buscarAlumnosPorFiltros(FiltroAlumno filtro, Pageable pageable);

    List<Alumno2DTO> listarAlumnosPorCodigos(List<String> codigos, Long institucionEducativaId);

    Optional<AlumnoDTO> obtenerPorId(Long id);

    AlumnoDTO crearAlumno(AlumnoDTO alumno, Long institucionId);

    AlumnoDTO actualizarAlumno(Long id, AlumnoDTO alumno);

    int inactivarById(Long id);

    ExcelValidadoDTO<AlumnoDTO> validarArchivo(MultipartFile file) throws IOException;
}
