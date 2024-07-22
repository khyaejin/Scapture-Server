package com.server.scapture.oauth.controller;

import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.oauth.service.SignService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/oauth")
@RequiredArgsConstructor
public class SignController {
    private static final Logger logger = LoggerFactory.getLogger(SignController.class);
    private final SignService signService;

    //카카오 소셜 로그인
    @PostMapping(value = "/social/kakao")
    public ResponseEntity<CustomAPIResponse<?>> kakaoLogin(@RequestParam String code) {
        // 1. 인가 코드 받기 (@RequestParam String code)

        // 2. 접근 토큰 받기
        String accessToken = signService.getAccessToken(code);

            //테스트 용도
            logger.info("Access Token: {}", accessToken);

        // 3. 사용자 정보 받기
        UserInfo userInfo = signService.getUserInfo(accessToken);

            //log를 통한 테스트 용도

            logger.info("User_Email: {}", userInfo.getEmail());
            logger.info("User_Nickname: {}", userInfo.getNickname());
            logger.info("User_Id: {}", userInfo.getId());
            logger.info("User_ProfileImage: {}", userInfo.getProfileImage());

        // 4. 로그인

        // 5. jwt 토큰 발급

        return null;
    }
}
