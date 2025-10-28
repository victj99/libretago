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

/**
 * Repositorio JPA para la entidad {@link Alumno}.
 * <p>
 * Esta interfaz proporciona métodos personalizados de acceso y manipulación
 * de datos de los alumnos, además de las operaciones básicas CRUD heredadas
 * de {@link JpaRepository}.
 * </p>
 * <p>
 * También implementa {@link JpaSpecificationExecutor} para permitir
 * búsquedas dinámicas mediante especificaciones (criterios).
 * </p>
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface AlumnoRepository extends JpaRepository<Alumno, Long>, JpaSpecificationExecutor<Alumno> {

    /**
     * Marca un alumno como inactivo (activo = false) según su ID.
     *
     * @param id identificador único del alumno a inactivar.
     * @return número de registros actualizados (1 si fue exitoso, 0 si no existe).
     */
    @Modifying
    @Query("UPDATE Alumno i SET i.activo = false WHERE i.id = ?1")
    int inactivarAlumnoPorId(Long id);
    
    /**
     * Busca un alumno por su código único.
     *
     * @param codigo código institucional del alumno.
     * @return el alumno correspondiente, o {@code null} si no existe.
     */
    @Query("SELECT a FROM Alumno a WHERE a.codigoAlumno = ?1")
    Alumno findByCodigoAlumno(String codigo);

    /**
     * Obtiene el ID de un alumno a partir de su código.
     *
     * @param codigo código institucional del alumno.
     * @return el identificador del alumno o {@code null} si no existe.
     */
    @Query("SELECT a.id FROM Alumno a WHERE a.codigoAlumno = ?1")
    Long findIdByCodigoAlumno(String codigo);

    /**
     * Busca todos los alumnos activos cuyo código esté en una lista y pertenezcan
     * a una institución educativa específica.
     *
     * @param codigos lista de códigos de alumnos.
     * @param institucionEducativaId identificador de la institución educativa.
     * @return lista de alumnos que cumplen los criterios.
     */
    @Query("SELECT a FROM Alumno a WHERE a.codigoAlumno IN (?1) AND a.institucionEducativaId = ?2 AND a.activo = true")
    List<Alumno> findByCodigoAlumnoIn(List<String> codigos, Long institucionEducativaId);

    /**
     * Obtiene los IDs de los apoderados asociados a una lista de alumnos.
     *
     * @param alumnoIds lista de identificadores de alumnos.
     * @return lista de IDs de los usuarios apoderados.
     */
    @Query("SELECT a.usuarioApoderadoId FROM Alumno a WHERE a.id IN (?1)")
    List<Long> findApoderadoIdsByAlumnoIds(List<Long> alumnoIds);

    /**
     * Recupera una página de alumnos según una {@link Specification} dada, 
     * utilizando un {@link EntityGraph} para incluir los datos del apoderado
     * en una sola consulta (evita múltiples consultas N+1).
     *
     * @param spec objeto de especificación JPA que define los criterios de búsqueda.
     * @param pageable información de paginación (número de página, tamaño, orden, etc.).
     * @return una página de alumnos que cumplen la especificación.
     */
    @EntityGraph(attributePaths = {"usuarioApoderado"})
    Page<Alumno> findAll(Specification<Alumno> spec, Pageable pageable);
}
