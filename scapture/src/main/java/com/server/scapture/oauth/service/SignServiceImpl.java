package com.server.scapture.oauth.service;

import com.server.scapture.domain.Role;
import com.server.scapture.domain.User;
import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private static final Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    public String getAccessToken(String code) {
        String kakaoApiKey = "024871f91fe647ce7262bd022bd1afc2";
        String kakaoRedirectUri = "http://localhost:3000/oauth/redirected/kakao";
        String accessToken = "";
        String reqUrl = "https://kauth.kakao.com/oauth/token";
        String kakaoClientSecret = "8hIjcRxQfSSvvG8NV1nuksp9k2c9PEUP";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 필수 헤더 세팅
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true); // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.

            // 필수 쿼리 파라미터 세팅
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(kakaoApiKey);
            sb.append("&client_secret=").append(kakaoClientSecret);
            sb.append("&redirect_uri=").append(kakaoRedirectUri);
            sb.append("&code=").append(code);

            // POST 데이터 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            bw.write(sb.toString());
            bw.flush();

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            logger.info("Token Response Code: {}", responseCode);

            // 응답 읽기
            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            StringBuilder responseSb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseSb.append(line);
            }
            String result = responseSb.toString();
            logger.info("Token Response Body: {}", result);

            // JSON 파싱
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("access_token")) {
                accessToken = jsonObject.getString("access_token");
            } else {
                logger.warn("No 'access_token' field in token response");
            }

            br.close();
            bw.close();
        } catch (Exception e) {
            logger.error("Error getting access token", e);
        }
        return accessToken;
    }


    @Override
    public UserInfo getUserInfo(String accessToken) {
        String providerId = null;
        String provider = "kakao";
        String nickname = null;
        String email = null;
        String profileImageUrl = null;
        Role role = Role.BASIC;

        String reqUrl = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode();
            logger.info("Response Code: {}", responseCode);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode <= 300 ? conn.getInputStream() : conn.getErrorStream()))) {
                String line;
                StringBuilder responseSb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseSb.append(line);
                }
                String result = responseSb.toString();
                logger.info("User Info Response Body: {}", result);

                // JSON 파싱
                JSONObject jsonObject = new JSONObject(result);

                // ID 가져오기
                if (jsonObject.has("id")) {
                    providerId = String.valueOf(jsonObject.getLong("id"));
                } else {
                    logger.warn("No 'id' field in response");
                }

                // 닉네임 가져오기
                if (jsonObject.has("properties")) {
                    nickname = jsonObject.getJSONObject("properties").getString("nickname");
                } else {
                    logger.warn("No 'properties' field in response");
                }

                // 이메일 가져오기
                if (jsonObject.has("kakao_account")) {
                    JSONObject kakaoAccount = jsonObject.getJSONObject("kakao_account");
                    if (kakaoAccount.has("email")) {
                        email = kakaoAccount.getString("email");
                    } else {
                        logger.warn("No 'email' field in 'kakao_account'");
                    }

                    // 프로필 이미지 가져오기
                    if (kakaoAccount.has("profile")) {
                        JSONObject profile = kakaoAccount.getJSONObject("profile");
                        if (profile.has("profile_image_url")) {
                            profileImageUrl = profile.getString("profile_image_url");
                        } else {
                            logger.warn("No 'profile_image_url' field in 'profile'");
                        }
                    } else {
                        logger.warn("No 'profile' field in 'kakao_account'");
                    }
                } else {
                    logger.warn("No 'kakao_account' field in response");
                }
            }

        } catch (Exception e) {
            logger.error("Error getting user info", e);
        }

        return UserInfo.builder()
                .providerId(providerId)
                .name(nickname)
                .email(email)
                .image(profileImageUrl)
                .role(role)
                .build();
    }

    //카카오 소셜로그인 : 로그인/회원가입
    @Override
    public User login(UserInfo userInfo) {
        // 해당 provider, providerId를 가진 회원이 존재하는가 판별
        String provider = userInfo.getProvider();
        String providerId = userInfo.getProviderId();

        Optional<User> foundUser = userRepository.findByProviderAndProviderId(provider, providerId);

        // 회원가입 : 해당 회원이 존재하지 않을 시 -> 새로운 User 생성 후 리턴
        if (foundUser.isEmpty()) {
            User user = userInfo.toEntity();
            userRepository.save(user);
            return user;
        }

        // 로그인 : 해당 회원이 존재할 시 -> 기존 User 리턴
        return foundUser.get();
    }
}
