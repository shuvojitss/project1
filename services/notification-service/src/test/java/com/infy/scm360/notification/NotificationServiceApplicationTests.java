package com.infy.scm360.notification;

import com.infy.scm360.common.security.ScmUserPrincipal;
import com.infy.scm360.notification.dto.NotificationDto;
import com.infy.scm360.notification.dto.UnreadCountDto;
import com.infy.scm360.notification.service.NotificationService;
import com.infy.scm360.notification.service.SseEmitterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationServiceApplicationTests {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SseEmitterRegistry sseEmitterRegistry;

    @Test
    @DisplayName("Context loads and SSE registration succeeds")
    void testSseRegistration() {
        assertNotNull(notificationService);
        assertNotNull(sseEmitterRegistry);

        SseEmitter emitter = sseEmitterRegistry.register(
                "00000000-0000-0000-0000-000000000004", List.of("WAREHOUSE_MANAGER")
        );
        assertNotNull(emitter);
    }

    @Test
    @DisplayName("Notification creation, delivery, and read state lifecycle")
    void testNotificationLifecycle() {
        ScmUserPrincipal user = new ScmUserPrincipal(
                "00000000-0000-0000-0000-000000000004",
                "warehouse_mgr",
                List.of("WAREHOUSE_MANAGER"),
                List.of(),
                null
        );

        // 1. Create notification
        NotificationDto created = notificationService.createAndSend(
                null,
                "WAREHOUSE_MANAGER",
                "WARNING",
                "Stock Alert",
                "Critical replenishment required",
                "/inventory"
        );
        assertNotNull(created.id());
        assertFalse(created.isRead());

        // 2. Query unread count
        UnreadCountDto count = notificationService.getUnreadCount(user);
        assertTrue(count.unreadCount() > 0);

        // 3. List notifications
        Page<NotificationDto> page = notificationService.listNotifications(user, PageRequest.of(0, 10));
        assertFalse(page.isEmpty());
        assertTrue(page.stream().anyMatch(n -> n.title().equals("Stock Alert")));

        // 4. Mark single read
        NotificationDto read = notificationService.markRead(created.id(), user);
        assertTrue(read.isRead());

        // 5. Mark all read
        int updated = notificationService.markAllRead(user);
        assertTrue(updated >= 0);

        UnreadCountDto countAfter = notificationService.getUnreadCount(user);
        assertEquals(0, countAfter.unreadCount());
    }
}
