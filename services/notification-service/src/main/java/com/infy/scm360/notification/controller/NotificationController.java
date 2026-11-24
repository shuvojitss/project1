package com.infy.scm360.notification.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.common.security.SecurityUtils;
import com.infy.scm360.notification.dto.NotificationDto;
import com.infy.scm360.notification.dto.UnreadCountDto;
import com.infy.scm360.notification.service.NotificationService;
import com.infy.scm360.notification.service.SseEmitterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "In-App Notifications", description = "Server-Sent Events (SSE) notification stream, alert inbox, and read state management")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterRegistry sseEmitterRegistry;

    public NotificationController(NotificationService notificationService, SseEmitterRegistry sseEmitterRegistry) {
        this.notificationService = notificationService;
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Subscribe to real-time in-app notification Server-Sent Events (SSE)")
    public SseEmitter subscribeStream() {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        String userId = user != null ? user.getUserId() : "SYSTEM";
        var roles = user != null ? user.getRoles() : Collections.<String>emptyList();
        return sseEmitterRegistry.register(userId, roles);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List notifications for the logged-in user with pagination")
    public ResponseEntity<PageResponse<NotificationDto>> listNotifications(Pageable pageable) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        Page<NotificationDto> page = notificationService.listNotifications(user, pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get count of unread notifications for current user")
    public ResponseEntity<UnreadCountDto> getUnreadCount() {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.ok(notificationService.getUnreadCount(user));
    }

    @PostMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark single notification as read")
    public ResponseEntity<NotificationDto> markRead(@PathVariable String id) {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.ok(notificationService.markRead(id, user));
    }

    @PostMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark all notifications as read for current user")
    public ResponseEntity<Integer> markAllRead() {
        ScmUserPrincipal user = SecurityUtils.getCurrentUser().orElse(null);
        return ResponseEntity.ok(notificationService.markAllRead(user));
    }
}
