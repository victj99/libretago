package com.utp.libretago.service;

import java.util.List;

import com.utp.libretago.entity.TokenDispositivo;

public interface TokenDispositivoService {

    TokenDispositivo registrarTokenDispositivo(TokenDispositivo token);

    List<String> listarTokensPorGrupoId(Long grupoId, Long institucionEducativaId);
    
    boolean existeTokenParaUsuario(Long usuarioId, String token);

    TokenDispositivo obtenerPorToken(String token);

    void eliminarTokenPorToken(String token);
}
