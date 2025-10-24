package com.utp.libretago.classes;

import java.util.Collection;
import java.util.Collections;

import jakarta.annotation.Nonnull;
import lombok.Getter;

@Getter
public class UserInfo {

    @Nonnull
    private String name;

    @Nonnull
    private Collection<String> authorities;

    public UserInfo(String name, Collection<String> authorities) {
        this.name = name;
        this.authorities = Collections.unmodifiableCollection(authorities);
    }
}
