package com.example.authservice.controller;

import com.example.authservice.dto.req.LoginReq;
import com.example.authservice.dto.req.UserRegisterReq;
import com.example.authservice.dto.res.LoginRes;
import com.example.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;


    // dk tai khoan
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterReq req) {
        userService.createUser(req);
        return ResponseEntity.ok().body("Register successful");
    }

    // get token
    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq req) {
        return ResponseEntity.ok().body(userService.login(req));
    }
}
