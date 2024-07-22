package com.server.scapture.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CreateStadiumRequestDto {
    private String name;
    private String description;
    private String location;
    private String city;
    private String state;
    private String hours;
    private Boolean isOutside;
    private String parking;
}
