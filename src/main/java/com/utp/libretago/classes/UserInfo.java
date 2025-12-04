package com.utp.libretago.classes;

import java.util.Collection;
import java.util.Collections;

import jakarta.annotation.Nonnull;
import lombok.Getter;

/**
 * Representa la información básica de un usuario dentro del sistema Libretago.
 * <p>
 * Contiene el nombre del usuario, la colección de autoridades o roles asignados, y el nombre de la institución
 * educativa actualmente activa en su sesión.
 * </p>
 * <p>
 * Esta clase es inmutable tras su construcción: el nombre y la colección de autoridades no pueden cambiar — la
 * colección se envuelve con {@link Collections#unmodifiableCollection(Collection)}.
 * </p>
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-27
 */

@Getter
public class UserInfo {

    /** El nombre de usuario (login). No puede ser {@code null}. */
    @Nonnull
    private String name;

    /** El nombre completo del usuario (nombres y apellidos). */
    private String nombreCompleto;

    /** Colección inmutable de autoridades/roles del usuario. No puede ser {@code null}. */
    @Nonnull
    private Collection<String> authorities;

    /**
     * Nombre de la institución educativa actualmente activa en la sesión del usuario. Es {@code null} si el usuario no
     * tiene una institución educativa asociada o si su contexto institucional no ha sido establecido.
     */
    private String nombreInstitucion;

    public UserInfo(String name, String nombreCompleto, Collection<String> authorities, String nombreInstitucion) {
        // Asigna el nombre recibido al campo 'name'
        this.name = name;
        this.nombreCompleto = nombreCompleto;
        // Convierte la lista de autoridades en una colección inmutable
        this.authorities = Collections.unmodifiableCollection(authorities);
        this.nombreInstitucion = nombreInstitucion;
    }
}
