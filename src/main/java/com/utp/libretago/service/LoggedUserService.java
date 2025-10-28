package com.utp.libretago.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.utp.libretago.classes.UserInfo;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.entity.TokenDispositivo;
import com.utp.libretago.entity.Usuario;
import com.vaadin.hilla.BrowserCallable;

import jakarta.annotation.Nonnull;
import jakarta.annotation.security.PermitAll;
/**
     * Servicio para obtener información del usuario autenticado y gestionar tokens de dispositivos.
     * Permite obtener datos del usuario actual y registrar o eliminar tokens de dispositivos asociados.
     * @author Roberto
     * @version 1.0
     * @since 2025-10-28
 */
@BrowserCallable
public class LoggedUserService {
    @Autowired
    private TokenDispositivoService tokenDispositivoService;

    /**
         * Obtiene información básica del usuario autenticado, incluyendo nombre y roles.
         * @return {@link UserInfo} con el nombre de usuario y lista de roles.
     */
    @Nonnull
    @PermitAll
    public UserInfo getUserInfo() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        final List<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return new UserInfo(auth.getName(), authorities);
    }

    /**
         * Registra un token de dispositivo para el usuario autenticado.
         * Si el token ya existe y pertenece a otro usuario, se reasigna al usuario actual.
         * @param token Token del dispositivo a registrar.
     */
    @PermitAll
    public void registrarDispositivo(String token) {
        // Obtener el usuario autenticado actual
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TokenDispositivo tokenExistente = tokenDispositivoService.obtenerPorToken(token);

        if (tokenExistente != null) {
            if (tokenExistente.getUsuarioPropietario().getId().equals(appUser.getId())) {
                return;
            }

            tokenExistente.setUsuarioPropietario(new Usuario(appUser.getId()));
            tokenDispositivoService.registrarTokenDispositivo(tokenExistente);
            return;
        }

        TokenDispositivo tokenNuevo = new TokenDispositivo();
        tokenNuevo.setToken(token);
        tokenNuevo.setUsuarioPropietario(new Usuario(appUser.getId()));

        tokenDispositivoService.registrarTokenDispositivo(tokenNuevo);
    }
    
    /**
         * Elimina un token de dispositivo asociado al usuario autenticado.
         * Solo se elimina si el token pertenece al usuario actual.
         * @param token Token del dispositivo a eliminar.
     */
    @PermitAll
    public void eliminarDispositivo(String token) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TokenDispositivo existing = tokenDispositivoService.obtenerPorToken(token);
        if (existing == null) return;

        if (existing.getUsuarioPropietario() != null && existing.getUsuarioPropietario().getId().equals(appUser.getId())) {
            tokenDispositivoService.eliminarTokenPorToken(token);
        }
    }
}
