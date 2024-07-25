package com.server.scapture.comment.service;

import com.server.scapture.comment.dto.CreateCommentRequestDto;
import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface CommentService {
    ResponseEntity<CustomAPIResponse<?>> createComment(String header, CreateCommentRequestDto createCommentRequestDto);
    ResponseEntity<CustomAPIResponse<?>> createCommentLike(String header, Long commentId);
}
