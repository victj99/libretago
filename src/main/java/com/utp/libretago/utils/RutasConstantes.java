package com.utp.libretago.utils;

/**
 * Clase que define rutas y constantes utilizadas en la aplicación.
 *
 * Actualmente contiene la ruta donde se guardarán los archivos Excel con errores.
 *
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
public class RutasConstantes {
    /**
     * Ruta temporal donde se almacenan los archivos Excel que contienen errores de validación. La ruta se construye usando
     * el directorio temporal del sistema y un subdirectorio específico para LibreTago. Ejemplos:
     * <ul>
     * <li>Linux: {@code /tmp/libretago/excels-error/}</li>
     * <li>Windows: {@code C:\\Users\\<Usuario>\\AppData\\Local\\Temp\\libretago\\excels-error\\}</li>
     * </ul>
     */
    public static final String RUTA_EXCELS_ERROR = System.getProperty("java.io.tmpdir") + "/libretago/excels-error/";
}
