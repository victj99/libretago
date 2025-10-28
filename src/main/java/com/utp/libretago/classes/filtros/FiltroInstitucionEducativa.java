package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.InstitucionEducativa;

import jakarta.persistence.criteria.Predicate;
/**
     * Representa un filtro dinámico para la entidad {@link InstitucionEducativa}.
     * <p>
     * Permite construir consultas dinámicas usando el patrón <b>Specification</b>
     * de Spring Data JPA. Solo se aplican los filtros que tengan valores
     * no nulos y, en el caso de cadenas, no estén vacíos.
     * </p>
     *
     * <h3>Ejemplo de uso:</h3>
     * <pre>{@code
     * FiltroInstitucionEducativa filtro = new FiltroInstitucionEducativa("Colegio ABC", "UGEL123", true);
     * Specification<InstitucionEducativa> spec = filtro.generarFiltro();
     * List<InstitucionEducativa> resultados = institucionEducativaRepository.findAll(spec);
     * }</pre>
     *
     * <p>En este ejemplo, la consulta buscará instituciones cuyo nombre contenga "Colegio Salesiano Don Bosco",
     * cuyo código UGEL sea "UGEL PIURA" y que estén activas.</p>
     *
     * @see org.springframework.data.jpa.domain.Specification
     * @see com.utp.libretago.entity.InstitucionEducativa
     * @author Roberto Anton
     * @version 1.0
     * @since 28-10-2025
     * @param nombre     Nombre de la institución (búsqueda parcial, insensible a mayúsculas/minúsculas).
     * @param codigoUgel Código UGEL de la institución (búsqueda parcial, insensible a mayúsculas/minúsculas).
     * @param activo     Estado de actividad de la institución.
 */
public record FiltroInstitucionEducativa(String nombre, String codigoUgel, Boolean activo) {
    /**
         * Genera una {@link Specification} dinámica para la entidad {@link InstitucionEducativa}.
         * <p>
         * Cada atributo se evalúa de manera independiente:
         * <ul>
         *   <li><b>nombre:</b> se aplica un filtro LIKE (coincidencia parcial, insensible a mayúsculas/minúsculas)</li>
         *   <li><b>codigoUgel:</b> se aplica un filtro LIKE (coincidencia parcial, insensible a mayúsculas/minúsculas)</li>
         *   <li><b>activo:</b> se aplica un filtro EQUAL</li>
         * </ul>
         * Solo los campos no nulos y no vacíos se incluyen en la consulta.
         * </p>
         *
         * @return una {@link Specification} que puede ser utilizada en un repositorio JPA
         *         para realizar consultas filtradas.
     */
    public Specification<InstitucionEducativa> generarFiltro() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filtro por nombre 
            if (nombre != null && !nombre.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }
            
            // Filtro por código UGEL 
            if (codigoUgel != null && !codigoUgel.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("codigoUgel")), "%" + codigoUgel.toLowerCase() + "%"));
            }
            
            // Filtro por estado de actividad
            if (activo != null) {
                predicates.add(builder.equal(root.get("activo"), activo));
            }
            
            // Combina todos los predicados con AND lógico
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
};
