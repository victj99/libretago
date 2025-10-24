package com.utp.libretago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.Alumno;

public interface AlumnoRepository extends JpaRepository<Alumno, Long>, JpaSpecificationExecutor<Alumno> {

    @Modifying
    @Query("UPDATE Alumno i SET i.activo = false WHERE i.id = ?1")
    int inactivarAlumnoPorId(Long id);
    
    @Query("SELECT a FROM Alumno a WHERE a.codigoAlumno = ?1")
    Alumno findByCodigoAlumno(String codigo);


    @Query("SELECT a.id FROM Alumno a WHERE a.codigoAlumno = ?1")
    Long findIdByCodigoAlumno(String codigo);

    @Query("SELECT a FROM Alumno a WHERE a.codigoAlumno IN (?1) AND a.institucionEducativaId = ?2 AND a.activo = true")
    List<Alumno> findByCodigoAlumnoIn(List<String> codigos, Long institucionEducativaId);

    @Query("SELECT a.usuarioApoderadoId FROM Alumno a WHERE a.id IN (?1)")
    List<Long> findApoderadoIdsByAlumnoIds(List<Long> alumnoIds);

    // Según la documentación de Spring se usa EntityGraph para obtener el usuarioApoderado en una sola consulta en ves de que jpa haga varios selects individuales
    @EntityGraph(attributePaths = {"usuarioApoderado"})
    Page<Alumno> findAll(Specification<Alumno> spec, Pageable pageable);
}