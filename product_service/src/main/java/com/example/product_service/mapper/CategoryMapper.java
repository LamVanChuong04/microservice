package com.example.product_service.mapper;

import com.example.product_service.dto.req.CreateCategoryReq;
import com.example.product_service.dto.res.CategoryRes;
import com.example.product_service.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity fromCategoryReq(CreateCategoryReq req);
    CategoryRes fromCategoryEntity(CategoryEntity entity);
    CategoryEntity update(CreateCategoryReq req, @MappingTarget CategoryEntity entity);
}
