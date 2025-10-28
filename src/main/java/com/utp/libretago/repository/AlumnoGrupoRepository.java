package com.utp.libretago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.Alumno;
import com.utp.libretago.entity.AlumnoGrupo;
import com.utp.libretago.entity.AlumnoGrupoId;

/**
 * Repositorio para la gestión de la entidad {@link AlumnoGrupo}.
 * <p>
 * Proporciona métodos personalizados para consultar relaciones entre alumnos y grupos
 * dentro de una institución educativa.
 * </p>
 *
 * <p>
 * Extiende {@link JpaRepository}, lo que permite realizar operaciones CRUD
 * y consultas derivadas sobre la tabla intermedia <strong>alumno_grupo</strong>.
 * </p>
 *
 * @see Alumno
 * @see com.utp.libretago.entity.Grupo
 * @see AlumnoGrupoId
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface AlumnoGrupoRepository extends JpaRepository<AlumnoGrupo, AlumnoGrupoId> {

    /**
     * Lista todos los alumnos pertenecientes a un grupo específico dentro de una institución educativa.
     *
     * @param grupoId              identificador del grupo.
     * @param institucionEducativaId identificador de la institución educativa.
     * @return lista de entidades {@link Alumno} asociadas al grupo indicado.
     */
    @Query("""
           SELECT ag.alumno
           FROM AlumnoGrupo ag
           INNER JOIN ag.alumno a
           WHERE ag.grupoId = ?1
             AND a.institucionEducativaId = ?2
           """)
    List<Alumno> listarAlumnosPorGrupoId(Long grupoId, Long institucionEducativaId);

    /**
     * Obtiene los identificadores de los alumnos asociados a un grupo dentro de una institución educativa.
     *
     * @param grupoId              identificador del grupo.
     * @param institucionEducativaId identificador de la institución educativa.
     * @return lista de identificadores de alumnos asociados al grupo.
     */
    @Query("""
           SELECT ag.alumno.id
           FROM AlumnoGrupo ag
           INNER JOIN ag.alumno a
           WHERE ag.grupoId = ?1
             AND a.institucionEducativaId = ?2
           """)
    List<Long> listarIdsAlumnoByGrupoId(Long grupoId, Long institucionEducativaId);

    /**
     * Obtiene los identificadores únicos de los grupos a los que pertenecen los alumnos
     * vinculados a un apoderado específico.
     *
     * @param apoderadoId identificador del usuario apoderado.
     * @return lista de identificadores únicos de grupos relacionados con el apoderado.
     */
    @Query("""
           SELECT DISTINCT ag.grupoId
           FROM AlumnoGrupo ag
           JOIN ag.alumno a
           WHERE a.usuarioApoderadoId = ?1
           """)
    List<Long> listarGrupoIdsPorApoderadoId(Long apoderadoId);
}
