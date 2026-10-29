package com.example.product_service.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface RedisService {
    // key-value operation
    void set(String key, Object value);
    void setWithTtl(String key, Object value, long timeout, TimeUnit timeUnit);
    Object get(String key);
    boolean delete(String key);
    boolean hasKey(String key);


    // hash operation
    void hashSet(String key, String field, Object value);
    boolean hashExist(String key, String field);
    Object hashGet(String key, String field);
    void delete(String key, String field);
    void delete(String key, List<String> fields);
}
