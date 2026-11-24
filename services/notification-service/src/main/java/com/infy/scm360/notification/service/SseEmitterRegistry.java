package com.infy.scm360.notification.service;

import com.infy.scm360.notification.dto.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterRegistry {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterRegistry.class);
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 mins

    // userId -> list of emitters (user might have multiple browser tabs)
    private final Map<String, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    // role -> set of userIds currently online with that role
    private final Map<String, Set<String>> roleUsers = new ConcurrentHashMap<>();

    public SseEmitter register(String userId, Collection<String> roles) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        if (roles != null) {
            for (String r : roles) {
                String cleanRole = r.startsWith("ROLE_") ? r.substring(5) : r;
                roleUsers.computeIfAbsent(cleanRole, k -> ConcurrentHashMap.newKeySet()).add(userId);
            }
        }

        emitter.onCompletion(() -> removeEmitter(userId, roles, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, roles, emitter));
        emitter.onError(e -> removeEmitter(userId, roles, emitter));

        // Send initial connection ACK
        try {
            emitter.send(SseEmitter.event().name("CONNECT").data("Connected to SCM360 notification stream"));
        } catch (IOException e) {
            removeEmitter(userId, roles, emitter);
        }

        return emitter;
    }

    public void sendToUser(String userId, NotificationDto dto) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("NOTIFICATION").data(dto));
                } catch (Exception e) {
                    emitters.remove(emitter);
                }
            }
        }
    }

    public void sendToRole(String role, NotificationDto dto) {
        String cleanRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        Set<String> userIds = roleUsers.get(cleanRole);
        if (userIds != null) {
            for (String userId : userIds) {
                sendToUser(userId, dto);
            }
        }
    }

    public void sendToAll(NotificationDto dto) {
        userEmitters.keySet().forEach(uid -> sendToUser(uid, dto));
    }

    private void removeEmitter(String userId, Collection<String> roles, SseEmitter emitter) {
        List<SseEmitter> list = userEmitters.get(userId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                userEmitters.remove(userId);
                if (roles != null) {
                    for (String r : roles) {
                        String cleanRole = r.startsWith("ROLE_") ? r.substring(5) : r;
                        Set<String> uids = roleUsers.get(cleanRole);
                        if (uids != null) uids.remove(userId);
                    }
                }
            }
        }
    }
}
