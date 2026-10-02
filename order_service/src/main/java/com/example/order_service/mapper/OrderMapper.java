package com.example.order_service.mapper;

import com.example.order_service.dto.res.OrderRes;
import com.example.order_service.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderRes fromOrderEntity(OrderEntity entity);
}
