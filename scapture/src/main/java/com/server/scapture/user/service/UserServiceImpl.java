package com.server.scapture.user.service;

import com.server.scapture.domain.Subscribe;
import com.server.scapture.domain.User;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.subscribe.repository.SubscribeRepository;
import com.server.scapture.user.dto.BananaGetDetailDto;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SubscribeRepository subscribeRepository;

    // 버내너 잔액 조회
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getBananaBalance(String authorizationHeader) {
        Optional<User> foundUserByToken = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 토큰에 해당하는 회원이 없을 시
        if (foundUserByToken.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "유효하지 않은 토큰이거나, 토큰으로 유저 정보를 조회하는데 실패하였습니다.");
            return ResponseEntity.status(401).body(res);
        }

        User user = foundUserByToken.get();



        return null;
    }
}
