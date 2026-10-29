package com.example.order_service.dto.res;

import com.example.order_service.dto.client.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRes {
    private ProductDto product;
    private Integer quantity;

}
