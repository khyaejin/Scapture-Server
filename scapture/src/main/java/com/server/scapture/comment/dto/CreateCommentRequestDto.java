package com.server.scapture.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class CreateCommentRequestDto {
    private Long videoId;
    private String content;
}
