package com.utp.libretago.service;

import com.utp.libretago.entity.Evento;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define los métodos del servicio para la gestión de eventos dentro del sistema.
 *
 * Permite realizar operaciones CRUD (crear, leer, actualizar, eliminar) sobre la entidad {@link Evento}.
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
public interface EventoService {

    /**
     * Obtiene la lista completa de eventos registrados en el sistema.
     * 
     * @return lista de objetos {@link Evento}
     */
    List<Evento> findAll();

    /**
     * Busca un evento por su identificador único.
     * 
     * @param id identificador del evento
     * @return un {@link Optional} que contiene el evento si existe, o vacío si no se encuentra
     */
    Optional<Evento> findById(Long id);

    /**
     * Crea un nuevo evento en el sistema.
     * 
     * @param evento objeto {@link Evento} con los datos del evento a registrar
     * @return el evento creado con su identificador asignado
     */
    Evento create(Evento evento);

    /**
     * Actualiza los datos de un evento existente.
     * 
     * @param id     identificador del evento a actualizar
     * @param evento objeto {@link Evento} con la información actualizada
     * @return el evento actualizado
     */
    Evento update(Long id, Evento evento);

    /**
     * Elimina un evento por su identificador.
     * 
     * @param id identificador del evento a eliminar
     */
    void deleteById(Long id);
}
