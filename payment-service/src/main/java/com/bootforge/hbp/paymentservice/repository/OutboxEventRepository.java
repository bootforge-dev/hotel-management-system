package com.bootforge.hbp.paymentservice.repository;

import com.bootforge.hbp.common.dto.booking.outbox.OutboxStatus;
import com.bootforge.hbp.paymentservice.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent>
    findTop100ByStatusOrderByCreatedAtAsc(
            OutboxStatus status
    );
}