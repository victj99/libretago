package com.utp.libretago.endpoint;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.GrupoDTO;
import com.utp.libretago.classes.dto.IdLabelDTO;
import com.utp.libretago.classes.filtros.FiltroGrupo;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.service.GrupoService;
import com.utp.libretago.utils.Reutilizables;
import com.vaadin.hilla.Endpoint;

import jakarta.annotation.security.RolesAllowed;

@Endpoint
@RolesAllowed({ RolesEnum.COLEGIO })
public class GrupoEndpoint {

    @Autowired
    private GrupoService grupoService;

    @NonNull
    public Page<@NonNull GrupoDTO> buscarPorFiltros(FiltroGrupo filtro, Pageable pageable) {
        pageable = Reutilizables.ordernarPorDefectoDesc(pageable, "id");
        return grupoService.buscarGruposPorFiltros(filtro, pageable);
    }

    public GrupoDTO obtenerGrupo(Long id) {
        var grupo = grupoService.obtenerPorId(id);
        if (grupo.isPresent()) {
            return grupo.get();
        }
        return null;
    }

    public Long crearGrupo(GrupoDTO data) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        data.setInstitucionEducativaId(appUser.getInstitucionEducativaId());
        var grupoDTO = grupoService.crearGrupo(data);
        return grupoDTO.getId();
    }

    public Long editarGrupo(Long id, GrupoDTO data) {
        var grupoDTO = grupoService.actualizarGrupo(id, data);
        return grupoDTO.getId();
    }

    public int inactivarGrupo(Long id) {
        return grupoService.inactivarById(id);
    }

    @NonNull
    public List<@NonNull Alumno2DTO> listarAlumnosPorGrupoId(Long grupoId) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return grupoService.listarAlumnosPorGrupoId(grupoId, appUser.getInstitucionEducativaId());
    }

    @NonNull
    public List<@NonNull IdLabelDTO> listarGruposPorNombre(Pageable pageable, String nombre) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var filtro = new FiltroGrupo();
        filtro.setNombre(nombre);
        filtro.setInstitucionEducativaId(appUser.getInstitucionEducativaId());
        filtro.setActivo(true);

        return grupoService.listarGruposPorNombre(pageable, filtro);
    }
}