package com.server.scapture.oauth.service;

import com.server.scapture.domain.User;
import com.server.scapture.oauth.dto.SocialLoginResponseDto;
import com.server.scapture.oauth.dto.UserInfo;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractSocialLoginService implements SocialLoginService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSocialLoginService.class);
    protected final UserRepository userRepository;
    protected final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<CustomAPIResponse<?>> login(UserInfo userInfo) {
        Optional<User> foundUser = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId());

        String token = jwtUtil.createToken(userInfo.getProvider(), userInfo.getProviderId());
        SocialLoginResponseDto socialLoginResponseDto = SocialLoginResponseDto.builder()
                .token(token).build();

        if (foundUser.isEmpty()) {
            User user = userInfo.toEntity();
            userRepository.save(user);
            logger.info("User 회원가입 성공: {}", user.getName());
            CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(201, socialLoginResponseDto, "회원가입이 성공적으로 완료되었습니다.");
            return ResponseEntity.status(201).body(res);
        } else {
            User user = foundUser.get();
            logger.info("User 로그인 성공: {}", user.getName());
            CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, socialLoginResponseDto, "로그인이 성공적으로 완료되었습니다.");
            return ResponseEntity.status(200).body(res);
        }
    }
}
