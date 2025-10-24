package com.utp.libretago.entity;

import java.time.LocalDateTime;

import com.utp.libretago.classes.dto.Alumno2DTO;
import com.utp.libretago.classes.dto.AlumnoDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "alumno")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombres;

    @Column(nullable = false, length = 255)
    private String apellidos;

    @Column(name = "codigo_alumno", nullable = false, length = 50)
    private String codigoAlumno;

    @Column(name = "institucion_educativa_id", nullable = false)
    private Long institucionEducativaId;

    @Column(name = "usuario_apoderado_id")
    private Long usuarioApoderadoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_apoderado_id", insertable = false, updatable = false)
    private Usuario usuarioApoderado;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    private void prePersist() {
        activo = true;
        fechaCreacion = LocalDateTime.now();
    }

    public AlumnoDTO obtenerAlumnoDTO() {
        String email = null;
        String telefono = null;
        String dni = null;
        String nombreApoderado = null;

        if (usuarioApoderado != null) {
            email = usuarioApoderado.getEmail();
            telefono = usuarioApoderado.getTelefono();
            dni = usuarioApoderado.getNombreUsuario();
            nombreApoderado = usuarioApoderado.getNombreCompleto();
        }

        return new AlumnoDTO(id, nombres, apellidos, codigoAlumno, email, telefono, dni, nombreApoderado, activo);
    }

    public Alumno2DTO obtenerAlumno2DTO() {
        return new Alumno2DTO(id, nombres, apellidos, codigoAlumno);
    }
}