package com.server.scapture.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class GetVideosByLikeCountResponseDto {
    private Long videoId;
    private String name;
    private String image;
    private String stadiumName;
    private String date;
    private int likeCount;
    private int views;
}
