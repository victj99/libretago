package com.utp.libretago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.libretago.entity.Rol;

/**
 * Repositorio para la gestión de la entidad {@link Rol}.
 * <p>
 * Extiende {@link JpaRepository} para proporcionar operaciones CRUD básicas
 * sobre la tabla de roles del sistema.
 * </p>
 *
 * <p>
 * Esta interfaz permite acceder a la información de los roles disponibles
 * en la aplicación (por ejemplo, Colegio, Profesor, Apoderado).
 * </p>
 *
 * <p>
 * No define consultas personalizadas porque las operaciones estándar son
 * suficientes para el manejo de roles dentro del sistema.
 * </p>
 *
 * @see Rol
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface RolRepository extends JpaRepository<Rol, Long> {
}
