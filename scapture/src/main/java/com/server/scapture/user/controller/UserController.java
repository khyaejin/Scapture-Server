package com.server.scapture.user.controller;

import com.server.scapture.user.dto.ProfileEditDto;
import com.server.scapture.user.service.UserService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.server.scapture.subscribe.dto.CreateSubscribeRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    // 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<CustomAPIResponse<?>> getProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return userService.getProfile(authorizationHeader);
    }

    // 프로필 편집
    @PutMapping("/profile")
    public ResponseEntity<CustomAPIResponse<?>> editProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestPart("data") ProfileEditDto profileEditDto,
            @RequestPart(value = "image", required = false) MultipartFile images) throws IOException {
        return userService.editProfile(authorizationHeader, profileEditDto, images);
    }

    // 버내너 잔액 조회
    @GetMapping("/bananas")
    public ResponseEntity<CustomAPIResponse<?>> searchBananas(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return userService.getBananaBalance(authorizationHeader);
    }

    // 버내너 충전
    @PostMapping("/bananas")
    public ResponseEntity<CustomAPIResponse<?>> addBananas(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody int balance) {
        return userService.addBananas(authorizationHeader, balance);
    }

    // 구독 생성
    @PostMapping("/subscribe")
    public ResponseEntity<CustomAPIResponse<?>> createSubscribe(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody CreateSubscribeRequestDto createSubscribeRequestDto) {
        return userService.createSubscribe(authorizationHeader, createSubscribeRequestDto);
    }

    // 구독 갱신
    @PutMapping("/subscribe")
    public ResponseEntity<CustomAPIResponse<?>> renewSubscribe(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return userService.extensionSubscribe(authorizationHeader);
    }

    // 예약 내역 조회
    @GetMapping("/reservations")
    public ResponseEntity<CustomAPIResponse<?>> searchReservations(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return userService.searchReservations(authorizationHeader);
    }
}
