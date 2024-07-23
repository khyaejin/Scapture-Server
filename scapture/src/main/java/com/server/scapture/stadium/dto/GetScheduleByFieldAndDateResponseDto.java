package com.server.scapture.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class GetScheduleByFieldAndDateResponseDto {
    private Long scheduleId;
    private String hours;
    private int videoCount;
}
