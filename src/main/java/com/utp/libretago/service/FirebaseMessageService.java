package com.utp.libretago.service;

import java.util.List;
import java.util.Map;

public interface FirebaseMessageService {
    void sendNotification(String title, String body, Map<String, String> data, List<String> tokens);
}