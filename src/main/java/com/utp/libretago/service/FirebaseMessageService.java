package com.utp.libretago.service;

import java.util.List;
import java.util.Map;
/**
 * Interfaz para el envío de notificaciones push a través de Firebase Cloud Messaging (FCM).
 * <p>
 * Permite enviar mensajes con título, cuerpo y datos adicionales a múltiples dispositivos mediante sus tokens.
 * </p>
 * @author Roberto Anton
 * @version 1.0
 * @since 2025-10-28
 */
public interface FirebaseMessageService {
    /**
         * Envía una notificación push a los dispositivos indicados.
         * @param title  Título de la notificación.
         * @param body   Cuerpo o mensaje de la notificación.
         * @param data   Datos adicionales que se incluirán en la notificación (clave-valor).
         * @param tokens Lista de tokens de los dispositivos que recibirán la notificación.
     */
    void sendNotification(String title, String body, Map<String, String> data, List<String> tokens);
}
