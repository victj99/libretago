package com.utp.libretago.service;

import com.utp.libretago.entity.Rol;
import java.util.List;
import java.util.Optional;

public interface RolService {
    List<Rol> findAll();

    Optional<Rol> findById(Long id);

    Rol create(Rol rol);

    Rol update(Long id, Rol rol);

    void deleteById(Long id);
}
