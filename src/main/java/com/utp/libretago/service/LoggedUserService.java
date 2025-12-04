package com.utp.libretago.service;

import java.util.List;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.utp.libretago.classes.RolesEnum;
import com.utp.libretago.classes.UserInfo;
import com.utp.libretago.classes.dto.EventoStatsMultiLineDTO;
import com.utp.libretago.classes.dto.NotificationStatsMultiLineDTO;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.entity.InstitucionEducativa;
import com.utp.libretago.entity.TokenDispositivo;
import com.utp.libretago.entity.Usuario;
import com.utp.libretago.repository.InstitucionEducativaRepository;
import com.utp.libretago.repository.UsuarioInstitucionRepository;
import com.vaadin.hilla.BrowserCallable;
import com.vaadin.hilla.exception.EndpointException;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

/**
 * Servicio para obtener información del usuario autenticado y gestionar tokens de dispositivos. Permite obtener datos
 * del usuario actual y registrar o eliminar tokens de dispositivos asociados.
 * 
 * @author Roberto
 * @version 1.0
 * @since 2025-10-28
 */
@BrowserCallable
public class LoggedUserService {
    @Autowired
    private TokenDispositivoService tokenDispositivoService;
    @Autowired
    private InstitucionEducativaRepository institucionEducativaRepository;
    @Autowired
    private UsuarioInstitucionRepository usuarioInstitucionRepository;

    /**
     * Obtiene información básica del usuario autenticado, incluyendo nombre y roles.
     * 
     * @return {@link UserInfo} con el nombre de usuario y lista de roles.
     */
    @NonNull
    @PermitAll
    public UserInfo getUserInfo() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = (AppUser) auth.getPrincipal();

        final List<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String nombreInstitucion = null;
        Long currentInstitucionId = appUser.getInstitucionEducativaId();

        if (currentInstitucionId != null) {
            // Verificar si el usuario aún tiene acceso a la institución actual (Optimizado: solo IDs)
            List<Long> idsInstituciones = usuarioInstitucionRepository.findIdColegioByIdUsuario(appUser.getId());
            boolean accesoValido = idsInstituciones.contains(currentInstitucionId);

            if (accesoValido) {
                // Si es válido, obtener el nombre
                nombreInstitucion = institucionEducativaRepository.findById(currentInstitucionId)
                        .map(InstitucionEducativa::getNombre).orElse(null);
            } else {
                // Si NO es válido (fue eliminado), buscar un nuevo contexto
                Long nuevoId = idsInstituciones.isEmpty() ? null : idsInstituciones.get(0);

                // Actualizar la sesión con el nuevo ID (o null)
                actualizarContextoSesion(nuevoId);

                // Si se encontró una nueva institución, obtener su nombre
                if (nuevoId != null) {
                    nombreInstitucion = institucionEducativaRepository.findById(nuevoId).map(InstitucionEducativa::getNombre)
                            .orElse(null);
                }
            }
        } else {
            // Caso borde: Si no tiene ID en sesión pero debería tener (ej. se le asignó uno recién)
            // Podríamos intentar asignarle uno aquí también, pero por ahora mantenemos la lógica de solo validar lo existente.
        }

        return new UserInfo(auth.getName(), authorities, nombreInstitucion);
    }

    /**
     * Registra un token de dispositivo para el usuario autenticado. Si el token ya existe y pertenece a otro usuario, se
     * reasigna al usuario actual.
     * 
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
     * Elimina un token de dispositivo asociado al usuario autenticado. Solo se elimina si el token pertenece al usuario
     * actual.
     * 
     * @param token Token del dispositivo a eliminar.
     */
    @PermitAll
    public void eliminarDispositivo(String token) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TokenDispositivo existing = tokenDispositivoService.obtenerPorToken(token);
        if (existing == null)
            return;

        if (existing.getUsuarioPropietario() != null && existing.getUsuarioPropietario().getId().equals(appUser.getId())) {
            tokenDispositivoService.eliminarTokenPorToken(token);
        }
    }

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todas las instituciones educativas a las que el usuario autenticado tiene acceso.
     * <p>
     * Este método está disponible únicamente para usuarios con roles COLEGIO o PROFESOR.
     * </p>
     *
     * @return lista de {@link UsuarioInstitucionDTO} con las instituciones asociadas al usuario
     */
    @NonNull
    @RolesAllowed({ "COLEGIO", "PROFESOR" })
    public List<@NonNull UsuarioInstitucionDTO> listarInstituciones() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioService.listarInstitucionesPorUsuario(appUser.getId());
    }

    /**
     * Obtiene el identificador de la institución educativa actualmente activa en la sesión del usuario.
     *
     * @return ID de la institución activa, o {@code null} si no hay ninguna establecida
     */
    @PermitAll
    public Long obtenerIdInstitucionActual() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return appUser.getInstitucionEducativaId();
    }

    /**
     * Cambia la institución educativa activa en la sesión del usuario autenticado.
     * <p>
     * Verifica que el usuario tenga acceso a la institución solicitada antes de realizar el cambio.
     * Si el cambio es exitoso, actualiza el contexto de seguridad de la sesión.
     * </p>
     *
     * @param nuevaInstitucionId ID de la nueva institución a establecer como activa
     * @throws EndpointException si el usuario no tiene acceso a la institución solicitada
     */
    @RolesAllowed({ "COLEGIO", "PROFESOR" })
    public void cambiarInstitucion(Long nuevaInstitucionId) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Verificar que el usuario pertenece a la institución solicitada (Optimizado)
        List<Long> idsInstituciones = usuarioInstitucionRepository.findIdColegioByIdUsuario(appUser.getId());

        if (!idsInstituciones.contains(nuevaInstitucionId)) {
            throw new EndpointException("No tiene acceso a la institución seleccionada.");
        }

        // Actualizar el contexto de seguridad
        actualizarContextoSesion(nuevaInstitucionId);
    }

    /**
     * Actualiza el ID de la institución en el contexto de seguridad actual (sesión).
     * <p>
     * Crea un nuevo objeto {@link AppUser} con el nuevo ID de institución y actualiza
     * la autenticación en el {@link SecurityContextHolder}.
     * </p>
     *
     * @param institucionId nuevo ID de institución (puede ser {@code null})
     */
    private void actualizarContextoSesion(Long institucionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = (AppUser) auth.getPrincipal();

        AppUser nuevoAppUser = new AppUser(appUser.getId(), institucionId, appUser.getUsername(), "[PROTECTED]", appUser.isEnabled(),
                appUser.isAccountNonExpired(), appUser.isCredentialsNonExpired(), appUser.isAccountNonLocked(), appUser.getAuthorities());

        Authentication newAuth = new UsernamePasswordAuthenticationToken(nuevoAppUser, auth.getCredentials(), auth.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Obtiene estadísticas de notificaciones enviadas por día en los últimos 2 meses
     * para todas las instituciones educativas.
     * <p>
     * Este método está disponible únicamente para usuarios con rol ADMIN y es útil
     * para visualizar un gráfico Line Race comparando la actividad de todos los colegios.
     * </p>
     *
     * @return lista de {@link NotificationStatsMultiLineDTO} con las estadísticas por institución
     */
    @NonNull
    @RolesAllowed(RolesEnum.ADMIN)
    public List<@NonNull NotificationStatsMultiLineDTO> obtenerEstadisticasNotificacionesTodasInstituciones() {
        return notificacionService.obtenerEstadisticasNotificacionesTodasInstituciones();
    }

    @Autowired
    private EventoService eventoService;

    /**
     * Obtiene estadísticas de eventos enviados por día en los últimos 2 meses
     * para todas las instituciones educativas.
     * <p>
     * Este método está disponible únicamente para usuarios con rol ADMIN y es útil
     * para visualizar un gráfico Line Race comparando la actividad de eventos de todos los colegios.
     * </p>
     *
     * @return lista de {@link EventoStatsMultiLineDTO} con las estadísticas por institución
     */
    @NonNull
    @RolesAllowed(RolesEnum.ADMIN)
    public List<@NonNull EventoStatsMultiLineDTO> obtenerEstadisticasEventosTodasInstituciones() {
        return eventoService.obtenerEstadisticasEventosTodasInstituciones();
    }
}
