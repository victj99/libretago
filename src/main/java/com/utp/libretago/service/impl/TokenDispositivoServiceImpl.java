package com.utp.libretago.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utp.libretago.entity.TokenDispositivo;
import com.utp.libretago.repository.AlumnoGrupoRepository;
import com.utp.libretago.repository.AlumnoRepository;
import com.utp.libretago.repository.TokenDispositivoRepository;
import com.utp.libretago.service.TokenDispositivoService;

@Service
public class TokenDispositivoServiceImpl implements TokenDispositivoService {

    @Autowired
    private TokenDispositivoRepository tokenRepository;

    @Autowired
    private AlumnoGrupoRepository alumnoGrupoRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Override
    public TokenDispositivo registrarTokenDispositivo(TokenDispositivo token) {
        return tokenRepository.save(token);
    }

    @Override
    public TokenDispositivo obtenerPorToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void eliminarTokenPorToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    @Override
    public List<String> listarTokensPorGrupoId(Long grupoId, Long institucionEducativaId) {
        List<Long> alumnosId = alumnoGrupoRepository.listarIdsAlumnoByGrupoId(grupoId, institucionEducativaId);

        List<Long> usuariosId = alumnoRepository.findApoderadoIdsByAlumnoIds(alumnosId);

        var listaTokens = tokenRepository.listarTokensPorUsuariosId(usuariosId);

        return listaTokens.stream().map(item -> item.getToken()).toList();
    }

    @Override
    public boolean existeTokenParaUsuario(Long usuarioId, String token) {
        // Buscar si existe un token igual para este usuario
        var tokensDelUsuario = tokenRepository.findAll();
        return tokensDelUsuario.stream().anyMatch(item -> item.getUsuarioPropietario().getId().equals(usuarioId) && item.getToken().equals(token));
    }

}
