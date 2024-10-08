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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
        // Kakao OAuth2 토큰 요청 URL
        String reqUrl = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 요청 헤더에 ContentType을 application/x-www-form-urlencoded으로 설정

        // 요청 본문에 필요한 파라미터 설정
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", kakaoApiKey);
        requestBody.add("client_secret", kakaoClientSecret);
        requestBody.add("redirect_uri", kakaoRedirectUri);
        requestBody.add("code", code);

        // 요청 본문을 포함한 HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            // Kakao OAuth2 서버로 POST 요청 전송
            ResponseEntity<String> response = restTemplate.exchange(reqUrl, HttpMethod.POST, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // 응답을 JSON 객체로 변환
                JSONObject jsonObject = new JSONObject(response.getBody());
                if (jsonObject.has("access_token")) {
                    // access_token 추출
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
        // Kakao 사용자 정보 요청 URL
        String reqUrl = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 요청 헤더에 ContentTpye을 application/x-www-form-urlencoded으로 설정
        headers.set("Authorization", "Bearer " + accessToken); // 요청 헤더에 Authorization 토큰 설정

        // 요청 헤더를 포함한 HttpEntity 생성
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            // Kakao 서버로 POST 요청 전송
            ResponseEntity<String> response = restTemplate.exchange(reqUrl, HttpMethod.POST, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // 응답 본문을 JSON 객체로 변환
                JSONObject jsonObject = new JSONObject(response.getBody());

                // 사용자 정보 추출
                String providerId = jsonObject.optString("id", null);
                String provider = "kakao";
                String nickname = null;
                String email = null;
                String profileImageUrl = null;

                if (jsonObject.has("properties")) {
                    JSONObject properties = jsonObject.getJSONObject("properties");
                    nickname = properties.optString("nickname", null);
                }

                if (jsonObject.has("kakao_account")) {
                    JSONObject kakaoAccount = jsonObject.getJSONObject("kakao_account");
                    email = kakaoAccount.optString("email", null);

                    if (kakaoAccount.has("profile")) {
                        JSONObject profile = kakaoAccount.getJSONObject("profile");
                        profileImageUrl = profile.optString("profile_image_url", null);
                    }
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

    //login()은 모든 소셜 로그인에서 공통 -> 추상 클래스로 구현
}
