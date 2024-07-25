package com.server.scapture.user.controller;

import com.server.scapture.user.service.UserService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    // 버내너 잔액 조회
    @GetMapping("/bananas")
    public ResponseEntity<CustomAPIResponse<?>> searchBananas(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return userService.getBananaBalance(authorizationHeader);
    }
}
