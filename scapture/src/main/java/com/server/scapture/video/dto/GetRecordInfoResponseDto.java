package com.server.scapture.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@Builder
public class GetRecordInfoResponseDto {
    private String stadiumName;
    private String fieldName;
    private String date;
    private List<String> ipList;
}
