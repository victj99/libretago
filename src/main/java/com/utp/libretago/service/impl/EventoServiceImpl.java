package com.utp.libretago.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.utp.libretago.entity.Evento;
import com.utp.libretago.repository.EventoRepository;
import com.utp.libretago.service.EventoService;
import java.util.List;
import java.util.Optional;
/**
 * Implementación del servicio {@link EventoService} para la gestión de eventos.
 * Permite listar, crear, actualizar y eliminar eventos en la base de datos.
 * 
 * @author Roberto
 * @version 1.0
 * @since 2025-10-28
 */
@Service
public class EventoServiceImpl implements EventoService {

    @Autowired
    private EventoRepository eventoRepository;
      /**
         * Obtiene todos los eventos registrados.
         * @return Lista de eventos {@link Evento}.
     */
    @Override
    public List<Evento> findAll() {
        return eventoRepository.findAll();
    }

    /**
         * Obtiene un evento por su ID.
         * @param id ID del evento.
         * @return Optional con el {@link Evento} si existe, vacío si no.
     */
    @Override
    public Optional<Evento> findById(Long id) {
        return eventoRepository.findById(id);
    }
    /**
         * Crea un nuevo evento en la base de datos.
         * @param evento Datos del evento a crear.
         * @return Evento creado con ID generado.
     */
    @Override
    public Evento create(Evento evento) {
        return eventoRepository.save(evento);
    }

    /**
         * Actualiza un evento existente según su ID.
         * Los campos "id" y "fechaCreacion" no se actualizan.
         * @param id ID del evento a actualizar.
         * @param evento Datos actualizados del evento.
         * @return Evento actualizado, o null si no se encontró el evento.
     */
    @Override
    public Evento update(Long id, Evento evento) {
        Optional<Evento> existing = eventoRepository.findById(id);
        if (!existing.isPresent())
            return null;
        Evento e = existing.get();
        BeanUtils.copyProperties(evento, e, "id", "fechaCreacion");
        return eventoRepository.save(e);
    }
    /**
         * Elimina un evento según su ID.
         * @param id ID del evento a eliminar.
     */
    @Override
    public void deleteById(Long id) {
        eventoRepository.deleteById(id);
    }
}
