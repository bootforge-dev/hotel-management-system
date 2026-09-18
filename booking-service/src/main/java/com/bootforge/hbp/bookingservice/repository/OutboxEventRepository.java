package com.bootforge.hbp.bookingservice.repository;

import com.bootforge.hbp.bookingservice.entity.OutboxEvent;
import com.bootforge.hbp.common.dto.booking.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}