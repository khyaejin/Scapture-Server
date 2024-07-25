package com.server.scapture.comment.controller;

import com.server.scapture.comment.dto.CreateCommentRequestDto;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/comments")
public class CommentController {
    @PostMapping
    public ResponseEntity<CustomAPIResponse<?>> createComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @RequestBody CreateCommentRequestDto createCommentRequestDto) {
        return null;
    }
}
