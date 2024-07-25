package com.server.scapture.comment.service;

import com.server.scapture.comment.dto.CreateCommentRequestDto;
import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService{

    @Override
    public ResponseEntity<CustomAPIResponse<?>> createComment(String header, CreateCommentRequestDto createCommentRequestDto) {
        return null;
    }
}
