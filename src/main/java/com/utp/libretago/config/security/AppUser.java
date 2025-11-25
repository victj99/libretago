package com.utp.libretago.config.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AppUser extends User {
    private Long id;
    private Long institucionEducativaId;

    public AppUser(Long id, Long institucionEducativaId, String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.id = id;
        this.institucionEducativaId = institucionEducativaId;
    }

    public Long getId() {
        return id;
    }

    public Long getInstitucionEducativaId() {
        return institucionEducativaId;
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     * <p>
     * Este método busca en las autoridades del usuario si existe un rol que coincida con el nombre proporcionado (agregando
     * el prefijo "ROLE_" automáticamente).
     * </p>
     *
     * @param role nombre del rol a verificar (sin el prefijo "ROLE_")
     * @return {@code true} si el usuario tiene el rol especificado, {@code false} en caso contrario
     */
    public boolean hasRole(String role) {
        return getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
