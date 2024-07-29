package com.server.scapture.schedule.controller;

import com.server.scapture.schedule.dto.CreateScheduleRequestDto;
import com.server.scapture.schedule.service.ScheduleService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;
    @PostMapping
    public ResponseEntity<CustomAPIResponse<?>> createSchedule(@RequestBody CreateScheduleRequestDto createScheduleRequestDto) {
        return scheduleService.createSchedule(createScheduleRequestDto);
    }
}
