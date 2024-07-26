package com.server.scapture.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReservationResponseDto {
    private String date;
    private String name;
    private String hours;
    //"date" : "2024.07.18",
    //			"name" : "장충 체육관 A구장",
    //			"hours" : "20:00 ~ 22:00"
}
