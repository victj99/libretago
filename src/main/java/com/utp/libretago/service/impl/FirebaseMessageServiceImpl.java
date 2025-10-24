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

@Service
public class FirebaseMessageServiceImpl implements FirebaseMessageService {
    private final static int FIREBASE_LIMIT = 500;

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Override
    public void sendNotification(String title, String body, Map<String, String> data, List<String> tokens) {
        Notification notification = Notification.builder().setTitle(title).setBody(body).build();

        // Enviar en lotes de 500
        for (int i = 0; i < tokens.size(); i += FIREBASE_LIMIT) {
            int end = Math.min(i + FIREBASE_LIMIT, tokens.size());
            List<String> tokensLimite = tokens.subList(i, end);

            enviarEnLotes(notification, data, tokensLimite);
        }
    }

    private void enviarEnLotes(Notification notification, Map<String, String> data, List<String> tokens) {
        List<Message> messages = new ArrayList<>();

        // Construir los mensajes del lote
        for (String token : tokens) {
            Message message = Message.builder().setToken(token).setNotification(notification).putAllData(data).build();
            messages.add(message);
        }

        try {
            // Enviar el lote completo usando sendEach (reemplaza a sendAll deprecado)
            firebaseMessaging.sendEach(messages);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}