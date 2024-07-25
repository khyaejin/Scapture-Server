package com.server.scapture.user.service;

import com.server.scapture.domain.Subscribe;
import com.server.scapture.domain.User;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.subscribe.repository.SubscribeRepository;
import com.server.scapture.user.dto.BananaBalanceResponseDto;
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
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 토큰에 해당하는 회원이 없을 시
        if (foundUser.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 회원이 없습니다.");
            return ResponseEntity.status(401).body(res);
        }
        User user = foundUser.get();

        Optional<Subscribe> foundSubscribe = subscribeRepository.findByUserId(user.getId());

        // 해당 회원이 구독중이 아닌 경우
        if (foundSubscribe.isEmpty()) {
            BananaBalanceResponseDto bananaBalanceResponseDto = BananaBalanceResponseDto.builder()
                    .balance(user.getBanana())
                    .isSubscribed(false)
                    .build();
            // 조회 성공, 구독중 X (200)
            CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, bananaBalanceResponseDto, "버내너 잔액 조회 완료되었습니다. 해당 회원은 구독중이 아닙니다.");
            return ResponseEntity.status(200).body(res);
        }

        // 해당 회원이 구독중인 경우
        BananaBalanceResponseDto bananaBalanceResponseDto = BananaBalanceResponseDto.builder()
                .balance(user.getBanana())
                .isSubscribed(true)
                .build();
        // 조회 성공, 구독중 O (200)
        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, bananaBalanceResponseDto, "버내너 잔액 조회 완료되었습니다. 해당 회원은 구독중입니다.");
        return ResponseEntity.status(200).body(res);
    }
}
