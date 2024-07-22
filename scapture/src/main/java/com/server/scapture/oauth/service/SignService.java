package com.server.scapture.oauth.service;

import java.util.Map;

public interface SignService {
    //카카오 소셜로그인 : 접근 토큰 받기
    String getAccessToken(String code);

    //카카오 소셜로그인 : 사용자 정보 받기
    Map<String, Object> getUserInfo(String accessToken);
}
