package com.server.scapture.oauth.service;

import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

//소셜로그인 공통 인터페이스
public interface SocialLoginService {
    //접근 토큰 받기
    ResponseEntity<CustomAPIResponse<?>> getAccessToken(String code, String state);

    //사용자 정보 받기
    ResponseEntity<CustomAPIResponse<?>> getUserInfo(String accessToken);

    //로그인/회원가입
    ResponseEntity<CustomAPIResponse<?>> login(UserInfo userInfo);

}