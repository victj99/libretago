package com.utp.libretago.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.InstitucionEducativa;

import java.util.List;

/**
 * Repositorio para la gestión de la entidad {@link InstitucionEducativa}.
 * <p>
 * Extiende {@link JpaRepository} para operaciones CRUD básicas y
 * {@link JpaSpecificationExecutor} para consultas dinámicas mediante especificaciones
 * personalizadas (filtros complejos).
 * </p>
 *
 * <p>
 * Incluye métodos personalizados para inactivar instituciones educativas y
 * realizar búsquedas por nombre con coincidencias parciales e insensibles a mayúsculas.
 * </p>
 *
 * @see InstitucionEducativa
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 * @see org.springframework.data.domain.Pageable
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface InstitucionEducativaRepository extends JpaRepository<InstitucionEducativa, Long>,
        JpaSpecificationExecutor<InstitucionEducativa> {

    /**
     * Inactiva lógicamente una institución educativa estableciendo su campo {@code activo} en {@code false}.
     * <p>
     * Esta operación preserva el registro en la base de datos, permitiendo mantener
     * su historial sin eliminarlo físicamente.
     * </p>
     *
     * @param id identificador único de la institución educativa a inactivar.
     * @return número de registros afectados por la actualización (normalmente 1).
     */
    @Modifying
    @Query("UPDATE InstitucionEducativa i SET i.activo = false WHERE i.id = ?1")
    int inactivarInstitucionPorId(Long id);

    /**
     * Busca instituciones educativas cuyo nombre coincida parcialmente con el texto indicado,
     * sin distinguir entre mayúsculas y minúsculas.
     * <p>
     * El resultado puede limitarse o paginarse mediante el parámetro {@link Pageable}.
     * </p>
     *
     * @param nombre   texto parcial del nombre a buscar (por ejemplo: "%San%").
     * @param pageable objeto que define la paginación y el orden de los resultados.
     * @return lista de instituciones educativas cuyo nombre contiene el texto buscado.
     */
    List<InstitucionEducativa> findByNombreLikeIgnoreCase(String nombre, Pageable pageable);
}
