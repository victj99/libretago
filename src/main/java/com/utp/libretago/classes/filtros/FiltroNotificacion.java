package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.Notificacion;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa un filtro dinámico para la entidad {@link Notificacion}.
 *
 * Permite construir consultas flexibles utilizando el patrón <b>Specification</b> de Spring Data JPA. Solo se aplican
 * los filtros que tengan valores no nulos y, en el caso de cadenas, no estén en blanco.
 *
 *
 * Ejemplo de uso:
 * 
 * <pre>{@code
 * FiltroNotificacion filtro = new FiltroNotificacion();
 * filtro.setTitulo("Reunión");
 * filtro.setEstado("PENDIENTE");
 *
 * Specification<Notificacion> spec = filtro.generarFiltroNotificacion();
 * List<Notificacion> resultados = notificacionRepository.findAll(spec);
 * }</pre>
 *
 * La consulta buscará notificaciones cuyo título contenga "Reunión" y cuyo estado sea "PENDIENTE".
 *
 * @see org.springframework.data.jpa.domain.Specification
 * @see com.utp.libretago.entity.Notificacion
 * @author Roberto Anton
 * @version 1.0
 * @since 28-10-2025
 */
@Getter
@Setter
public class FiltroNotificacion {
    /** Título de la notificación. */
    String titulo;

    /** Estado de la notificación. */
    String estado;

    /** Constructor vacío requerido para frameworks como Spring. */
    public FiltroNotificacion() {
    }

    /**
     * Genera una {@link Specification} dinámica para la entidad {@link Notificacion}.
     *
     * Cada campo se evalúa de manera independiente:
     * <ul>
     * <li><b>titulo:</b> se aplica un filtro LIKE (búsqueda parcial, insensible a mayúsculas/minúsculas)</li>
     * <li><b>estado:</b> se aplica un filtro EQUAL (exacto)</li>
     * </ul>
     * Solo los campos no nulos y no vacíos se incluyen en la consulta.
     *
     * @return una {@link Specification} que puede ser utilizada en un repositorio JPA para realizar consultas filtradas.
     */
    public Specification<Notificacion> generarFiltroNotificacion() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por título
            if (titulo != null && !titulo.isBlank()) {
                Expression<String> campo = root.get("titulo");
                predicates.add(builder.like(builder.lower(campo), "%" + titulo.toLowerCase() + "%"));
            }

            // Filtro por estado
            if (estado != null && !estado.isBlank()) {
                Expression<String> campo = root.get("estado");
                predicates.add(builder.equal(campo, estado));
            }

            // Combina todos los predicados con AND lógico
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
