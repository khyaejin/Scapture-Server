package com.server.scapture.oauth.service;

import com.server.scapture.domain.Role;
import com.server.scapture.domain.User;
import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.util.entity.OAuthException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                    throw new OAuthException("No 'access_token' field in token response");
                }
            }
        } catch (Exception e) {
            logger.error("Error getting access token", e);
            throw new OAuthException("Error getting access token");
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

            if (responseCode == 401) { // Unauthorized - token expired or invalid
                throw new OAuthException("Access token expired or invalid");
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
            throw new OAuthException("Error getting user info");
        }

        return UserInfo.builder()
                .provider(provider)
                .providerId(providerId)
                .name(nickname)
                .email(email)
                .image(profileImageUrl)
                .role(role)
                .build();
    }

    @Override
    public User login(UserInfo userInfo) {
        Optional<User> foundUser = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId());

        //회원가입
        if (foundUser.isEmpty()) {
            User user = userInfo.toEntity();
            userRepository.save(user);
            logger.info("User 회원가입 성공: {}", user.getName());
            return user;
        } else { //로그인
            User user = foundUser.get();
            logger.info("User 로그인 성공: {}", user.getName());
            return user;
        }
    }
}
