package com.utp.libretago.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utp.libretago.entity.TokenDispositivo;
import com.utp.libretago.repository.AlumnoGrupoRepository;
import com.utp.libretago.repository.AlumnoRepository;
import com.utp.libretago.repository.TokenDispositivoRepository;
import com.utp.libretago.service.TokenDispositivoService;

/**
 * Implementación del servicio {@link TokenDispositivoService}.
 * Se encarga de la gestión de los tokens de dispositivos para notificaciones push,
 * incluyendo registro, eliminación, búsqueda y obtención de tokens por grupo.
 *
 * <p>Esta clase interactúa con los repositorios de {@link TokenDispositivo},
 * {@link AlumnoGrupoRepository} y {@link AlumnoRepository} para determinar los
 * dispositivos asociados a los usuarios y grupos.</p>
 *
 * @author Roberto
 * @version 1.0
 * @since 2025-10-28
 */
@Service
public class TokenDispositivoServiceImpl implements TokenDispositivoService {

    @Autowired
    private TokenDispositivoRepository tokenRepository;

    @Autowired
    private AlumnoGrupoRepository alumnoGrupoRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;
    /**
     * Registra o actualiza un token de dispositivo en la base de datos.
     * @param token Objeto {@link TokenDispositivo} que contiene el token y el usuario propietario.
     * @return Token de dispositivo registrado o actualizado.
     */
    @Override
    public TokenDispositivo registrarTokenDispositivo(TokenDispositivo token) {
        // Guardar o actualizar el token en la base de datos
        return tokenRepository.save(token);
    }
    
    /**
         * Busca un token de dispositivo específico.
         * @param token Cadena del token a buscar.
         * @return Objeto {@link TokenDispositivo} si existe, o {@code null} en caso contrario.
     */
    @Override
    public TokenDispositivo obtenerPorToken(String token) {
        // Buscar token en base de datos
        return tokenRepository.findByToken(token);
    }
    
    /**
     * Elimina un token de dispositivo por su valor.
     * @param token Cadena del token a eliminar.
     */
    @Override
    public void eliminarTokenPorToken(String token) {
         // Eliminar token por coincidencia exacta
        tokenRepository.deleteByToken(token);
    }
    /**
         * Obtiene una lista de tokens de dispositivos pertenecientes a los apoderados
         * de los alumnos que integran un grupo determinado.
         * @param grupoId               ID del grupo de alumnos.
         * @param institucionEducativaId ID de la institución educativa.
         * @return Lista de cadenas con los tokens FCM de los dispositivos.
     */
    @Override
    public List<String> listarTokensPorGrupoId(Long grupoId, Long institucionEducativaId) {
         // Obtener IDs de alumnos pertenecientes al grupo
        List<Long> alumnosId = alumnoGrupoRepository.listarIdsAlumnoByGrupoId(grupoId, institucionEducativaId);
        // Obtener los IDs de usuarios (apoderados) asociados a esos alumnos
        List<Long> usuariosId = alumnoRepository.findApoderadoIdsByAlumnoIds(alumnosId);
        // Consultar los tokens de esos usuarios
        var listaTokens = tokenRepository.listarTokensPorUsuariosId(usuariosId);
        // Devolver solo las cadenas de tokens
        return listaTokens.stream().map(item -> item.getToken()).toList();
    }
    
    /**
         * Verifica si un usuario ya tiene registrado un token específico.
         * @param usuarioId ID del usuario propietario.
         * @param token     Token de dispositivo a verificar.
         * @return {@code true} si el token existe para el usuario, {@code false} en caso contrario.
     */
    @Override
    public boolean existeTokenParaUsuario(Long usuarioId, String token) {
// Obtener todos los tokens registrados (podría optimizarse con un query directo)
        var tokensDelUsuario = tokenRepository.findAll();
        // Validar si existe un token con ese valor para el usuario
        return tokensDelUsuario.stream().anyMatch(item -> item.getUsuarioPropietario().getId().equals(usuarioId) && item.getToken().equals(token));
    }

}
