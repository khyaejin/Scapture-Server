package com.server.scapture.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class GetReservationResponseDto {
    private Long scheduleId;
    private String name;
    private String type;
    private String hours;
    private String date;
    private Boolean isReserved;
    private int price;
}
