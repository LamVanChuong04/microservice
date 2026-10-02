package com.example.product_service.entity;

import com.example.product_service.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false) // khóa ngoại của bảng products
    private CategoryEntity category;

    private BigDecimal price;

    private Integer quantityInStock;


}

