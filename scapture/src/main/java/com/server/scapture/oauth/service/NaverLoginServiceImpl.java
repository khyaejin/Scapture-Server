package com.server.scapture.oauth.service;

import com.server.scapture.domain.Role;
import com.server.scapture.domain.User;
import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaverLoginServiceImpl implements SocialLoginService {

    private static final Logger logger = LoggerFactory.getLogger(NaverLoginServiceImpl.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<CustomAPIResponse<?>> getAccessToken(String code) {
        String naverApiKey = "Cj4QZ3AxTAmvLcDZYMUD"; //ClientId
        String naverRedirectUri = "http://localhost:3000/oauth/redirected/naver"; //등록한 콜백 url
        String accessToken;
        String reqUrl = "https://nid.naver.com/oauth2.0/token";
        String naverClientSecret = "E8Y0ugkSFA";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(naverApiKey);
            sb.append("&client_secret=").append(naverClientSecret);
            sb.append("&redirect_uri=").append(naverRedirectUri);
            sb.append("&code=").append(code);

            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
                bw.write(sb.toString());
                bw.flush(); //보내기
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
                    return ResponseEntity.status(401).body(res);                }
            }
        } catch (Exception e) {
            logger.error("Error getting access token", e);
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401,"접근 토큰을 받는데 실패했습니다.");
            return ResponseEntity.status(401).body(res);
        }

        //성공 200 : 엑세스 토큰을 성공적으로 받은 경우
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
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode();
            logger.info("Response Code: {}", responseCode);

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
        Optional<User> foundUser = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId());

        // JWT 토큰 생성
        String token = jwtUtil.createToken(userInfo.getProvider(), userInfo.getProviderId());

        //회원가입
        if (foundUser.isEmpty()) {
            User user = userInfo.toEntity();
            userRepository.save(user);
            logger.info("User 회원가입 성공: {}", user.getName());
            CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(201, token, "네이버 회원가입이 성공적으로 완료되었습니다.");
            return ResponseEntity.status(201).body(res);
        } else { //로그인
            User user = foundUser.get();
            logger.info("User 로그인 성공: {}", user.getName());
            CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, token, "네이버 로그인이 성공적으로 완료되었습니다.");
            return ResponseEntity.status(200).body(res);        }
    }
}
