package com.server.scapture.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class VideoGetResponseDto {
    private String title;
    private String place;
    private String url;
    private String createdAt;
}
