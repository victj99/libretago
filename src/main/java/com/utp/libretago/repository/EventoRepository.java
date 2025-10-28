package com.utp.libretago.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.libretago.entity.Evento;

/**
 * Repositorio para la gestión de la entidad {@link Evento}.
 * <p>
 * Extiende {@link JpaRepository} para proporcionar las operaciones básicas de
 * persistencia (crear, leer, actualizar, eliminar), así como paginación,
 * ordenamiento y búsquedas dinámicas.
 * </p>
 *
 * <p>
 * Este repositorio no requiere métodos personalizados por el momento, pero puede
 * ser ampliado para incluir consultas específicas relacionadas con eventos,
 * evaluaciones o asignaciones a grupos.
 * </p>
 *
 * @see Evento
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface EventoRepository extends JpaRepository<Evento, Long> {
}
