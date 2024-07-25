package com.server.scapture.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@Builder
public class SortReservationDto {
    private int index;
    private LocalDateTime startDate;
    private Long scheduleId;
    private String name;
    private String type;
    private String hours;
    private String date;
    private boolean isReserved;
    private int price;
}
