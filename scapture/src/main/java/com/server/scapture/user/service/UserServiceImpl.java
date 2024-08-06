package com.server.scapture.user.service;

import com.server.scapture.domain.*;
import com.server.scapture.field.repository.FieldRepository;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.reservation.repository.ReservationRepository;
import com.server.scapture.schedule.repository.ScheduleRepository;
import com.server.scapture.subscribe.dto.CreateSubscribeRequestDto;
import com.server.scapture.subscribe.repository.SubscribeRepository;
import com.server.scapture.subscribe.service.SubscribeService;
import com.server.scapture.user.dto.*;
import com.server.scapture.user.repository.UserRepository;
import com.server.scapture.util.S3.S3Service;
import com.server.scapture.util.date.DateUtil;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SubscribeRepository subscribeRepository;
    private final SubscribeService subscribeService;
    private final ReservationRepository reservationRepository;
    private final S3Service s3Service;

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

        subscribeService.checkRole(); // Subscribe 정보에 따른 User의 Role 확인 및 갱신

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
        ProfileViewDto profileViewDto;

        // 구독중 아닐 시
        if (foundSubscribe.isEmpty()) {
            profileViewDto = ProfileViewDto.builder()
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
            profileViewDto = ProfileViewDto.builder()
                    .name(user.getName())
                    .team(user.getTeam())
                    .location(user.getLocation())
                    .role(user.getRole())
                    .endDate(subscribe.convertEndDate()) // 구독 만료일 null
                    .image(user.getImage())
                    .build();
        }

        // 조회 성공(200)
        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, profileViewDto, "사용자 정보 조회 완료되었습니다.");
        return ResponseEntity.status(200).body(res);

    }

    // 프로필 수정
    public ResponseEntity<CustomAPIResponse<?>> editProfile(String authorizationHeader, ProfileEditDto profileEditDto, MultipartFile image) throws IOException {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 회원정보 찾을 수 없음 (404)
        if (foundUser.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 회원이 없습니다.");
            return ResponseEntity.status(404).body(res);
        }
        User user = foundUser.get();

        // 프로필 정보 업데이트
        user.editProfileWithoutImage(profileEditDto.getName(), profileEditDto.getTeam(), profileEditDto.getLocation());

        // 프로필 이미지 수정 있을 시
        if (image != null && !image.isEmpty()) {
            String imageName = String.valueOf(user.getId()); // 프로필 사진의 이름은 유저의 pk를 이용(한 유저당 하나의 프로필 사진)
            String imageUrl = s3Service.modifyUserImage(image, imageName);
            user.editProfileImage(imageUrl);
        }

        userRepository.save(user);

        // 프로필 편집 성공 (200) - 검증을 위해 save한 user를 가지고 다시 profileEditDto 생성
        ProfileEditDto updatedProfileEditDto = ProfileEditDto.builder()
                .name(user.getName())
                .team(user.getTeam())
                .location(user.getLocation())
                .image(user.getImage())
                .build();

        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, updatedProfileEditDto, "프로필이 성공적으로 업데이트되었습니다.");
        return ResponseEntity.status(200).body(res);
    }

    // 구독 관리 (생성/갱신)
    @Override
    public ResponseEntity<CustomAPIResponse<?>> manageSubscribe(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        // 회원정보 찾을 수 없음 (404)
        if (foundUser.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 회원이 없습니다.");
            return ResponseEntity.status(401).body(res);
        }
        User user = foundUser.get();

        subscribeService.checkRole(); // Subscribe 정보에 따른 User의 Role 확인 및 갱신

        Optional<Subscribe> foundSubscribe = subscribeRepository.findByUserId(user.getId());

        // 이미 구독중인 경우 -> 구독 갱신
        if (foundSubscribe.isPresent()) {
            Subscribe subscribe = foundSubscribe.get();
            LocalDateTime newEndDate = subscribe.getEndDate().plusMonths(1); // 한달 추가
            subscribe.updateEndDate(newEndDate);
            subscribeRepository.save(subscribe);

          /*  // yyyy.MM.dd 형태로 변환
            LocalDateTime startDateTime = subscribe.getStartDate();
            String startDate = startDateTime.format(formatter);
            LocalDateTime endDateTime = subscribe.getEndDate();
            String endDate = endDateTime.format(formatter);*/

            SubscribeResponseDto subscribeResponseDto = SubscribeResponseDto.builder()
                    .subscribeId(subscribe.getId())
                    .startDate(subscribe.getStartDate())
                    .endDate(subscribe.getEndDate())
                    .build();

            CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(201, subscribeResponseDto, "구독 갱신이 완료되었습니다.");
            return ResponseEntity.status(201).body(res);
        }

        // 구독중이 아닌 경우 -> 구독 생성
        LocalDateTime startDateTime = LocalDateTime.now(); // 현재시간
        LocalDateTime endDateTime = startDateTime.plusMonths(1); // 한 달 추가

        Subscribe subscribe = Subscribe.builder()
                .user(user)
                .startDate(startDateTime)
                .endDate(endDateTime)
                .build();

        subscribeRepository.save(subscribe);

       /* // yyyy.MM.dd 형태로 변환
        startDateTime = subscribe.getStartDate();
        String startDate = startDateTime.format(formatter);
        endDateTime = subscribe.getEndDate();
        String endDate = endDateTime.format(formatter);*/

        SubscribeResponseDto subscribeResponseDto = SubscribeResponseDto.builder()
                .subscribeId(subscribe.getId())
                .startDate(subscribe.getStartDate())
                .endDate(subscribe.getEndDate())
                .build();

        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(201, subscribeResponseDto, "구독 생성이 완료되었습니다.");
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

        // 해당 회원의 예약정보 불러오기
        List<Reservation> reservations = reservationRepository.findByUser(user);

        // 조회 성공 - 예약정보 존재하지 않는 경우 (200)
        if (reservations.isEmpty()) {
            CustomAPIResponse<?> res = CustomAPIResponse.createFailWithoutData(200, "아직 예약 내역이 없음");
            return ResponseEntity.status(200).body(res);
        }

        // 조회 성공 - 예약정보 존재하는 경우 (200)
        List<ReservationResponseDto> data = new ArrayList<>();

        for (Reservation reservation : reservations) {
            Schedule schedule = reservation.getSchedule();
            String date = DateUtil.formatLocalDateTimeWithKoreanDay(schedule.getStartDate()); // date 형식 변환 -> 2024.07.18(목)

            Field field = schedule.getField();
            Stadium stadium = field.getStadium();
            String name = stadium.getName() + " " + field.getName();

            ReservationResponseDto dto = ReservationResponseDto.builder()
                    .date(date)
                    .name(name)
                    .hours(stadium.getHours())
                    .build();
            data.add(dto);
        }

        CustomAPIResponse<?> res = CustomAPIResponse.createSuccess(200, data, "예약 내역 조회 완료되었습니다.");
        return ResponseEntity.status(200).body(res);
    }
}
