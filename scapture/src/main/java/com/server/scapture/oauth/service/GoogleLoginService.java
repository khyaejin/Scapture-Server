package com.server.scapture.oauth.service;

import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface GoogleLoginService {
    ResponseEntity<CustomAPIResponse<?>> getAccessToken(String code, String state);

    ResponseEntity<CustomAPIResponse<?>> getUserInfo(String accessToken);

    ResponseEntity<CustomAPIResponse<?>> login(UserInfo userInfo);
}
