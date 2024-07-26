package com.server.scapture.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SubscribeResponseDto {

    private Long subscribeId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
