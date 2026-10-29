//package com.example.product_service.service.impl;
//
//import com.example.product_service.service.RedisService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class RedisServiceImp implements RedisService {
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final HashOperations<String, String, Object> hashOperations;
//    public RedisServiceImp(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//        this.hashOperations = redisTemplate.opsForHash();
//    }
//    @Override
//    public void set(String key, Object value) {
//        redisTemplate.opsForValue().set(key, value);
//    }
//
//    @Override
//    public void setWithTtl(String key, Object value, long timeout, TimeUnit timeUnit) {
//        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
//    }
//
//    @Override
//    public Object get(String key) {
//        return redisTemplate.opsForValue().get(key);
//    }
//
//    @Override
//    public boolean delete(String key) {
//        return redisTemplate.delete(key);
//    }
//
//    @Override
//    public boolean hasKey(String key) {
//        return redisTemplate.hasKey(key);
//    }
//
//    @Override
//    public void hashSet(String key, String field, Object value) {
//        hashOperations.put(key, field, value);
//    }
//
//    @Override
//    public boolean hashExist(String key, String field) {
//        return hashOperations.hasKey(key, field);
//    }
//
//    @Override
//    public Object hashGet(String key, String field) {
//        return hashOperations.get(key, field);
//    }
//
//    @Override
//    public void delete(String key, String field) {
//        hashOperations.delete(key, field);
//    }
//
//    @Override
//    public void delete(String key, List<String> fields) {
//        for(String field : fields)
//        {
//            hashOperations.delete(key, field);
//        }
//    }
//}
