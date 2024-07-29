package com.server.scapture.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class GetCommentResponseDto {
    private Long commentId;
    private String name;
    private String image;
    private String content;
    private Boolean isLiked;
    private int likeCount;
}
