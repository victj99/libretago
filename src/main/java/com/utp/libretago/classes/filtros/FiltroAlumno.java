package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.Alumno;

import jakarta.persistence.criteria.Predicate;
/**
 * Clase de filtro para la entidad {@link Alumno}.
 * <p>
 * Esta clase implementa un registro (record) que encapsula los posibles
 * criterios de búsqueda que se pueden aplicar sobre la entidad Alumno.
 * Permite generar dinámicamente una {@link Specification} que será utilizada
 * por Spring Data JPA para realizar consultas personalizadas en función
 * de los parámetros proporcionados.
 * </p>
 *
 * <p><b>Uso principal:</b></p>
 * <ul>
 *   <li>Filtrar alumnos por nombres, apellidos o código de alumno.</li>
 *   <li>Permitir consultas flexibles y combinadas en los repositorios JPA.</li>
 * </ul>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>
 * FiltroAlumno filtro = new FiltroAlumno("Juan", "Pérez", null);
 * List&lt;Alumno&gt; resultados = alumnoRepository.findAll(filtro.generarFiltroAlumno());
 * </pre>
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 28-10-2025
 */
public record FiltroAlumno(String nombres, String apellidos, String codigoAlumno) {
  /**
     * Genera una especificación de búsqueda dinámica para la entidad {@link Alumno}.
     * <p>
     * Construye una lista de {@link Predicate} en función de los campos
     * proporcionados en el filtro. Cada campo se evalúa de manera independiente:
     * si el valor no es nulo ni está en blanco, se añade una condición
     * <i>LIKE</i> (con coincidencia parcial, insensible a mayúsculas/minúsculas)
     * al conjunto de predicados.
     * </p>
     *
     * @return una instancia de {@link Specification} que puede ser utilizada
     *         por un repositorio JPA para realizar consultas filtradas.
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
            // Combina todos los predicados con AND lógico
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
};
