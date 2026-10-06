package com.example.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "outbox_event")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 64)
    private String aggregateId;
    @Column(nullable = false, length = 64)
    private String aggregateType;
    @Column(nullable = false, length = 64)
    private String eventType;
    @Column(nullable = false, columnDefinition = "json")
    private String payload;
    private int status;
    private LocalDateTime createdAt;
}
