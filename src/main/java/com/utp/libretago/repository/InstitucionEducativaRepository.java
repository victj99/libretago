package com.utp.libretago.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.InstitucionEducativa;
import java.util.List;


public interface InstitucionEducativaRepository extends JpaRepository<InstitucionEducativa, Long>, JpaSpecificationExecutor<InstitucionEducativa> {

    @Modifying
    @Query("UPDATE InstitucionEducativa i SET i.activo = false WHERE i.id = ?1")
    int inactivarInstitucionPorId(Long id);


    List<InstitucionEducativa> findByNombreLikeIgnoreCase(String nombre, Pageable pageable);
}
