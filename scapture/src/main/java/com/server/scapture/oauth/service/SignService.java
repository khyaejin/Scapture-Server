package com.server.scapture.oauth.service;

import com.server.scapture.domain.User;
import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface SignService {
    //카카오 소셜로그인 : 접근 토큰 받기
    ResponseEntity<CustomAPIResponse<?>> getAccessToken(String code);

    //카카오 소셜로그인 : 사용자 정보 받기
    ResponseEntity<CustomAPIResponse<?>> getUserInfo(String accessToken);

    //카카오 소셜로그인 : 로그인/회원가입
    ResponseEntity<CustomAPIResponse<?>> login(UserInfo userInfo);
}
