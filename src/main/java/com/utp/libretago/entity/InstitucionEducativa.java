package com.utp.libretago.entity;

import java.time.LocalDateTime;

import com.utp.libretago.utils.MensajesValidacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "institucion_educativa")
public class InstitucionEducativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    private String nombre;

    @Column(length = 255)
    @Size(max = 255, message = MensajesValidacion.LARGO_MAXIMO)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(name = "codigo_ugel", nullable = false, length = 50)
    @NotBlank(message = MensajesValidacion.CAMPO_REQUERIDO)
    @Size(max = 50, message = MensajesValidacion.LARGO_MAXIMO)
    private String codigoUgel;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    public InstitucionEducativa() {
    }

    public InstitucionEducativa(Long id) {
        this.id = id;
    }

}