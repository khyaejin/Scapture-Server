package com.server.scapture.reservation.service;

import com.server.scapture.domain.*;
import com.server.scapture.field.repository.FieldRepository;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.reservation.dto.GetReservationResponseDto;
import com.server.scapture.reservation.dto.SortReservationDto;
import com.server.scapture.reservation.repository.ReservationRepository;
import com.server.scapture.schedule.repository.ScheduleRepository;
import com.server.scapture.stadium.repository.StadiumRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{
    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final FieldRepository fieldRepository;
    private final StadiumRepository stadiumRepository;
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
    public ResponseEntity<CustomAPIResponse<?>> getReservation(Long stadiumId, Long fieldId, String date) {
        // 1. 경기장 조회
        Optional<Stadium> foundStadium = stadiumRepository.findById(stadiumId);
        // 1-1. 실패
        if (foundStadium.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 경기장입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. 성공
        Stadium stadium = foundStadium.get();
        // 2. 날짜 검증
        // 2-1. 요청 값 LocalDate로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(date, formatter);
        // 2-2. 기한(1주일) 내 날짜 확인
        LocalDate startDate =  LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(8);
        log.info("시작 날짜 : {} , 종료 날짜: {}", startDate, endDate);
        // 2-3. 검증 실패(1주일 후가 아닌 다른 요청 날짜)
        if (!(parsedDate.isAfter(startDate) && parsedDate.isBefore(endDate))) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.FORBIDDEN.value(), "허용되지 않는 날짜입니다.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(responseBody);
        }
        // 3. 경기장 예약 정보 조회(전체)
        // 3-1. responseDto
        List<SortReservationDto> sortedList = new ArrayList<>();
        // 3-2. 구장 조회
        List<Field> fieldList = fieldRepository.findByStadium(stadium);
        // 3-3. 운영 일정 조회
        int index = 0;
        for (Field field : fieldList) {
            List<Schedule> scheduleList = scheduleRepository.findScheduleByFieldBetweenMonthAndDay(field, parsedDate);
            for (Schedule schedule : scheduleList) {
                SortReservationDto dto = SortReservationDto.builder()
                        .index(index)
                        .startDate(schedule.getStartDate())
                        .scheduleId(schedule.getId())
                        .name(field.getName())
                        .type(field.getType())
                        .hours(schedule.convertHourAndMin())
                        .date(schedule.convertAll())
                        .isReserved(schedule.getIsReserved())
                        .price(schedule.getPrice())
                        .build();
                sortedList.add(dto);
            }
            index++;
        }
        // 4. data 0
        if (sortedList.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.OK.value(), "예약 조회 완료되었습니다.");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseBody);
        }
        // 5. 시간순 정렬
        sortedList.sort(new Comparator<SortReservationDto>() {
            @Override
            public int compare(SortReservationDto o1, SortReservationDto o2) {
                if(o1.getStartDate().equals(o2.getStartDate())) return o1.getIndex() - o2.getIndex();
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        // 6. 같은 시간끼리 리스트 만들기
        // 6-1. data
        List<List<GetReservationResponseDto>> data = new ArrayList<>();
        // 6-2. 같은 시간 리스트업
        List<GetReservationResponseDto> responseDtoList = new ArrayList<>();
        LocalDateTime targetTime = sortedList.get(0).getStartDate();
        for (SortReservationDto sortReservationDto : sortedList) {
            // 6-2-1. responseDto
            GetReservationResponseDto responseDto = GetReservationResponseDto.builder()
                    .scheduleId(sortReservationDto.getScheduleId())
                    .name(sortReservationDto.getName())
                    .type(sortReservationDto.getType())
                    .hours(sortReservationDto.getHours())
                    .date(sortReservationDto.getDate())
                    .isReserved(sortReservationDto.isReserved())
                    .price(sortReservationDto.getPrice())
                    .build();
            // 6-2-1. 같은 시간 리스트업
            if (!targetTime.equals(sortReservationDto.getStartDate())) {
                data.add(responseDtoList);
                responseDtoList = new ArrayList<>();
                targetTime = sortReservationDto.getStartDate();
            }
            responseDtoList.add(responseDto);
        }
        // 7. Response
        CustomAPIResponse<List<List<GetReservationResponseDto>>> responseBody = CustomAPIResponse.createSuccess(HttpStatus.OK.value(), data, "예약 조회 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }

}
