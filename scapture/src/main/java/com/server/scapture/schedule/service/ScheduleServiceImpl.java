package com.server.scapture.schedule.service;

import com.server.scapture.domain.Field;
import com.server.scapture.domain.Schedule;
import com.server.scapture.field.repository.FieldRepository;
import com.server.scapture.schedule.dto.CreateScheduleRequestDto;
import com.server.scapture.schedule.dto.CreateScheduleResponseDto;
import com.server.scapture.schedule.repository.ScheduleRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{
    private final ScheduleRepository scheduleRepository;
    private final FieldRepository fieldRepository;
    private LocalDateTime convert(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(date, formatter);
    }
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createSchedule(CreateScheduleRequestDto createScheduleRequestDto) {
        // 1. Field 존재 여부 검사
        Optional<Field> foundField = fieldRepository.findById(createScheduleRequestDto.getFieldId());
        // 1-1. Field 조회 불가
        if (foundField.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 구장입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. Field 조회 성공
        Field field = foundField.get();
        // 2. Schedule 생성
        // 2-1. 시간 생성
        LocalDateTime startDate = convert(createScheduleRequestDto.getStartDate());
        LocalDateTime endDate = convert(createScheduleRequestDto.getEndDate());
        System.out.println(startDate + " " + endDate);
        Schedule schedule = Schedule.builder()
                .field(field)
                .startDate(startDate)
                .endDate(endDate)
                .price(createScheduleRequestDto.getPrice())
                .isReserved(false)
                .build();
        // 3. Schedule 저장
        scheduleRepository.save(schedule);
        // 4. Response
        CreateScheduleResponseDto data = CreateScheduleResponseDto.builder()
                .scheduleId(schedule.getId())
                .build();
        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccess(HttpStatus.CREATED.value(), data,"운영 일정 생성 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
}
