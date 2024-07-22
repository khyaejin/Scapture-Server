package com.server.scapture.oauth.controller;

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
        logger.info("Access Token: {}", accessToken);

        // 3. 사용자 정보 받기
        Map<String, Object> userInfo = signService.getUserInfo(accessToken);

        String email = (String) userInfo.get("email");
        String nickname = (String) userInfo.get("nickname");

        logger.info("User Email: {}", email);
        logger.info("User Nickname: {}", nickname);

        // 필요한 로직 추가
        // 4. 로그인

        // 5. jwt 토큰 발급

        return null;
    }
}
