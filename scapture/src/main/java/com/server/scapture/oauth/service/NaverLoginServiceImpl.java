package com.server.scapture.oauth.service;

import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class NaverLoginServiceImpl extends AbstractSocialLoginService implements NaverLoginService {

    private static final Logger logger = LoggerFactory.getLogger(NaverLoginServiceImpl.class);

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverApiKey;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String reqUrl;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    public NaverLoginServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        super(userRepository, jwtUtil);
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getAccessToken(String code, String state) {
        // GET 요청을 통해 URL 파라미터로 필요한 정보를 전송해야 함
        String reqUrl = String.format("https://nid.naver.com/oauth2.0/token?client_id=%s&client_secret=%s&grant_type=authorization_code&state=%s&code=%s",
                naverApiKey, naverClientSecret, state, code);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(reqUrl, HttpMethod.GET, request, String.class);
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
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "접근 토큰을 받는데 실패했습니다.");
            return ResponseEntity.status(401).body(res);
        }
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getUserInfo(String accessToken) {
        String providerId = null;
        String provider = "naver";
        String nickname = null;
        String email = null;
        String profileImageUrl = null;

        String reqUrl = "https://openapi.naver.com/v1/nid/me";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode();
            logger.info("Response Code: {}", responseCode);

            if (responseCode == 401) {
                CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "토큰이 만료되었거나 유효하지 않은 토큰입니다.");
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

                    if (response.has("id")) {
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
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(400, "유저 정보를 가져오는데 실패했습니다.");
            return ResponseEntity.status(400).body(res);
        }

        UserInfo userInfo = UserInfo.builder()
                .provider(provider)
                .providerId(providerId)
                .name(nickname)
                .email(email)
                .image(profileImageUrl)
                .build();
        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, userInfo, "유저 정보를 성공적으로 가져왔습니다.");
        return ResponseEntity.status(200).body(res);
    }
    //login()은 모든 소셜 로그인에서 공통됨 -> 추상 클래스로 구현
}
