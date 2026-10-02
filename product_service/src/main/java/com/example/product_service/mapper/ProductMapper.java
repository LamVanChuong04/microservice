package com.example.product_service.mapper;

import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.res.ProductRes;
import com.example.product_service.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductEntity fromProductReq(CreateProductReq req);
    ProductRes fromProductEntity(ProductEntity entity);
    ProductEntity update(CreateProductReq req , @MappingTarget ProductEntity entity);
}
