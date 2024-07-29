package com.server.scapture.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@Getter @Setter
@AllArgsConstructor
@Builder
public class CreateScheduleRequestDto {
    private Long fieldId;
    private String startDate;
    private String endDate;
    private int price;
}
