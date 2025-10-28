package com.utp.libretago.service;

import java.util.List;
import com.utp.libretago.entity.TokenDispositivo;

/**
 * Servicio para la gestión de los tokens de dispositivos utilizados en el sistema.
 * 
 * <p>Esta interfaz define los métodos necesarios para registrar, obtener, listar y eliminar
 * tokens asociados a los dispositivos de los usuarios. Los tokens suelen utilizarse para
 * notificaciones push o autenticación en dispositivos móviles.</p>
 * 
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */

public interface TokenDispositivoService {
    /**
         * Registra un nuevo token de dispositivo en el sistema.
         * @param token el objeto {@link TokenDispositivo} que contiene la información del token a registrar.
         * @return el {@link TokenDispositivo} registrado, incluyendo su identificador generado.
     */
    TokenDispositivo registrarTokenDispositivo(TokenDispositivo token);

    /**
         * Obtiene una lista de tokens asociados a los usuarios de un grupo específico dentro de una institución educativa.
         * @param grupoId el identificador del grupo.
         * @param institucionEducativaId el identificador de la institución educativa.
         * @return una lista de cadenas que representan los tokens de los dispositivos asociados al grupo.
     */
    List<String> listarTokensPorGrupoId(Long grupoId, Long institucionEducativaId);

    /**
         * Verifica si un usuario ya tiene registrado un token específico.
         * @param usuarioId el identificador del usuario.
         * @param token el valor del token a verificar.
         * @return {@code true} si el token ya existe para el usuario, {@code false} en caso contrario.
     */
    boolean existeTokenParaUsuario(Long usuarioId, String token);
    
    /**
         * Obtiene un {@link TokenDispositivo} según su valor de token.
         * @param token el valor del token a buscar.
         * @return el {@link TokenDispositivo} correspondiente, o {@code null} si no existe.
     */
    TokenDispositivo obtenerPorToken(String token);
    
    /**
         * Elimina un token de dispositivo a partir de su valor.
         * @param token el valor del token a eliminar.
     */
    void eliminarTokenPorToken(String token);
}
