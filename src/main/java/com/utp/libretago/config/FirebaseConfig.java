package com.utp.libretago.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Configuración de Firebase para la aplicación.
 * <p>
 * Proporciona los beans necesarios para integrar Firebase y Firebase Cloud Messaging (FCM)
 * con Spring Boot.
 * </p>
 * 
 * <p>Incluye la inicialización de {@link FirebaseApp} y {@link FirebaseMessaging}.</p>
 * 
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-27
 */


@Configuration
public class FirebaseConfig {

    /** Ruta al archivo de configuración de Firebase, inyectada desde application.properties */
    @Value("${app.firebase.configuration.file}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Verificar si ya existe una instancia para evitar problemas con spring-boot-devtools
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }
        // Cargar credenciales desde el archivo JSON de configuración
        FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
         // Construir las opciones de Firebase con las credenciales
        FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
        // Inicializar y devolver la instancia de FirebaseApp
        return FirebaseApp.initializeApp(options);
    }
    
    /**
     * Crea el bean {@link FirebaseMessaging} usando la instancia de {@link FirebaseApp}.
     * 
     * @param firebaseApp la instancia de {@link FirebaseApp} a usar
     * @return la instancia de {@link FirebaseMessaging} lista para enviar notificaciones
     */
    
    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
