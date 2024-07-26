package com.server.scapture.user.service;

import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    // 버내너 잔액 조회
    ResponseEntity<CustomAPIResponse<?>> getBananaBalance(String authorizationHeader);

    //  버내너 충전
    ResponseEntity<CustomAPIResponse<?>> addBananas(String authorizationHeader, int balance);

    //프로필 조회
    ResponseEntity<CustomAPIResponse<?>> getProfile(String authorizationHeader);
}
