package com.utp.libretago.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Clase utilitaria para operaciones relacionadas con archivos Excel.
 * Proporciona métodos para leer valores de celdas, validar datos y generar archivos de errores.
 */
public class ExcelUtils {
    

    /**
     * Obtiene el valor de una celda como texto a partir de una fila y la posición de la celda.
     * 
     * @param fila la fila de la hoja de Excel
     * @param pos el índice de la celda en la fila
     * @return el valor de la celda como String o null si no hay valor
     */
    public static String getValorCeldaComoTexto(Row fila, int pos) {
        // Obtiene la celda en la posición indicada y delega a getValorCeldaComoTexto(Cell)
        return getValorCeldaComoTexto(fila.getCell(pos));
    }

     /**
     * Obtiene el valor de una celda como texto. 
     * @param celda la celda a leer
     * @return el valor de la celda como String, o null si es nula o tipo no soportado
     */   
    public static String getValorCeldaComoTexto(Cell celda) {
        if (celda == null) return null;
        switch (celda.getCellType()) {
            case STRING:
                return celda.getStringCellValue();
            case NUMERIC:
                // Convierte el valor numérico a long para evitar decimales innecesarios
                return String.valueOf((long) celda.getNumericCellValue());
            default:
                return null;
        }
    }
    
     /**
         * Genera un archivo Excel que incluye los errores detectados durante la validación de datos.
         * 
         * @param libro el workbook con los datos originales
         * @param errores mapa con fila -> mensaje de error
         * @param columnaError índice de la columna donde se escribirán los errores
         * @return un identificador único del archivo generado
         * @throws IOException si ocurre un error al escribir el archivo
         */
    public static String generarArchivoErrores(Workbook libro, Map<Integer, String> errores, int columnaError) throws IOException {
        // Obtenemos la primera hoja
        Sheet hoja = libro.getSheetAt(0);
        
        // Agregar columna de errores si no existe
        Row filaEncabezado = hoja.getRow(0);
        if (filaEncabezado.getLastCellNum() <= columnaError) {
            Cell celdaError = filaEncabezado.createCell(columnaError);
            // Asignamos encabezado "Errores"
            celdaError.setCellValue("Errores");
        }

        // Crear estilo de celda para errores
        CellStyle estiloError = libro.createCellStyle();
        estiloError.setWrapText(true);
        // Recorrer cada error y colocarlo en la fila correspondiente
        for (Map.Entry<Integer, String> entrada : errores.entrySet()) {
            Row fila = hoja.getRow(entrada.getKey());
            Cell celdaError = fila.createCell(columnaError);
            celdaError.setCellValue(entrada.getValue());
            celdaError.setCellStyle(estiloError);
        }

        // Crear directorio si no existe
        File directorioErrores = new File(RutasConstantes.RUTA_EXCELS_ERROR);
        if (!directorioErrores.exists()) {
            directorioErrores.mkdirs();
        }

        // Guardar el archivo con errores
        String idArchivo = String.valueOf(System.currentTimeMillis());
        String rutaArchivo = RutasConstantes.RUTA_EXCELS_ERROR + "excel-errors-" + idArchivo + ".xlsx";
        
        try (FileOutputStream salida = new FileOutputStream(rutaArchivo)) {
            // Escribimos el workbook en disco
            libro.write(salida);
        }

        return idArchivo;
    }
    
         /**
             * Valida la longitud de un campo y si es obligatorio.
             * 
             * @param valor valor del campo
             * @param nombreCampo nombre descriptivo del campo
             * @param longitudMaxima máximo de caracteres permitidos
             * @param requerido true si es obligatorio, false si es opcional
             * @return mensaje de error si falla validación o null si es válido
         */
    public static String validarLargoCampo(String valor, String nombreCampo, int longitudMaxima, boolean requerido) {
        if ((valor == null || valor.trim().isEmpty()) && requerido) {
            return nombreCampo + " es requerido. ";
        } else if (valor != null && valor.length() > longitudMaxima) {
            return nombreCampo + " excede " + longitudMaxima + " caracteres. ";
        }
        return null;
    }

    /**
     * Valida que un correo electrónico tenga formato correcto y longitud aceptable.
     * 
     * @param correo correo a validar
     * @return mensaje de error si falla validación o null si es válido
     */

    public static String validarCorreo(String correo) {
        // Verifica que el correo no sea nulo ni vacío
        if (correo != null && !correo.isEmpty()) {
            // Valida la longitud máxima permitida (255 caracteres)
            if (correo.length() > 255) {
                return "Correo electrónico excede 255 caracteres. ";
                // Valida el formato del correo usando expresión regular
            } else if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return "Correo electrónico inválido. ";
            }
        }
        // Si pasa todas las validaciones, retorna null indicando que es válido
        return null;
    }
}
