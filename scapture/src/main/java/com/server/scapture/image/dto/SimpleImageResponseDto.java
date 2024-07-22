package com.server.scapture.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class SimpleImageResponseDto {
    private Long imageId;
    private String image;
}
