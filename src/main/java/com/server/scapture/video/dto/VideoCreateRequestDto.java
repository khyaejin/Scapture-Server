package com.server.scapture.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class VideoCreateRequestDto {
//    private Long scheduleId;
    private String name;
    private String image;
    private String video;
}
