package com.server.scapture.oauth.service;

import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class GoogleLoginServiceImpl extends AbstractSocialLoginService implements GoogleLoginService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleLoginServiceImpl.class);

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    public GoogleLoginServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        super(userRepository, jwtUtil);
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getAccessToken(String code, String state) {
        String reqUrl = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();
        requestBody.put("code", code);
        requestBody.put("client_id", googleClientId);
        requestBody.put("client_secret", googleClientSecret);
        requestBody.put("redirect_uri", googleRedirectUri);
        requestBody.put("grant_type", "authorization_code");

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(reqUrl, HttpMethod.POST, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonObject = new JSONObject(response.getBody());
                if (jsonObject.has("access_token")) {
                    String accessToken = jsonObject.getString("access_token");
                    CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, accessToken, "접근 토큰을 성공적으로 받았습니다.");
                    return ResponseEntity.status(200).body(res);
                } else {
                    CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "이미 사용되었거나 유효하지 않은 인가 코드 입니다.");
                    return ResponseEntity.status(401).body(res);
                }
            } else {
                CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(response.getStatusCodeValue(), "접근 토큰을 받는데 실패했습니다.");
                return ResponseEntity.status(response.getStatusCodeValue()).body(res);
            }
        } catch (Exception e) {
            logger.error("Error getting access token", e);
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(500, "접근 토큰을 받는데 실패했습니다.");
            return ResponseEntity.status(500).body(res);
        }
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getUserInfo(String accessToken) {
        String providerId = null;
        String provider = "google";
        String nickname = null;
        String email = null;
        String profileImageUrl = null;

        String reqUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(reqUrl, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonObject = new JSONObject(response.getBody());

                providerId = jsonObject.optString("id", null);
                nickname = jsonObject.optString("name", null);
                email = jsonObject.optString("email", null);
                profileImageUrl = jsonObject.optString("picture", null);

                UserInfo userInfo = UserInfo.builder()
                        .provider(provider)
                        .providerId(providerId)
                        .name(nickname)
                        .email(email)
                        .image(profileImageUrl)
                        .build();

                CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, userInfo, "유저 정보를 성공적으로 가져왔습니다.");
                return ResponseEntity.status(200).body(res);
            } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "토큰이 만료되었거나 유효하지 않은 토큰입니다.");
                return ResponseEntity.status(401).body(res);
            } else {
                CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(response.getStatusCodeValue(), "유저 정보를 가져오는데 실패했습니다.");
                return ResponseEntity.status(response.getStatusCodeValue()).body(res);
            }
        } catch (Exception e) {
            logger.error("Error getting user info", e);
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(400, "유저 정보를 가져오는데 실패했습니다.");
            return ResponseEntity.status(400).body(res);
        }
    }

}
