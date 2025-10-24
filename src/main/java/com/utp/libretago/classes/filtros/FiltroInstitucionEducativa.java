package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.InstitucionEducativa;

import jakarta.persistence.criteria.Predicate;

public record FiltroInstitucionEducativa(String nombre, String codigoUgel, Boolean activo) {

    public Specification<InstitucionEducativa> generarFiltro() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }

            if (codigoUgel != null && !codigoUgel.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("codigoUgel")), "%" + codigoUgel.toLowerCase() + "%"));
            }

            if (activo != null) {
                predicates.add(builder.equal(root.get("activo"), activo));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
};
