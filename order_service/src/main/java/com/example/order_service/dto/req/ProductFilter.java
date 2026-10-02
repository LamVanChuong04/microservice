package com.example.order_service.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class ProductFilter {
    private List<String> ids;
}
