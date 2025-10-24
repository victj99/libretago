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
}
