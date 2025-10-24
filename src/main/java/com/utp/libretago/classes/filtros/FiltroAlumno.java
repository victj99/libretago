package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.Alumno;

import jakarta.persistence.criteria.Predicate;

public record FiltroAlumno(String nombres, String apellidos, String codigoAlumno) {

    public Specification<Alumno> generarFiltroAlumno() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombres != null && !nombres.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("nombres")), "%" + nombres.toLowerCase() + "%"));
            }

            if (apellidos != null && !apellidos.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("apellidos")), "%" + apellidos.toLowerCase() + "%"));
            }

            if (codigoAlumno != null && !codigoAlumno.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("codigoAlumno")), "%" + codigoAlumno.toLowerCase() + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
};
