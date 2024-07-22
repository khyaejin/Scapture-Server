package com.server.scapture.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class GetStadiumResponseDto {
    private Long stadiumId;
    private String name;
    private String location;
    private String hours;
    private Boolean isOutside;
    private String parking;
    private String image;
}
