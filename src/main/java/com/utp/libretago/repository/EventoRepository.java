package com.utp.libretago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.libretago.entity.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}