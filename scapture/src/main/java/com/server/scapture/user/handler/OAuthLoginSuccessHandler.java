//package com.server.scapture.user.handler;
//
//import com.server.scapture.domain.User;
//import com.server.scapture.user.JwtUtil;
//import com.server.scapture.user.dto.GoogleUserInfo;
//import com.server.scapture.user.dto.KakaoUserInfo;
//import com.server.scapture.user.dto.NaverUserInfo;
//import com.server.scapture.user.dto.OAuth2UserInfo;
//import com.server.scapture.user.repository.UserRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.server.scapture.util.response.CustomAPIResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Slf4j
//@Component
//public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JwtUtil jwtUtil;
//    private final UserRepository userRepository;
//    private final String redirectUri;
//    private final long accessTokenExpirationTime;
//
//    public OAuthLoginSuccessHandler(
//            JwtUtil jwtUtil,
//            UserRepository userRepository,
//            @Value("${jwt.redirect}") String redirectUri,
//            @Value("${jwt.access-token.expiration-time}") long accessTokenExpirationTime) {
//        this.jwtUtil = jwtUtil;
//        this.userRepository = userRepository;
//        this.redirectUri = redirectUri;
//        this.accessTokenExpirationTime = accessTokenExpirationTime;
//    }
//
//    private OAuth2UserInfo oAuth2UserInfo = null;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication; // 토큰
//        final String provider = token.getAuthorizedClientRegistrationId(); // provider 추출
//
//        // 구글 || 카카오 || 네이버 로그인 요청
//        switch (provider) {
//            case "google" -> {
//                log.info("구글 로그인 요청");
//                oAuth2UserInfo = new GoogleUserInfo(token.getPrincipal().getAttributes());
//            }
//            case "kakao" -> {
//                log.info("카카오 로그인 요청");
//                oAuth2UserInfo = new KakaoUserInfo(token.getPrincipal().getAttributes());
//            }
//            case "naver" -> {
//                log.info("네이버 로그인 요청");
//                oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) token.getPrincipal().getAttributes().get("response"));
//            }
//        }
//
//        // 정보 추출
//        String providerId = oAuth2UserInfo.getProviderId();
//        String name = oAuth2UserInfo.getName();
//
//        User existUser = userRepository.findByProviderId(providerId);
//        User user;
//
//        if (existUser == null) {
//            // 신규 유저인 경우
//            log.info("신규 유저입니다. 등록을 진행합니다.");
//
//            user = User.builder()
//                    .name(name)
//                    .provider(provider)
//                    .providerId(providerId)
//                    .build();
//            userRepository.save(user);
//        } else {
//            // 기존 유저인 경우
//            log.info("기존 유저입니다.");
//            user = existUser;
//        }
//
//        log.info("유저 이름 : {}", name);
//        log.info("PROVIDER : {}", provider);
//        log.info("PROVIDER_ID : {}", providerId);
//
//        // 액세스 토큰 발급
//        String accessToken = jwtUtil.generateAccessToken(user.getId(), accessTokenExpirationTime);
//
//        // 커스텀 응답 생성
//        CustomAPIResponse<Map<String, String>> customResponse = CustomAPIResponse.createSuccess(200, Map.of("token", accessToken), "카카오톡 로그인이 성공적으로 완료되었습니다.");
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        ObjectMapper objectMapper = new ObjectMapper();
//        response.getWriter().write(objectMapper.writeValueAsString(customResponse));
//    }
//}
