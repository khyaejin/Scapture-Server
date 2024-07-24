package com.server.scapture.oauth.service;

import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class KakaoLoginServiceImpl extends AbstractSocialLoginService implements KakaoLoginService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoLoginServiceImpl.class);

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoApiKey;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String reqUrl;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    public KakaoLoginServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        super(userRepository, jwtUtil);
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getAccessToken(String code, String state) {
        String accessToken;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(kakaoApiKey);
            sb.append("&client_secret=").append(kakaoClientSecret);
            sb.append("&redirect_uri=").append(kakaoRedirectUri);
            sb.append("&code=").append(code);

            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
                bw.write(sb.toString());
                bw.flush();
            }

            int responseCode = conn.getResponseCode();
            logger.info("Token Response Code: {}", responseCode);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()))) {
                String line;
                StringBuilder responseSb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseSb.append(line);
                }
                String result = responseSb.toString();
                logger.info("Token Response Body: {}", result);

                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("access_token")) {
                    accessToken = jsonObject.getString("access_token");
                } else {
                    CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401,"이미 사용되었거나 유효하지 않은 인가 코드 입니다.");
                    return ResponseEntity.status(401).body(res);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting access token", e);
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401,"접근 토큰을 받는데 실패했습니다.");
            return ResponseEntity.status(401).body(res);
        }

        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, accessToken, "접근 토큰을 성공적으로 받았습니다.");
        return ResponseEntity.status(200).body(res);
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getUserInfo(String accessToken) {
        String providerId = null;
        String provider = "kakao";
        String nickname = null;
        String email = null;
        String profileImageUrl = null;

        String reqUrl = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
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

                if (jsonObject.has("id")) {
                    providerId = String.valueOf(jsonObject.getLong("id"));
                } else {
                    logger.warn("No 'id' field in response");
                }

                if (jsonObject.has("properties")) {
                    nickname = jsonObject.getJSONObject("properties").optString("nickname", null);
                } else {
                    logger.warn("No 'properties' field in response");
                }

                if (jsonObject.has("kakao_account")) {
                    JSONObject kakaoAccount = jsonObject.getJSONObject("kakao_account");
                    email = kakaoAccount.optString("email", null);

                    if (kakaoAccount.has("profile")) {
                        JSONObject profile = kakaoAccount.getJSONObject("profile");
                        profileImageUrl = profile.optString("profile_image_url", null);
                    } else {
                        logger.warn("No 'profile' field in 'kakao_account'");
                    }
                } else {
                    logger.warn("No 'kakao_account' field in response");
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
