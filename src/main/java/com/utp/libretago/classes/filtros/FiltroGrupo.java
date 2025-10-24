package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.Grupo;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltroGrupo {

    String nombre;
    Long institucionEducativaId;
    Long usuarioProfesorId;
    Boolean activo;

    public FiltroGrupo() {
    }

    public Specification<Grupo> generarFiltroGrupo() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.isBlank()) {
                Expression<String> campo = root.get("nombre");
                predicates.add(builder.like(builder.lower(campo), "%" + nombre.toLowerCase() + "%"));
            }

            if (institucionEducativaId != null) {
                Expression<Long> campo = root.get("institucionEducativaId");
                predicates.add(builder.equal(campo, institucionEducativaId));
            }

            if (usuarioProfesorId != null) {
                Expression<Long> campo = root.get("usuarioProfesorId");
                predicates.add(builder.equal(campo, usuarioProfesorId));
            }

            if (activo != null) {
                Expression<Boolean> campo = root.get("activo");
                predicates.add(builder.equal(campo, activo));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
};
