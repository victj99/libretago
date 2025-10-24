package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.UsuarioInstitucion;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltroUsuario {

    String nombreUsuario;
    String nombreCompleto;
    Long rolId;
    Long institucionEducativaId;

    public FiltroUsuario() {
    }

    public Specification<UsuarioInstitucion> generarFiltroUsuarioInstitucion() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            var alumnoRoot = root.join("usuarioColegio");

            if (nombreUsuario != null && !nombreUsuario.isBlank()) {
                Expression<String> campo = alumnoRoot.get("nombreUsuario");
                predicates.add(builder.like(builder.lower(campo), "%" + nombreUsuario.toLowerCase() + "%"));
            }

            if (nombreCompleto != null && !nombreCompleto.isBlank()) {
                Expression<String> campo = alumnoRoot.get("nombreCompleto");
                predicates.add(builder.like(builder.lower(campo), "%" + nombreCompleto.toLowerCase() + "%"));
            }

            if (institucionEducativaId != null) {
                Expression<Long> campo = alumnoRoot.get("institucionEducativaId");
                predicates.add(builder.equal(campo, institucionEducativaId));

            }

            if (rolId != null) {
                var rolesJoin = alumnoRoot.join("roles");
                predicates.add(builder.equal(rolesJoin.get("id"), rolId));
                query.distinct(true);
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
};
