package com.utp.libretago.utils;

/**
 * Clase que contiene constantes de mensajes de validación reutilizables.
 * 
 * <p>Estos mensajes pueden ser usados en la validación de formularios,
 * entidades o cualquier entrada de datos para mantener consistencia.</p>
  * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
public class MensajesValidacion {
    /** Mensaje para campos obligatorios que no deben estar vacíos */
    public static final String CAMPO_REQUERIDO = "Este campo es requerido";
    /** Mensaje para campos que deben contener al menos un elemento (listas, colecciones) */
    public static final String NO_VACIO = "Este campo debe tener al menos 1 elemento";
    
    /** 
         * Mensaje para campos que exceden la longitud máxima permitida.
         * <p>Reemplazar {max} con el valor máximo permitido.</p>
     */
    public static final String LARGO_MAXIMO = "Este campo es debe tener un máximo de {max} caracteres";
    
    /** 
         * Mensaje para campos que no alcanzan la longitud mínima requerida.
         * <p>Reemplazar {min} con el valor mínimo requerido.</p>
     */
    public static final String LARGO_MINIMO = "Este campo es debe tener un mínimo de {min} caracteres";
}
