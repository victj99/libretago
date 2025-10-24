package com.utp.libretago.endpoint;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.AlumnoDTO;
import com.utp.libretago.classes.dto.ExcelValidadoDTO;
import com.utp.libretago.classes.filtros.FiltroAlumno;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.service.AlumnoService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

@Endpoint
@RolesAllowed({ RolesEnum.COLEGIO })
public class AlumnoEndpoint {

    @Autowired
    private AlumnoService alumnoService;

    @NonNull
    public Page<@NonNull AlumnoDTO> buscarPorFiltros(FiltroAlumno filtro, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");
        return alumnoService.buscarAlumnosPorFiltros(filtro, pageable);
    }

    public AlumnoDTO obtenerAlumno(Long id) {
        var ie = alumnoService.obtenerPorId(id);

        if (ie.isPresent()) {
            return ie.get();
        }

        return null;
    }

    public Long crearAlumno(AlumnoDTO data) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var institucion = alumnoService.crearAlumno(data, appUser.getInstitucionEducativaId());

        return institucion.id();
    }

    public Long editarAlumno(Long id, AlumnoDTO data) {
        var alumno = alumnoService.actualizarAlumno(id, data);

        return alumno.id();
    }

    public int inactivarAlumno(Long id) {
        return alumnoService.inactivarById(id);
    }

    @NonNull
    public List<@NonNull Alumno2DTO> listarAlumnosPorCodigo(List<String> codigosAlumno) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return alumnoService.listarAlumnosPorCodigos(codigosAlumno, appUser.getInstitucionEducativaId());
    }

    public ExcelValidadoDTO<@NonNull AlumnoDTO> validarArchivo(@NonNull MultipartFile file) throws Exception {
        return alumnoService.validarArchivo(file);
    }
}
