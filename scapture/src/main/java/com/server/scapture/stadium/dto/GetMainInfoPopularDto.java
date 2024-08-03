package com.server.scapture.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class GetMainInfoPopularDto {
    private Long videoId;
    private String image;
    private String stadiumName;
    private String date;
    private String hours;
}
