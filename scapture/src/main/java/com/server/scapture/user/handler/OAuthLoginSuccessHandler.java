package com.server.scapture.user.handler;

import com.server.scapture.domain.User;
import com.server.scapture.user.JwtUtil;
import com.server.scapture.user.dto.GoogleUserInfo;
import com.server.scapture.user.dto.KakaoUserInfo;
import com.server.scapture.user.dto.NaverUserInfo;
import com.server.scapture.user.dto.OAuth2UserInfo;
import com.server.scapture.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${jwt.redirect}")
    private String REDIRECT_URI; // 프론트엔드로 Jwt 토큰을 리다이렉트할 URI

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME; // 액세스 토큰 유효기간

    private OAuth2UserInfo oAuth2UserInfo = null;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication; // 토큰
        final String provider = token.getAuthorizedClientRegistrationId(); // provider 추출

        // 구글 || 카카오 || 네이버 로그인 요청
        switch (provider) {
            case "google" -> {
                log.info("구글 로그인 요청");
                oAuth2UserInfo = new GoogleUserInfo(token.getPrincipal().getAttributes());
            }
            case "kakao" -> {
                log.info("카카오 로그인 요청");
                oAuth2UserInfo = new KakaoUserInfo(token.getPrincipal().getAttributes());
            }
            case "naver" -> {
                log.info("네이버 로그인 요청");
                oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) token.getPrincipal().getAttributes().get("response"));
            }
        }

        // 정보 추출
        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();

        User existUser = userRepository.findByProviderId(providerId);
        User user;

        if (existUser == null) {
            // 신규 유저인 경우
            log.info("신규 유저입니다. 등록을 진행합니다.");

            user = User.builder()
                    .name(name)
                    .provider(provider)
                    .providerId(Integer.parseInt(providerId))
                    .build();
            userRepository.save(user);
        } else {
            // 기존 유저인 경우
            log.info("기존 유저입니다.");
            user = existUser;
        }

        log.info("유저 이름 : {}", name);
        log.info("PROVIDER : {}", provider);
        log.info("PROVIDER_ID : {}", providerId);

        // 액세스 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user.getId(), ACCESS_TOKEN_EXPIRATION_TIME);

        // 이름과 액세스 토큰을 담아 리다이렉트
        String encodedName = URLEncoder.encode(name, "UTF-8");
        String redirectUri = String.format(REDIRECT_URI, encodedName, accessToken);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
