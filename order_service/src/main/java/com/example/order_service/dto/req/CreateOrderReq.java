package com.example.order_service.dto.req;

import com.example.order_service.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderReq {
    @NotEmpty(message = "Customer id is not empty" )
    private String customerId;
    private PaymentMethod paymentMethod;
    @Valid
    private List<OrderItemReq> items;


}
