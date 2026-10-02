package com.example.product_service.service.impl;

import com.example.product_service.dto.req.CreateCategoryReq;
import com.example.product_service.dto.res.CategoryRes;
import com.example.product_service.entity.CategoryEntity;
import com.example.product_service.mapper.CategoryMapper;
import com.example.product_service.repository.CategoryRepository;
import com.example.product_service.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository repo;
    private final CategoryMapper mapper;

    @Override
    @Transactional
    public CategoryRes create(CreateCategoryReq req) {
        CategoryEntity entity = mapper.fromCategoryReq(req);
        repo.save(entity);
        return mapper.fromCategoryEntity(entity);
    }
}
