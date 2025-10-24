package com.utp.libretago.service;

import com.utp.libretago.entity.Evento;
import java.util.List;
import java.util.Optional;

public interface EventoService {
    List<Evento> findAll();

    Optional<Evento> findById(Long id);

    Evento create(Evento evento);

    Evento update(Long id, Evento evento);

    void deleteById(Long id);
}
