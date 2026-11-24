package com.infy.scm360.common.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {

    @Query(value = "SELECT * FROM outbox_event WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT :limit FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEvent> findPendingEventsForProcessing(@Param("limit") int limit);

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    @Modifying
    @Query("DELETE FROM OutboxEvent o WHERE o.status = :status AND o.sentAt < :cutoff")
    int deleteOldSentEvents(@Param("status") OutboxStatus status, @Param("cutoff") Instant cutoff);
}
