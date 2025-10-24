package com.utp.libretago.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.UsuarioInstitucion;
import com.utp.libretago.entity.UsuarioInstitucionId;

public interface UsuarioInstitucionRepository extends JpaRepository<UsuarioInstitucion, UsuarioInstitucionId>, JpaSpecificationExecutor<UsuarioInstitucion> {

    @Query("SELECT ui FROM UsuarioInstitucion ui WHERE ui.usuarioColegio.id = ?1")
    Optional<UsuarioInstitucion> findByUsuarioColegioId(Long usuarioId);

    @Query("SELECT ui.institucionEducativaId FROM UsuarioInstitucion ui WHERE ui.usuarioColegio.id = ?1")
    Long findIdColegioByIdUsuario(Long usuarioId);
}