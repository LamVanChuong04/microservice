package com.example.product_service.repository;

import com.example.product_service.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity,String> {
    @Query("select p from ProductEntity p where p.id in :ids")
    List<ProductEntity> findAllByIds(List<String> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductEntity p where p.id in :ids")
    List<ProductEntity> findAllById(List<String> ids);
}
