package com.bootforge.hbp.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "processed_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_processed_event_id",
                        columnNames = "event_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "event_id",
            nullable = false,
            unique = true,
            length = 100
    )
    private String eventId;

    @Column(
            name = "event_type",
            nullable = false,
            length = 100
    )
    private String eventType;

    @Column(
            name = "processed_at",
            nullable = false
    )
    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {

        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }
}