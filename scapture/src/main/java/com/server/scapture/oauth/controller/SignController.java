package com.server.scapture.oauth.controller;

import com.server.scapture.domain.User;
import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.oauth.service.SignService;
import com.server.scapture.util.response.CustomAPIResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/oauth/social")
@RequiredArgsConstructor
public class SignController {
    private static final Logger logger = LoggerFactory.getLogger(SignController.class);
    private final SignService signService;
    private final JwtUtil jwtUtil;

    //카카오 소셜 로그인
    @PostMapping(value = "/kakao")
    public ResponseEntity<CustomAPIResponse<?>> kakaoLogin(@RequestParam String code) {
        // 1. 인가 코드 받기 (@RequestParam String code)
        logger.info("Request_Code: {}", code);
        if (code.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(CustomAPIResponse.createFailWithoutData(HttpStatus.FORBIDDEN.value(), "인가 코드를 전달받지 못했습니다."));
        }

        // 2. 접근 토큰 받기
        ResponseEntity<CustomAPIResponse<?>> tokenResponse = signService.getAccessToken(code);
        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(tokenResponse.getStatusCode()).body(tokenResponse.getBody());
        }

        // 3. 사용자 정보 받기
        String accessToken = (String) tokenResponse.getBody().getData(); //후에 서비스 계층 안으로 넣어주기
        ResponseEntity<CustomAPIResponse<?>> userInfoResponse = signService.getUserInfo(accessToken);
        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(tokenResponse.getStatusCode()).body(tokenResponse.getBody());
        }

        // 4. 로그인/회원가입 후 JWT 토큰 발급
        UserInfo userInfo = (UserInfo) userInfoResponse.getBody().getData(); //후에 서비스 계층 안으로 넣어주기
        //log를 통한 테스트 용도
        logger.info("User_Email: {}", userInfo.getEmail());
        logger.info("User_Name: {}", userInfo.getName());
        logger.info("User_ProviderId: {}", userInfo.getProviderId());
        logger.info("User_Image: {}", userInfo.getImage());
        ResponseEntity<CustomAPIResponse<?>> loginResponse = signService.login(userInfo);
        if (loginResponse.getBody().getStatus() != 200 || loginResponse.getBody().getStatus() != 201) {
            return ResponseEntity.status(loginResponse.getStatusCode()).body(loginResponse.getBody());
        }
        return ResponseEntity.status(loginResponse.getStatusCode()).body(loginResponse.getBody());
    }

    @GetMapping(value = "/test/find/user")
    public Optional<User> testGetUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return jwtUtil.findUserByJwtToken(authorizationHeader);
    }

    @GetMapping(value = "/test/find/user/fail")
    public User testGetUserFail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        Optional<User> foundUser =  jwtUtil.findUserByJwtToken(authorizationHeader);
        if (foundUser.isEmpty()) {
            // 적절한 예외처리
        }
        User user = foundUser.get();
        return user;
    }
}
