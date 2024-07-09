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
    private String title;
    private String place;
    private List<String> videoUrl;
}
