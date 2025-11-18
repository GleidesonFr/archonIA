package com.backend.archonia.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/redis-test")
public class RedisTestController {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/set")
    public String setValue() {
        redisTemplate.opsForValue().set("testKey", "Hello, Redis!");
        return "Value set in Redis";
    }

    @GetMapping("/get")
    public String getValue() {
        String value = (String) redisTemplate.opsForValue().get("testKey");
        return "Value from Redis: " + value;
    }
    
}
