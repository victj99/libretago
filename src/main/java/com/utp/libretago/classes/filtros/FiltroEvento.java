package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.Evento;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa un filtro dinámico para la entidad {@link Evento}.
 *
 * @author Victor Tinoco
 * @version 1.0
 * @since 2025-11
 */
@Getter
@Setter
public class FiltroEvento {

    String titulo;
    Long institucionEducativaId;
    String estado;

    public FiltroEvento() {
    }

    /**
     * Genera una {@link Specification} dinámica para filtrar eventos en la base de datos.
     * <p>
     * Construye una consulta JPA dinámica basada en los criterios de búsqueda establecidos:
     * </p>
     * <ul>
     * <li><b>titulo:</b> filtro LIKE (búsqueda parcial, insensible a mayúsculas/minúsculas)</li>
     * <li><b>institucionEducativaId:</b> filtro por institución a través de los grupos asociados</li>
     * <li><b>estado:</b> filtro EQUAL (exacto)</li>
     * </ul>
     * <p>
     * Solo los campos no nulos y no vacíos se incluyen en la consulta.
     * </p>
     *
     * @return una {@link Specification} para uso con repositorios JPA
     */
    public Specification<Evento> generarFiltroEvento() {
        return (root, query, builder) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por título
            if (titulo != null && !titulo.isBlank()) {
                Expression<String> campo = root.get("titulo");
                predicates.add(builder.like(builder.lower(campo), "%" + titulo.toLowerCase() + "%"));
            }

            // Filtro por institución educativa
            if (institucionEducativaId != null) {
                var gruposJoin = root.join("eventoGrupos").join("grupo");
                Expression<Long> campo = gruposJoin.get("institucionEducativaId");
                predicates.add(builder.equal(campo, institucionEducativaId));
            }

            // Filtro por estado
            if (estado != null && !estado.isBlank()) {
                Expression<String> campo = root.get("estado");
                predicates.add(builder.equal(campo, estado));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
