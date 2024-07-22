package com.server.scapture.schedule.service;

import com.server.scapture.domain.Field;
import com.server.scapture.domain.Schedule;
import com.server.scapture.field.repository.FieldRepository;
import com.server.scapture.schedule.dto.CreateScheduleRequestDto;
import com.server.scapture.schedule.repository.ScheduleRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{
    private final ScheduleRepository scheduleRepository;
    private final FieldRepository fieldRepository;
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createSchedule(List<CreateScheduleRequestDto> createScheduleRequestDtos) {
        for (CreateScheduleRequestDto createScheduleRequestDto : createScheduleRequestDtos) {
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
            Schedule schedule = Schedule.builder()
                    .field(field)
                    .startDate(createScheduleRequestDto.convert(true))
                    .endDate(createScheduleRequestDto.convert(false))
                    .price(createScheduleRequestDto.getPrice())
                    .build();
            // 3. Schedule 저장
            scheduleRepository.save(schedule);
        }
        // 4. Response
        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.CREATED.value(), "운영 일정 생성 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
}
