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
import com.utp.libretago.service.UsuarioService;
import com.utp.libretago.classes.filtros.FiltroUsuario;
import com.utp.libretago.classes.dto.UsuarioInstitucionDTO;
import com.utp.libretago.entity.Rol;
import com.utp.libretago.config.security.AppUser;
import com.utp.libretago.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

/**
 * Controlador REST para la descarga de archivos generados por la aplicación.
 * <p>
 * Permite descargar archivos Excel que contienen errores de validación generados durante el procesamiento de datos.
 * </p>
 * 
 * <p>
 * El archivo se obtiene del directorio definido en {@link RutasConstantes#RUTA_EXCELS_ERROR}.
 * </p>
 * 
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-27
 */

@RestController
@RequestMapping("/descargar")
public class DescargarArchivoEndpoint {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Permite descargar un archivo Excel de errores por su identificador.
     * <p>
     * El archivo debe existir en el directorio configurado para archivos de error. Si no existe, se devuelve un estado 404
     * (no encontrado).
     * </p>
     * 
     * @param id identificador único del archivo generado (parte del nombre del archivo)
     * @return una respuesta HTTP con el archivo como recurso adjunto o un estado 404 si no se encuentra
     */
    @GetMapping(value = "/excelErrores/{id}")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable String id) {
        try {
            // Construir la ruta completa del archivo Excel de errores
            String rutaArchivo = RutasConstantes.RUTA_EXCELS_ERROR + "excel-errors-" + id + ".xlsx";
            // Crear un recurso que representa el archivo en el sistema de archivos
            Resource archivo = new FileSystemResource(rutaArchivo);

            // Si el archivo existe, preparar la respuesta con los encabezados adecuados
            if (archivo.exists()) {
                return ResponseEntity.ok()
                        // Forzar descarga con un nombre de archivo amigable
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"errores_" + id + ".xlsx\"")
                        // Especificar el tipo de contenido (Excel)
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        // Incluir el archivo como cuerpo de la respuesta
                        .body(archivo);
            }
        } catch (Exception e) {
            // Log error
        }
        // Si el archivo no existe o ocurre un error, devolver 404 Not Found
        return ResponseEntity.notFound().build();
    }

    /**
     * Genera y descarga un reporte de usuarios en Excel según filtros.
     * 
     * @param nombre nombre del usuario (opcional)
     * @param rolId  ID del rol (opcional)
     * @return archivo Excel
     */
    @GetMapping(value = "/usuarios/reporte")
    public ResponseEntity<Resource> descargarReporteUsuarios(String nombre, Long rolId) {
        try {
            AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            FiltroUsuario filtro = new FiltroUsuario();
            filtro.setNombreCompleto(nombre);
            filtro.setRolId(Rol.ID_PROFESOR); // Forzamos rol profesor como en la vista
            filtro.setInstitucionEducativaId(appUser.getInstitucionEducativaId());

            // Obtenemos todos los registros (sin paginación real, usamos un tamaño grande)
            // Nota: Para reportes grandes, esto debería optimizarse.
            Pageable pageable = Pageable.unpaged();

            var pagina = usuarioService.buscarUsuarioInstitucionPorFiltros(filtro, pageable);
            List<UsuarioInstitucionDTO> usuarios = pagina.getContent();

            String idArchivo = ExcelUtils.generarExcelUsuarios(usuarios);
            String rutaArchivo = RutasConstantes.RUTA_EXCELS_ERROR + "reporte-usuarios-" + idArchivo + ".xlsx";

            Resource archivo = new FileSystemResource(rutaArchivo);

            if (archivo.exists()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte_usuarios_" + idArchivo + ".xlsx\"")
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(archivo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }
}
