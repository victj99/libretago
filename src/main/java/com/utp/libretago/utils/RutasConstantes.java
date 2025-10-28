package com.utp.libretago.utils;
/**
 * Clase que define rutas y constantes utilizadas en la aplicación.
 * 
 * <p>Actualmente contiene la ruta donde se guardarán los archivos Excel con errores.</p>
  * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
public class RutasConstantes {
    /**
         * Ruta temporal donde se almacenan los archivos Excel que contienen errores de validación.
         * <p>La ruta se construye usando el directorio temporal del sistema
         * y un subdirectorio específico para LibreTago.</p>
         * Ejemplo en Linux: /tmp/libretago/excels-error/
         * Ejemplo en Windows: C:\Users\<Usuario>\AppData\Local\Temp\libretago\excels-error\
     */
    public static final String RUTA_EXCELS_ERROR = System.getProperty("java.io.tmpdir") + "/libretago/excels-error/";
}
