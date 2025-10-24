package com.utp.libretago.endpoint;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.LabelValueDTO;
import com.utp.libretago.classes.filtros.FiltroInstitucionEducativa;
import com.utp.libretago.entity.InstitucionEducativa;
import com.utp.libretago.service.InstitucionEducativaService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

@Endpoint
@RolesAllowed(RolesEnum.ADMIN)
public class InstitucionEducativaEndpoint {
    @Autowired
    private InstitucionEducativaService institucionEducativaService;

    @NonNull
    public Page<@NonNull InstitucionEducativa> buscarPorFiltros(FiltroInstitucionEducativa filtro, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");
        return institucionEducativaService.buscarPorFiltros(filtro, pageable);
    }

    public InstitucionEducativa obtenerInstitucion(Long id) {
        var ie = institucionEducativaService.obtenerPorId(id);

        if (ie.isPresent()) {
            return ie.get();
        }

        return null;

    }

    public Long crearInstitucion(InstitucionEducativa data) {
        var institucion = institucionEducativaService.crearInstitucion(data);

        return institucion.getId();
    }

    public Long editarInstitucion(Long id, InstitucionEducativa data) {
        var institucion = institucionEducativaService.actualizarInstitucion(id, data);

        return institucion.getId();
    }

    public int inactivarInstitucion(Long id) {
        return institucionEducativaService.inactivarById(id);
    }

    @NonNull
    public List<@NonNull LabelValueDTO> listarInstituciones(Pageable pageable, String nombre) {
        return institucionEducativaService.listarInstitucionesPorNombre(pageable, nombre);
    }
}
