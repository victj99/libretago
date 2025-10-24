package com.utp.libretago.endpoint;

import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utp.libretago.utils.RutasConstantes;

@RestController
@RequestMapping("/descargar")
public class DescargarArchivoEndpoint {

    @GetMapping(value = "/excelErrores/{id}")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable String id) {
        try {
            String rutaArchivo = RutasConstantes.RUTA_EXCELS_ERROR + "excel-errors-" + id + ".xlsx";
            Resource archivo = new FileSystemResource(rutaArchivo);
            
            if (archivo.exists()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"errores_" + id + ".xlsx\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(archivo);
            }
        } catch (Exception e) {
            // Log error
        }
        
        return ResponseEntity.notFound().build();
    }
}
