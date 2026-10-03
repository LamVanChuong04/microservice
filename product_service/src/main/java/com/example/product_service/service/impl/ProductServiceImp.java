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
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImp implements ProductService {
    private final ProductRepository repo;
    private final ProductMapper mapper;
    private final CategoryRepository categoryRepo;
    private final RedissonClient redissonClient;

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
    @Cacheable(value = "products", key = "#productFilter.ids", condition = "#productFilter != null")
    public List<ProductRes> search(ProductFilter productFilter) {
        List<ProductEntity> products = repo.findAllByIds(productFilter.getIds());
        return products.stream().map(mapper::fromProductEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void lock(LockProductReq req) {
        List<LockProductItem> items = req.getItems();
        Map<String, Integer> map = items.stream()
                .collect(Collectors.toMap(LockProductItem::getProductId, LockProductItem::getQuantity));

        List<ProductEntity> products = repo.findAllByIds(new ArrayList<>(map.keySet()));

        if (products.isEmpty()) {
            throw new BusinessException("Product Not Found");
        }
        products.forEach(product -> {
            product.setQuantityInStock(product.getQuantityInStock() - map.get(product.getId()));
        });
        repo.saveAll(products);
    }

    @Override
    @Transactional
    public void lockForUpdate(LockProductReq req) {
        List<LockProductItem> items = req.getItems();
        Map<String, Integer> map = items.stream()
                .collect(Collectors.toMap(LockProductItem::getProductId, LockProductItem::getQuantity));

        // pessimistic lock => select ... for update
        List<ProductEntity> products = repo.findAllById(new ArrayList<>(map.keySet()));

        if (products.isEmpty()) {
            throw new BusinessException("Product Not Found");
        }
        products.forEach(product -> {
            product.setQuantityInStock(product.getQuantityInStock() - map.get(product.getId()));
        });
        repo.saveAll(products);
    }

    @Override
    @Transactional
    //@CacheEvict(allEntries = true, value = "product") // khi data update se delete cache nay di
    public void distributeLock(LockProductReq req) {
        // get product item from order request
        List<LockProductItem> items = req.getItems();

        // instance 1: => update product:1,2
        // instance 2: => update product:2,1

        // 1. Tạo khóa dựa trên ds item sắp xếp theo thứ tự
        // sort prevent deadlock
        List<String> sortIds = items.stream()
                .map(LockProductItem::getProductId)
                .sorted()
                .collect(Collectors.toList());

        String lockKey = "lock:products:" + String.join(",", sortIds);
        RLock lock = redissonClient.getLock(lockKey);

        try{
            // 2. Thử lấy lock trong 10s, hold trong 5s
            if(lock.tryLock(10, 5, TimeUnit.SECONDS))
            {
                Thread.sleep(4000);
                log.info("acquired redis lock for {}", lockKey);
                var productQuantityMap = items.stream()
                        .collect(Collectors.toMap(LockProductItem::getProductId, LockProductItem::getQuantity));
                // 3. Khong dung select .. for update
                List<ProductEntity> products = repo.findAllByIds(new ArrayList<>(productQuantityMap.keySet()));

                if (products.isEmpty()) {
                    throw new BusinessException("Product Not Found");
                }

                // 4. Tinh khau tru ton kho
                products.forEach(product -> {
                    Integer remainStock = product.getQuantityInStock() - productQuantityMap.get(product.getId());
                    if (remainStock < 0) {
                        throw new BusinessException("Product " + product.getId() + " out of stock");
                    }
                    product.setQuantityInStock(remainStock);
                });
                // 5. Save xuong db
                repo.saveAll(products);
            }
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }
    }

}
