package com.utp.libretago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.Grupo;

/**
 * Repositorio para la gestión de la entidad {@link Grupo}.
 * <p>
 * Extiende {@link JpaRepository} para ofrecer operaciones CRUD básicas y
 * {@link JpaSpecificationExecutor} para ejecutar consultas dinámicas basadas
 * en criterios personalizados (Specifications).
 * </p>
 *
 * <p>
 * Además, incluye un método personalizado para realizar la inactivación lógica
 * de grupos sin eliminarlos físicamente de la base de datos.
 * </p>
 *
 * @see Grupo
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface GrupoRepository extends JpaRepository<Grupo, Long>, JpaSpecificationExecutor<Grupo> {

    /**
     * Inactiva lógicamente un grupo estableciendo su campo {@code activo} en {@code false}.
     * <p>
     * Esta operación no elimina el registro, solo lo marca como inactivo para
     * mantener la integridad de los datos históricos.
     * </p>
     *
     * @param id identificador único del grupo a inactivar.
     * @return número de registros afectados por la actualización (normalmente 1).
     */
    @Modifying
    @Query("UPDATE Grupo i SET i.activo = false WHERE i.id = ?1")
    int inactivarGrupoPorId(Long id);
}
