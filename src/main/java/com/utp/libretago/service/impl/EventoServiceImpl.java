package com.utp.libretago.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.utp.libretago.entity.Evento;
import com.utp.libretago.repository.EventoRepository;
import com.utp.libretago.service.EventoService;
import java.util.List;
import java.util.Optional;

@Service
public class EventoServiceImpl implements EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Override
    public List<Evento> findAll() {
        return eventoRepository.findAll();
    }

    @Override
    public Optional<Evento> findById(Long id) {
        return eventoRepository.findById(id);
    }

    @Override
    public Evento create(Evento evento) {
        return eventoRepository.save(evento);
    }

    @Override
    public Evento update(Long id, Evento evento) {
        Optional<Evento> existing = eventoRepository.findById(id);
        if (!existing.isPresent())
            return null;
        Evento e = existing.get();
        BeanUtils.copyProperties(evento, e, "id", "fechaCreacion");
        return eventoRepository.save(e);
    }

    @Override
    public void deleteById(Long id) {
        eventoRepository.deleteById(id);
    }
}
