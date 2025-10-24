package com.utp.libretago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.TokenDispositivo;

public interface TokenDispositivoRepository extends JpaRepository<TokenDispositivo, Long> {

    @Query("SELECT td FROM TokenDispositivo td WHERE td.usuarioPropietario.id IN (?1)")
    List<TokenDispositivo> listarTokensPorUsuariosId(List<Long> usuariosId);

    TokenDispositivo findByToken(String token);

    void deleteByToken(String token);

}