package com.utp.libretago.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.utp.libretago.service.FirebaseMessageService;
/**
     * Implementación de {@link FirebaseMessageService} para enviar notificaciones push
     * a dispositivos móviles usando Firebase Cloud Messaging (FCM).
     * Permite enviar mensajes en lotes de hasta 500 tokens por envío.
     * @author Roberto
     * @version 1.0
     * @since 2025-10-28
 */
@Service
public class FirebaseMessageServiceImpl implements FirebaseMessageService {
     /** Límite de tokens que Firebase permite enviar en un solo lote */
    private final static int FIREBASE_LIMIT = 500;

    @Autowired
    private FirebaseMessaging firebaseMessaging;
    /**
         * Envía una notificación push a una lista de dispositivos.
         * Divide la lista en lotes de 500 tokens para cumplir con las restricciones de Firebase.
         * @param title  Título de la notificación.
         * @param body   Cuerpo de la notificación.
         * @param data   Datos adicionales en formato clave-valor.
         * @param tokens Lista de tokens de dispositivos a los que enviar la notificación.
     */
    @Override
    public void sendNotification(String title, String body, Map<String, String> data, List<String> tokens) {
        Notification notification = Notification.builder().setTitle(title).setBody(body).build();

        // Enviar en lotes de 500 tokens
        for (int i = 0; i < tokens.size(); i += FIREBASE_LIMIT) {
            int end = Math.min(i + FIREBASE_LIMIT, tokens.size());
            List<String> tokensLimite = tokens.subList(i, end);
            // Enviar cada lote
            enviarEnLotes(notification, data, tokensLimite);
        }
    }
    /**
         * Envía un lote de mensajes a Firebase.
         * Cada token recibe un mensaje con la misma notificación y datos.
         * @param notification Notificación que se enviará.
         * @param data         Datos adicionales a enviar.
         * @param tokens       Lista de tokens para el lote.
     */

    private void enviarEnLotes(Notification notification, Map<String, String> data, List<String> tokens) {
        // Lista para almacenar los mensajes del lote
        List<Message> messages = new ArrayList<>();

        // Construir los mensajes por cada token
        for (String token : tokens) {
            Message message = Message.builder().setToken(token).setNotification(notification).putAllData(data).build();
            messages.add(message);
        }

        try {
            // Enviar el lote completo usando sendEach (reemplaza a sendAll deprecado)
            firebaseMessaging.sendEach(messages);

        } catch (Exception e) {
            // Imprimir cualquier error durante el envío
            e.printStackTrace();
        }
    }
}
