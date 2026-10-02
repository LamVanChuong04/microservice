package com.example.order_service.entity;

import com.example.order_service.common.BaseEntity;
import com.example.order_service.enums.PaymentMethod;
import com.example.order_service.enums.PaymentStatus;
import com.example.order_service.enums.StatusOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private String customerId;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private StatusOrder status;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private BigDecimal amount;

    @OneToMany(mappedBy = "order")
    private List<OrderItemEntity> orderItems = new ArrayList<>();
}
