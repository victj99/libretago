package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.Grupo;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa un filtro dinámico para la entidad {@link Grupo}.
 * <p>
 * Esta clase permite construir consultas flexibles mediante el uso del patrón <b>Specification</b> de Spring Data JPA.
 * Los filtros aplican solo si sus valores son diferentes de {@code null} o, en el caso de cadenas, no están en blanco.
 * </p>
 *
 * Ejemplo de uso:
 * 
 * <pre>{@code
 * FiltroGrupo filtro = new FiltroGrupo();
 * filtro.setNombre("Matemáticas");
 * filtro.setInstitucionEducativaId(10L);
 *
 * Specification<Grupo> spec = filtro.generarFiltroGrupo();
 * List<Grupo> resultados = grupoRepository.findAll(spec);
 * }</pre>
 *
 * <p>
 * En este ejemplo, la consulta resultante buscará todos los grupos cuyo nombre contenga "Matemáticas" (sin distinción
 * de mayúsculas/minúsculas) y que pertenezcan a la institución educativa con ID 10.
 * </p>
 *
 * @see org.springframework.data.jpa.domain.Specification
 * @see com.utp.libretago.entity.Grupo
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 28-10-2025
 */

@Getter
@Setter
public class FiltroGrupo {
    /** Nombre del grupo */
    String nombre;

    /** Identificador de la institución educativa asociada al grupo. */
    Long institucionEducativaId;

    /** Identificador del profesor asignado al grupo. */
    Long usuarioProfesorId;

    /** Estado del grupo (activo o inactivo). */
    Boolean activo;

    /** Constructor vacío requerido para frameworks como Spring. */
    public FiltroGrupo() {
    }

    /**
     * Genera una {@link Specification} dinámica para la entidad {@link Grupo}.
     * <p>
     * Cada campo de este filtro se evalúa de manera independiente: si el valor no es {@code null} (y no está en blanco en
     * el caso de cadenas), se añade una condición al conjunto de predicados.
     * </p>
     * <ul>
     * <li><b>nombre:</b> usa un filtro <i>LIKE</i> (búsqueda parcial, sin distinción de mayúsculas/minúsculas)</li>
     * <li><b>institucionEducativaId:</b> usa un filtro <i>EQUAL</i></li>
     * <li><b>usuarioProfesorId:</b> usa un filtro <i>EQUAL</i></li>
     * <li><b>activo:</b> usa un filtro <i>EQUAL</i></li>
     * </ul>
     * 
     * @return una especificación JPA que combina todos los filtros mediante una condición lógica AND.
     */
    public Specification<Grupo> generarFiltroGrupo() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Filtro por nombre (coincidencia parcial e insensible a mayúsculas/minúsculas)
            if (nombre != null && !nombre.isBlank()) {
                Expression<String> campo = root.get("nombre");
                predicates.add(builder.like(builder.lower(campo), "%" + nombre.toLowerCase() + "%"));
            }
            // Filtro por institución educativa
            if (institucionEducativaId != null) {
                Expression<Long> campo = root.get("institucionEducativaId");
                predicates.add(builder.equal(campo, institucionEducativaId));
            }
            // Filtro por profesor asignado
            if (usuarioProfesorId != null) {
                Expression<Long> campo = root.get("usuarioProfesorId");
                predicates.add(builder.equal(campo, usuarioProfesorId));
            }
            // Filtro por estado de actividad
            if (activo != null) {
                Expression<Boolean> campo = root.get("activo");
                predicates.add(builder.equal(campo, activo));
            }
            // Combina todos los predicados con un AND lógico
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
};
