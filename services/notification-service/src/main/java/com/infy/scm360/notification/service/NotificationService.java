package com.infy.scm360.notification.service;

import com.infy.scm360.common.exception.ResourceNotFoundException;
import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.notification.domain.Notification;
import com.infy.scm360.notification.dto.NotificationDto;
import com.infy.scm360.notification.dto.UnreadCountDto;
import com.infy.scm360.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterRegistry sseEmitterRegistry;

    public NotificationService(NotificationRepository notificationRepository, SseEmitterRegistry sseEmitterRegistry) {
        this.notificationRepository = notificationRepository;
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    public NotificationDto createAndSend(String userId, String roleTarget, String type, String title, String message, String link) {
        Notification notification = new Notification(userId, roleTarget, type, title, message, link);
        Notification saved = notificationRepository.save(notification);
        NotificationDto dto = toDto(saved);

        if (userId != null && !userId.isBlank()) {
            sseEmitterRegistry.sendToUser(userId, dto);
        } else if (roleTarget != null && !roleTarget.isBlank()) {
            sseEmitterRegistry.sendToRole(roleTarget, dto);
        } else {
            sseEmitterRegistry.sendToAll(dto);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> listNotifications(ScmUserPrincipal user, Pageable pageable) {
        String userId = user != null ? user.getUserId() : "SYSTEM";
        Collection<String> roles = user != null ? cleanRoles(user.getRoles()) : Collections.emptyList();
        return notificationRepository.findByUserOrRoles(userId, roles, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public UnreadCountDto getUnreadCount(ScmUserPrincipal user) {
        String userId = user != null ? user.getUserId() : "SYSTEM";
        Collection<String> roles = user != null ? cleanRoles(user.getRoles()) : Collections.emptyList();
        long count = notificationRepository.countUnreadByUserOrRoles(userId, roles);
        return new UnreadCountDto(count);
    }

    public NotificationDto markRead(String id, ScmUserPrincipal user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        notification.setRead(true);
        return toDto(notificationRepository.save(notification));
    }

    public int markAllRead(ScmUserPrincipal user) {
        String userId = user != null ? user.getUserId() : "SYSTEM";
        Collection<String> roles = user != null ? cleanRoles(user.getRoles()) : Collections.emptyList();
        return notificationRepository.markAllReadByUserOrRoles(userId, roles);
    }

    private List<String> cleanRoles(Collection<String> roles) {
        return roles.stream()
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                .toList();
    }

    public NotificationDto toDto(Notification n) {
        return new NotificationDto(
                n.getId(),
                n.getUserId(),
                n.getRoleTarget(),
                n.getType(),
                n.getTitle(),
                n.getMessage(),
                n.getLink(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
