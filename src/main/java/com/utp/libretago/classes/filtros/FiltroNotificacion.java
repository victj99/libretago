package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.Notificacion;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltroNotificacion {

    String titulo;
    String estado;

    public FiltroNotificacion() {
    }

    public Specification<Notificacion> generarFiltroNotificacion() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (titulo != null && !titulo.isBlank()) {
                Expression<String> campo = root.get("titulo");
                predicates.add(builder.like(builder.lower(campo), "%" + titulo.toLowerCase() + "%"));
            }

            if (estado != null && !estado.isBlank()) {
                Expression<String> campo = root.get("estado");
                predicates.add(builder.equal(campo, estado));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
