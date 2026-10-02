package com.example.order_service.entity;

import com.example.order_service.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Table(name = "order_items")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private String productId;
    private Integer quantity;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}
