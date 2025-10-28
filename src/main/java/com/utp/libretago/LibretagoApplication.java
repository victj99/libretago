package com.utp.libretago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

/**
 * Clase principal de la aplicación Libretago.
 * <p>
 * Esta clase arranca la aplicación Spring Boot y configura la integración con Vaadin.
 * </p>
 * 
 * <p>Se utiliza la anotación {@link Theme} para definir el tema por defecto de Vaadin.</p>
 * 
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-27
 */

// Define el tema por defecto para la interfaz Vaadin
@Theme("default")
// Marca esta clase como la entrada principal de Spring Boot
@SpringBootApplication
public class LibretagoApplication implements AppShellConfigurator {

    /**
     * Punto de entrada de la aplicación.
     * 
     * @param args argumentos de línea de comando
     */
	public static void main(String[] args) {
		// Arranca la aplicación Spring Boot
		SpringApplication.run(LibretagoApplication.class, args);
	}

}
