package com.server.scapture.oauth.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {

    private static final Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);

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
    public HashMap<String, Object> getUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqUrl = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode();
            logger.info("Response Code: {}", responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode <= 300) {
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
            logger.info("User Info Response Body: {}", result);

            // JSON 파싱
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("properties")) {
                String nickname = jsonObject.getJSONObject("properties").getString("nickname");
                userInfo.put("nickname", nickname);
            } else {
                logger.warn("No 'properties' field in response");
            }
            if (jsonObject.has("kakao_account")) {
                String email = jsonObject.getJSONObject("kakao_account").getString("email");
                userInfo.put("email", email);
            } else {
                logger.warn("No 'kakao_account' field in response");
            }

            br.close();

        } catch (Exception e) {
            logger.error("Error getting user info", e);
        }
        return userInfo;
    }
}
