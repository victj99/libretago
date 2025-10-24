package com.utp.libretago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.Alumno;
import com.utp.libretago.entity.AlumnoGrupo;
import com.utp.libretago.entity.AlumnoGrupoId;

public interface AlumnoGrupoRepository extends JpaRepository<AlumnoGrupo, AlumnoGrupoId> {

    @Query("SELECT ag.alumno FROM AlumnoGrupo ag INNER JOIN ag.alumno a WHERE ag.grupoId = ?1 AND a.institucionEducativaId = ?2")
    List<Alumno> listarAlumnosPorGrupoId(Long grupoId, Long institucionEducativaId);

    @Query("SELECT ag.alumno.id FROM AlumnoGrupo ag INNER JOIN ag.alumno a WHERE ag.grupoId = ?1 AND a.institucionEducativaId = ?2")
    List<Long> listarIdsAlumnoByGrupoId(Long grupoId, Long institucionEducativaId);

    @Query("SELECT DISTINCT ag.grupoId FROM AlumnoGrupo ag JOIN ag.alumno a WHERE a.usuarioApoderadoId = ?1")
    List<Long> listarGrupoIdsPorApoderadoId(Long apoderadoId);
}