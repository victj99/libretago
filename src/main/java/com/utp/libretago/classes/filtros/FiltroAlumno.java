package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.Alumno;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase de filtro para la entidad {@link Alumno}.
 *
 * Esta clase encapsula los posibles criterios de búsqueda que se pueden aplicar sobre la entidad Alumno. Permite
 * generar dinámicamente una {@link Specification} que será utilizada por Spring Data JPA para realizar consultas
 * personalizadas en función de los parámetros proporcionados.
 *
 * <b>Uso principal:</b>
 * <ul>
 * <li>Filtrar alumnos por nombres, apellidos, código de alumno o institución educativa.</li>
 * <li>Permitir consultas flexibles y combinadas en los repositorios JPA.</li>
 * </ul>
 *
 * <b>Ejemplo de uso:</b>
 * 
 * <pre>
 * FiltroAlumno filtro = new FiltroAlumno("Juan", "Pérez", null, 1L);
 * List&lt;Alumno&gt; resultados = alumnoRepository.findAll(filtro.generarFiltroAlumno());
 * </pre>
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 28-10-2025
 */

@Getter
@Setter
@AllArgsConstructor
public class FiltroAlumno {
    /** Nombres o parte de los nombres del alumno para búsqueda parcial. */
    private String nombres;

    /** Apellidos o parte de los apellidos del alumno para búsqueda parcial. */
    private String apellidos;

    /** Código del alumno para búsqueda parcial. */
    private String codigoAlumno;

    /**
     * ID de la institución educativa a la que pertenece el alumno. Se utiliza para filtrar alumnos por institución de forma
     * exacta.
     */
    private Long institucionEducativaId;

    public FiltroAlumno() {
    }

    /**
     * Genera una especificación de búsqueda dinámica para la entidad {@link Alumno}.
     *
     * Construye una lista de {@link Predicate} en función de los campos proporcionados en el filtro:
     * <ul>
     * <li><b>nombres, apellidos, codigoAlumno:</b> filtro LIKE (coincidencia parcial, insensible a
     * mayúsculas/minúsculas)</li>
     * <li><b>institucionEducativaId:</b> filtro EQUAL (coincidencia exacta)</li>
     * </ul>
     * Solo los campos no nulos y no vacíos se incluyen en la consulta.
     *
     * @return una instancia de {@link Specification} que puede ser utilizada por un repositorio JPA para realizar consultas
     *         filtradas.
     */
    public Specification<Alumno> generarFiltroAlumno() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Filtro por nombres (coincidencia parcial, sin distinción de mayúsculas/minúsculas)
            if (nombres != null && !nombres.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("nombres")), "%" + nombres.toLowerCase() + "%"));
            }
            // Filtro por apellidos (coincidencia parcial, sin distinción de mayúsculas/minúsculas)
            if (apellidos != null && !apellidos.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("apellidos")), "%" + apellidos.toLowerCase() + "%"));
            }
            // Filtro por código de alumno (coincidencia parcial, sin distinción de mayúsculas/minúsculas)
            if (codigoAlumno != null && !codigoAlumno.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("codigoAlumno")), "%" + codigoAlumno.toLowerCase() + "%"));
            }

            // Filtro por institución educativa (igualdad)
            if (institucionEducativaId != null) {
                predicates.add(builder.equal(root.get("institucionEducativaId"), institucionEducativaId));
            }

            // Combina todos los predicados con AND lógico
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
};
