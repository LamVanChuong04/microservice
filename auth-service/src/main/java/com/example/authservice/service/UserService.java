package com.example.authservice.service;

import com.example.authservice.dto.req.LoginReq;
import com.example.authservice.dto.req.UserRegisterReq;
import com.example.authservice.dto.res.LoginRes;

public interface UserService {
    String createUser(UserRegisterReq req);
    LoginRes login(LoginReq req);
}
