package com.utp.libretago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.Grupo;

public interface GrupoRepository extends JpaRepository<Grupo, Long>, JpaSpecificationExecutor<Grupo> {

    @Modifying
    @Query("UPDATE Grupo i SET i.activo = false WHERE i.id = ?1")
    int inactivarGrupoPorId(Long id);
}