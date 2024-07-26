package com.server.scapture.user.service;

import com.server.scapture.domain.Subscribe;
import com.server.scapture.domain.User;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.subscribe.dto.CreateSubscribeRequestDto;
import com.server.scapture.subscribe.repository.SubscribeRepository;
import com.server.scapture.subscribe.service.SubscribeService;
import com.server.scapture.user.dto.BananaAddResponseDto;
import com.server.scapture.user.dto.BananaBalanceResponseDto;
import com.server.scapture.user.dto.SubscribeResponseDto;
import com.server.scapture.user.dto.UserProfileDto;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SubscribeRepository subscribeRepository;
    private final SubscribeService subscribeService;

    // 버내너 잔액 조회
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getBananaBalance(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 회원정보 찾을 수 없음 (401)
        if (foundUser.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 회원이 없습니다.");
            return ResponseEntity.status(404).body(res);
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


    // 버내너 충전
    @Override
    public ResponseEntity<CustomAPIResponse<?>> addBananas(String authorizationHeader, int balance) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 회원정보 찾을 수 없음 (404)
        if (foundUser.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 회원이 없습니다.");
            return ResponseEntity.status(401).body(res);
        }
        User user = foundUser.get();

        Optional<Subscribe> foundSubscribe = subscribeRepository.findByUserId(user.getId());

        // 구독중인 회원인 경우 (401)
        if (foundSubscribe.isPresent()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "구독중인 회원입니다.");
            return ResponseEntity.status(404).body(res);
        }

        // 충전 성공 (200)
        user.increaseTotalBananas(balance);
        userRepository.save(user);
        int totalBananas = user.getBanana();

        BananaAddResponseDto bananaAddResponseDto = BananaAddResponseDto.builder()
                .balance(totalBananas)
                .build();

        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, bananaAddResponseDto, "버내너가 성공적으로 충전되었습니다.");
        return ResponseEntity.status(200).body(res);
    }

    // 프로필 조회
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getProfile(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 회원정보 찾을 수 없음 (404)
        if (foundUser.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 회원이 없습니다.");
            return ResponseEntity.status(401).body(res);
        }
        User user = foundUser.get();

        subscribeService.checkRole(); // Subscribe 정보에 따른 User의 Role 확인 및 갱신

        Optional<Subscribe> foundSubscribe = subscribeRepository.findByUserId(user.getId());
        UserProfileDto userProfileDto;


        // 구독중 아닐 시
        if (foundSubscribe.isEmpty()) {
            userProfileDto = UserProfileDto.builder()
                    .name(user.getName())
                    .team(user.getTeam())
                    .location(user.getLocation())
                    .role(user.getRole())
                    .endDate(null) // 구독 만료일 null
                    .image(user.getImage())
                    .build();
        }
        // 구독중일 시
        else{
            Subscribe subscribe = foundSubscribe.get();

            userProfileDto = UserProfileDto.builder()
                    .name(user.getName())
                    .team(user.getTeam())
                    .location(user.getLocation())
                    .role(user.getRole())
                    .endDate(subscribe.convertEndDate()) // 구독 만료일 null
                    .image(user.getImage())
                    .build();
        }

        // 조회 성공(200)
        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, userProfileDto, "사용자 정보 조회 완료되었습니다.");
        return ResponseEntity.status(200).body(res);

    }

    // 구독 생성
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createSubscribe(String authorizationHeader, CreateSubscribeRequestDto createSubscribeRequestDto) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 회원정보 찾을 수 없음 (404)
        if (foundUser.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 회원이 없습니다.");
            return ResponseEntity.status(401).body(res);
        }
        User user = foundUser.get();

        subscribeService.checkRole(); // Subscribe 정보에 따른 User의 Role 확인 및 갱신

        Optional<Subscribe> foundSubscribe = subscribeRepository.findByUserId(user.getId());

        // 해당 회원이 이미 구독중인 경우(401)
        if (foundSubscribe.isPresent()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "이미 구독중인 회원입니다.");
            return ResponseEntity.status(401).body(res);
        }

        // 구독 생성 성공 (201) - 구독중이 아닌 경우
        Subscribe subscribe = Subscribe.builder()
                .user(user)
                .startDate(createSubscribeRequestDto.convert(true))
                .endDate(createSubscribeRequestDto.convert(false))
                .build();

        subscribeRepository.save(subscribe);

        SubscribeResponseDto subscribeResponseDto = SubscribeResponseDto.builder()
                .subscribeId(subscribe.getId())
                .startDate(subscribe.getStartDate())
                .endDate(subscribe.getEndDate())
                .build();

        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(201, subscribeResponseDto, "구독 생성이 완료되었습니다.");
        return ResponseEntity.status(201).body(res);
    }

    // 구독 갱신
    @Override
    public ResponseEntity<CustomAPIResponse<?>> extensionSubscribe(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 회원정보 찾을 수 없음 (404)
        if (foundUser.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 회원이 없습니다.");
            return ResponseEntity.status(401).body(res);
        }

        User user = foundUser.get();

        subscribeService.checkRole(); // Subscribe 정보에 따른 User의 Role 확인 및 갱신

        Optional<Subscribe> foundSubscribe = subscribeRepository.findByUserId(user.getId());

        // 구독중이 아닌 회원의 경우 (401)
        if (foundSubscribe.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(401, "해당 회원은 구독중이 아닙니다.");
            return ResponseEntity.status(401).body(res);
        }

        // 구독 갱신 성공 (200) - 이미 구독중인 경우
        Subscribe subscribe = foundSubscribe.get();
        LocalDateTime newEndDate = subscribe.getEndDate().plusMonths(1); // 한달 추가
        subscribe.updateEndDate(newEndDate);
        subscribeRepository.save(subscribe);

        SubscribeResponseDto subscribeResponseDto = SubscribeResponseDto.builder()
                .subscribeId(subscribe.getId())
                .startDate(subscribe.getStartDate())
                .endDate(subscribe.getEndDate())
                .build();

        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(201, subscribeResponseDto, "구독 갱신이 완료되었습니다.");
        return ResponseEntity.status(201).body(res);
    }

    // 예약 내역 조회
    @Override
    public ResponseEntity<CustomAPIResponse<?>> searchReservations(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 회원정보 찾을 수 없음 (404)
        if (foundUser.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 회원이 없습니다.");
            return ResponseEntity.status(401).body(res);
        }

        User user = foundUser.get();


        return null;
    }
}
