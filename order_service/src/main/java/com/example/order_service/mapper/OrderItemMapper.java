package com.example.order_service.mapper;

import com.example.order_service.dto.req.OrderItemReq;
import com.example.order_service.entity.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemReq fromEntity(OrderItemEntity entity);
}
