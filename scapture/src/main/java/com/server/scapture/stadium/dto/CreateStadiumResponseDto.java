package com.server.scapture.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class CreateStadiumResponseDto {
    private Long stadiumId;
}
