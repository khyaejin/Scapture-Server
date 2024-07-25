package com.server.scapture.reservation.service;

import com.server.scapture.domain.Reservation;
import com.server.scapture.domain.Schedule;
import com.server.scapture.domain.User;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.reservation.repository.ReservationRepository;
import com.server.scapture.schedule.repository.ScheduleRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{
    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final JwtUtil jwtUtil;
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createReservation(String header, Long scheduleId) {
        // 1. 사용자 조회
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(header);
        // 1-1. 실패
        if (foundUser.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 사용자입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. 성공
        User user = foundUser.get();
        // 2. 운영 일정 조회
        Optional<Schedule> foundSchedule = scheduleRepository.findById(scheduleId);
        // 2-1. 실패
        if (foundSchedule.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 운영 일정입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 2-2. 성공
        Schedule schedule = foundSchedule.get();
        // 3. 에약 생성
        // 3-1. 중복 조회
        if (reservationRepository.findByScheduleAndUser(schedule, user).isPresent()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.CONFLICT.value(), "이미 존재하는 예약입니다.");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(responseBody);
        }
        // 3-2. 예약 저장
        Reservation reservation = Reservation.builder()
                .user(user)
                .schedule(schedule)
                .build();
        reservationRepository.save(reservation);
        // 4. Response
        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.CREATED.value(), "예약 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getReservation(String header, Long scheduleId, String date) {
        return null;
    }
}
