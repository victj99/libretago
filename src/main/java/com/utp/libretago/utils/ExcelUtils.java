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

public class ExcelUtils {
    
    public static String getValorCeldaComoTexto(Row fila, int pos) {
        return getValorCeldaComoTexto(fila.getCell(pos));

    }
    public static String getValorCeldaComoTexto(Cell celda) {
        if (celda == null) return null;
        switch (celda.getCellType()) {
            case STRING:
                return celda.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) celda.getNumericCellValue());
            default:
                return null;
        }
    }

    public static String generarArchivoErrores(Workbook libro, Map<Integer, String> errores, int columnaError) throws IOException {
        Sheet hoja = libro.getSheetAt(0);
        
        // Agregar columna de errores si no existe
        Row filaEncabezado = hoja.getRow(0);
        if (filaEncabezado.getLastCellNum() <= columnaError) {
            Cell celdaError = filaEncabezado.createCell(columnaError);
            celdaError.setCellValue("Errores");
        }

        // Agregar errores a las filas correspondientes
        CellStyle estiloError = libro.createCellStyle();
        estiloError.setWrapText(true);

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
            libro.write(salida);
        }

        return idArchivo;
    }

    public static String validarLargoCampo(String valor, String nombreCampo, int longitudMaxima, boolean requerido) {
        if ((valor == null || valor.trim().isEmpty()) && requerido) {
            return nombreCampo + " es requerido. ";
        } else if (valor != null && valor.length() > longitudMaxima) {
            return nombreCampo + " excede " + longitudMaxima + " caracteres. ";
        }
        return null;
    }

    public static String validarCorreo(String correo) {
        if (correo != null && !correo.isEmpty()) {
            if (correo.length() > 255) {
                return "Correo electrónico excede 255 caracteres. ";
            } else if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return "Correo electrónico inválido. ";
            }
        }
        return null;
    }
}