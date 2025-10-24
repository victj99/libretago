package com.utp.libretago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    public Usuario findByNombreUsuario(String nombreUsuario);

    @Query("select u from Usuario u left join fetch u.roles where u.nombreUsuario = ?1")
    public Usuario findByNombreUsuarioFetchRoles(String nombreUsuario);

    @Modifying
    @Query("UPDATE Usuario i SET i.activo = false WHERE i.id = ?1")
    int inactivarUsuarioPorId(Long id);

    // Search users by role id through the many-to-many roles collection
    List<Usuario> findByRolesId(Long rolId);
}