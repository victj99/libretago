package com.utp.libretago.service;

import com.utp.libretago.entity.Rol;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para la gestión de roles dentro del sistema.
 * 
 * Define las operaciones básicas de mantenimiento (CRUD) sobre la entidad {@link Rol}, que representa los distintos
 * niveles o permisos de acceso que puede tener un usuario.
 * 
 * Ejemplos de roles pueden ser: <strong>Administrador</strong>, <strong>Profesor</strong>, <strong>Apoderado</strong>,
 * entre otros.
 * 
 * @author Roberto
 * @version 1.0
 * @since 2025-10-28
 */

public interface RolService {

    /**
     * Obtiene la lista completa de roles registrados en el sistema.
     * 
     * @return Lista de objetos {@link Rol}.
     */
    List<Rol> findAll();

    /**
     * Busca un rol específico por su identificador único.
     * 
     * @param id ID del rol a buscar.
     * @return Un {@link Optional} que contiene el rol si existe, o vacío si no.
     */
    Optional<Rol> findById(Long id);

    /**
     * Crea un nuevo rol en el sistema.
     * 
     * @param rol Objeto {@link Rol} que contiene los datos del nuevo rol.
     * @return El rol creado con su ID asignado.
     */
    Rol create(Rol rol);

    /**
     * Actualiza los datos de un rol existente.
     * 
     * @param id  ID del rol a actualizar.
     * @param rol Objeto {@link Rol} con la información nueva.
     * @return El rol actualizado.
     */
    Rol update(Long id, Rol rol);

    /**
     * Elimina un rol del sistema según su ID.
     * 
     * @param id ID del rol que se desea eliminar.
     */
    void deleteById(Long id);
}
