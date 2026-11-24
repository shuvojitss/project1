package com.infy.scm360.notification.repository;

import com.infy.scm360.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId OR n.roleTarget IN :roles ORDER BY n.createdAt DESC")
    Page<Notification> findByUserOrRoles(String userId, Collection<String> roles, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.userId = :userId OR n.roleTarget IN :roles) AND n.read = false")
    long countUnreadByUserOrRoles(String userId, Collection<String> roles);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE (n.userId = :userId OR n.roleTarget IN :roles) AND n.read = false")
    int markAllReadByUserOrRoles(String userId, Collection<String> roles);
}
