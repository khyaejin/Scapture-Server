package com.server.scapture.schedule.service;

import com.server.scapture.schedule.dto.CreateScheduleRequestDto;
import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ScheduleService {
    ResponseEntity<CustomAPIResponse<?>> createSchedule(List<CreateScheduleRequestDto> createScheduleRequestDtos);
}
