package com.server.scapture.user.service;

import com.server.scapture.subscribe.dto.CreateSubscribeRequestDto;
import com.server.scapture.user.dto.ProfileEditDto;
import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    // 버내너 잔액 조회
    ResponseEntity<CustomAPIResponse<?>> getBananaBalance(String authorizationHeader);

    //  버내너 충전
    ResponseEntity<CustomAPIResponse<?>> addBananas(String authorizationHeader, int balance);

    //프로필 조회
    ResponseEntity<CustomAPIResponse<?>> getProfile(String authorizationHeader);

    // 프로필 수정
    ResponseEntity<CustomAPIResponse<?>> editProfile(ProfileEditDto data, List<MultipartFile> images);

    // 구독 생성
    ResponseEntity<CustomAPIResponse<?>> createSubscribe(String authorizationHeader, CreateSubscribeRequestDto createSubscribeRequestDto);

    // 구독 갱신
    ResponseEntity<CustomAPIResponse<?>> extensionSubscribe(String authorizationHeader);

    // 예약 내역 조회
    ResponseEntity<CustomAPIResponse<?>> searchReservations(String authorizationHeader);
}
