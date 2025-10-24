package com.utp.libretago.config.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.utp.libretago.entity.Rol;
import com.utp.libretago.service.UsuarioService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioService.obtenerPorNombreUsuarioConRoles(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Error al iniciar, el usuario no existe");
        }

        Long institucionEducativaId = null;
        List<GrantedAuthority> authorities = usuario.getRoles().stream().map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .collect(Collectors.toList());

        boolean perteneceInstitucion = usuario.getRoles().stream().anyMatch(r -> Arrays.asList(Rol.ID_COLEGIO, Rol.ID_PROFESOR).contains(r.getId()));

        if (perteneceInstitucion) {
            institucionEducativaId = usuarioService.obtenerIdColegioPorIdUsuario(usuario.getId());
        }

        return new AppUser(usuario.getId(), institucionEducativaId, usuario.getNombreUsuario(), usuario.getContrasenia(), usuario.getActivo(), true,
                true, true, authorities);
    }

}
