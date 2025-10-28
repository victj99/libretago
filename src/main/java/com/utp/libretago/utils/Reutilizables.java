package com.utp.libretago.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
/**
 * Clase con métodos reutilizables para la aplicación.
 * <p>Contiene utilidades generales, como aplicar ordenamiento por defecto
 * a paginaciones de Spring Data.</p>
  * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
public class Reutilizables {
    /**
         * Aplica un ordenamiento descendente por defecto sobre un Pageable,
         * solo si no tiene ya un ordenamiento definido.
         * <p>Esto es útil cuando queremos asegurar que siempre haya un ordenamiento,
         * por ejemplo, por fecha de creación o id descendente.</p>
         * @param pageable el objeto Pageable recibido en la consulta
         * @param campo el nombre del campo por el cual ordenar por defecto
         * @return un Pageable con ordenamiento descendente por defecto si no estaba definido, 
         *         o el Pageable original si ya tenía ordenamiento
     */
    public static Pageable ordernarPorDefectoDesc(Pageable pageable, String campo) {
        // Verifica si el pageable ya tiene un ordenamiento definido
        if (pageable.getSort().isSorted())
            return pageable;

        // Si no tiene ordenamiento, crea uno por defecto descendente
        var defaultSort = Sort.by(Sort.Direction.DESC, campo);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
    }
}
