package com.example.product_service.service.impl;

import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.req.LockProductItem;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.dto.req.ProductFilter;
import com.example.product_service.dto.res.ProductRes;
import com.example.product_service.entity.CategoryEntity;
import com.example.product_service.entity.ProductEntity;
import com.example.product_service.exception.BusinessException;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.repository.CategoryRepository;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {
    private final ProductRepository repo;
    private final ProductMapper mapper;
    private final CategoryRepository categoryRepo;

    @Override
    @Transactional
    public ProductRes create(CreateProductReq req) {
        CategoryEntity category = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(()-> new BusinessException("Category Not Found"));
        ProductEntity entity = mapper.fromProductReq(req);
        entity.setCategory(category);
        repo.save(entity);
        return mapper.fromProductEntity(entity);
    }

    @Override
    public List<ProductRes> search(ProductFilter productFilter) {
        List<ProductEntity> products = repo.findAllByIds(productFilter.getIds());
        return products.stream().map(mapper::fromProductEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void lock(LockProductReq req) {
        List<LockProductItem> items = req.getItems();
        Map<String, Integer> map = items.stream().collect(Collectors.toMap(LockProductItem::getProductId, LockProductItem::getQuantity));

        List<ProductEntity> products = repo.findAllByIds(new ArrayList<>(map.keySet()));

        if (products.isEmpty()) {
            throw new BusinessException("Product Not Found");
        }
        products.forEach(product -> {
            product.setQuantityInStock(product.getQuantityInStock() - map.get(product.getId()));
        });
        repo.saveAll(products);
    }

}
