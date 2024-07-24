package com.server.scapture.oauth.service;

import com.server.scapture.domain.Role;
import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class NaverLoginServiceImpl implements NaverLoginService {

    private static final Logger logger = LoggerFactory.getLogger(NaverLoginServiceImpl.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverApiKey;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String reqUrl;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getAccessToken(String code, String state) {
        String accessToken = null;

        // 요청 URL 생성
        String reqUrl = String.format("https://nid.naver.com/oauth2.0/token?client_id=%s&client_secret=%s&grant_type=authorization_code&state=%s&code=%s",
                naverApiKey, naverClientSecret, state, code);

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // GET 요청 설정
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode(); // API 호출
            logger.info("Token_Response_Code: {}", responseCode);

            // 응답 처리
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()))) {
                String line;
                StringBuilder responseSb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseSb.append(line);
                }
                String result = responseSb.toString();
                logger.info("Token Response Body: {}", result);

                // JSON 파싱하여 access token 추출
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("access_token")) {
                    accessToken = jsonObject.getString("access_token");
                } else {
                    CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "이미 사용되었거나 유효하지 않은 인가 코드 입니다.");
                    return ResponseEntity.status(401).body(res);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting access token", e);
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "접근 토큰을 받는데 실패했습니다.");
            return ResponseEntity.status(401).body(res);
        }

        // 성공 200 : 엑세스 토큰을 성공적으로 받은 경우
        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, accessToken, "접근 토큰을 성공적으로 받았습니다.");
        return ResponseEntity.status(200).body(res);
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getUserInfo(String accessToken) {
        String providerId = null;
        String provider = "naver";
        String nickname = null;
        String email = null;
        String profileImageUrl = null;
        Role role = Role.BASIC;

        String reqUrl = "https://openapi.naver.com/v1/nid/me";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode(); // API 호출
            logger.info("Response_Code: {}", responseCode);

            if (responseCode == 401) { // Unauthorized - token expired or invalid
                CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401,"토큰이 만료되었거나 유효하지 않은 토큰입니다.");
                return ResponseEntity.status(401).body(res);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode <= 300 ? conn.getInputStream() : conn.getErrorStream()))) {
                String line;
                StringBuilder responseSb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseSb.append(line);
                }
                String result = responseSb.toString();
                logger.info("User Info Response Body: {}", result);

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("response")) {
                    JSONObject response = jsonObject.getJSONObject("response");

                    if (response.has("id")) { //providerId
                        providerId = response.getString("id");
                    } else {
                        logger.warn("No 'id' field in response");
                    }

                    nickname = response.optString("nickname", null);
                    email = response.optString("email", null);
                    profileImageUrl = response.optString("profile_image", null);
                } else {
                    logger.warn("No 'response' field in response");
                }
            }


        } catch (Exception e) {
            logger.error("Error getting user info", e);
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(400,"유저 정보를 가져오는데 실패했습니다.");
            return ResponseEntity.status(400).body(res);
        }

        UserInfo userInfo = UserInfo.builder()
                .provider(provider)
                .providerId(providerId)
                .name(nickname)
                .email(email)
                .image(profileImageUrl)
                .role(role)
                .build();
        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, userInfo, "엑세스 토큰을 성공적으로 받았습니다.");
        return ResponseEntity.status(200).body(res);

    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> login(UserInfo userInfo) {
        return null;
    }

}
