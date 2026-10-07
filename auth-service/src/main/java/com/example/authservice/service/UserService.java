package com.example.authservice.service;

import com.example.authservice.dto.req.UserRegisterReq;

public interface UserService {
    void createUser(UserRegisterReq req);
}
