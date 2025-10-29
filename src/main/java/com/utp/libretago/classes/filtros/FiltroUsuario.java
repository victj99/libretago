package com.utp.libretago.classes.filtros;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.utp.libretago.entity.UsuarioInstitucion;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;


/**
 * Representa un filtro dinámico para la entidad {@link UsuarioInstitucion}.
 * <p>
 * Permite construir consultas flexibles usando el patrón <b>Specification</b>
 * de Spring Data JPA. Los filtros se aplican solo si sus valores son distintos
 * de {@code null} y, en el caso de cadenas, no están vacíos.
 * </p>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>{@code
 * FiltroUsuario filtro = new FiltroUsuario();
 * filtro.setNombreUsuario("juan");
 * filtro.setRolId(2L);
 *
 * Specification<UsuarioInstitucion> spec = filtro.generarFiltroUsuarioInstitucion();
 * List<UsuarioInstitucion> resultados = usuarioInstitucionRepository.findAll(spec);
 * }</pre>
 *
 * <p>La consulta buscará usuarios cuyo nombre de usuario contenga "juan" 
 * y que tengan el rol con ID 2.</p>
 *
 * @see org.springframework.data.jpa.domain.Specification
 * @see com.utp.libretago.entity.UsuarioInstitucion
 * @author Roberto Anton
 * @version 1.0
 * @since 28-10-2025
 */
@Getter
@Setter
public class FiltroUsuario {

    /** Nombre de usuario. */
    String nombreUsuario;
    
    /** Nombre completo del usuario. */
    String nombreCompleto;
    
    /** ID del rol asociado al usuario. */
    Long rolId;
    
    /** ID de la institución educativa asociada al usuario. */
    Long institucionEducativaId;

    public FiltroUsuario() {
    }
    
  /**
         * Genera una {@link Specification} dinámica para la entidad {@link UsuarioInstitucion}.
         * <p>
         * Los filtros se aplican de forma independiente:
         * <ul>
         *   <li><b>nombreUsuario:</b> filtro LIKE insensible a mayúsculas/minúsculas</li>
         *   <li><b>nombreCompleto:</b> filtro LIKE insensible a mayúsculas/minúsculas</li>
         *   <li><b>institucionEducativaId:</b> filtro exacto con EQUAL</li>
         *   <li><b>rolId:</b> filtro exacto sobre la relación roles y se asegura que la consulta sea distinta</li>
         * </ul>
         * Solo se incluyen los filtros que no sean {@code null} y, en el caso de cadenas, no estén vacías.
         * </p>
         *
         * @return una {@link Specification} que puede ser utilizada en un repositorio JPA
         *         para realizar consultas filtradas sobre {@link UsuarioInstitucion}.
     */
    public Specification<UsuarioInstitucion> generarFiltroUsuarioInstitucion() {
        
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Se hace join con la entidad "usuarioColegio"
            var alumnoRoot = root.join("usuarioColegio");
            
            // Filtro por nombre de usuario
            if (nombreUsuario != null && !nombreUsuario.isBlank()) {
                Expression<String> campo = alumnoRoot.get("nombreUsuario");
                predicates.add(builder.like(builder.lower(campo), "%" + nombreUsuario.toLowerCase() + "%"));
            }
            
            // Filtro por nombre completo
            if (nombreCompleto != null && !nombreCompleto.isBlank()) {
                Expression<String> campo = alumnoRoot.get("nombreCompleto");
                predicates.add(builder.like(builder.lower(campo), "%" + nombreCompleto.toLowerCase() + "%"));
            }
            
            // Filtro por institución educativa
            if (institucionEducativaId != null) {
                Expression<Long> campo = alumnoRoot.get("institucionEducativaId");
                predicates.add(builder.equal(campo, institucionEducativaId));

            }
            // Filtro por rol
            if (rolId != null) {
                var rolesJoin = alumnoRoot.join("roles");
                predicates.add(builder.equal(rolesJoin.get("id"), rolId));
                query.distinct(true);
            }
                
            // Combina todos los predicados con AND lógico
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
