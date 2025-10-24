package com.utp.libretago.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "rol")
public class Rol {

    public static final Long ID_COLEGIO = 2l;
    public static final Long ID_PROFESOR = 3l;
    public static final Long ID_APODERADO = 4l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String nombre;

    public Rol() {
    }

    public Rol(Long id) {
        this.id = id;
    }

}