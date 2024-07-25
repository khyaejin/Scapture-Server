package com.server.scapture.comment.service;

import com.server.scapture.comment.dto.CreateCommentRequestDto;
import com.server.scapture.comment.repository.CommentRepository;
import com.server.scapture.commentLike.repository.CommentLikeRepository;
import com.server.scapture.domain.Comment;
import com.server.scapture.domain.CommentLike;
import com.server.scapture.domain.User;
import com.server.scapture.domain.Video;
import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.util.response.CustomAPIResponse;
import com.server.scapture.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<CustomAPIResponse<?>> createComment(String header, CreateCommentRequestDto createCommentRequestDto) {
        // 1. 사용자 조회
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(header);
        // 1-1. 실패
        if (foundUser.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 사용자입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. 성공
        User user = foundUser.get();
        // 2. Video 조회
        Optional<Video> foundVideo = videoRepository.findById(createCommentRequestDto.getVideoId());
        // 2-1. 실패
        if (foundVideo.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 영상입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 2-2. 성공
        Video video = foundVideo.get();
        // 3. Comment 저장
        Comment comment = Comment.builder()
                .user(user)
                .video(video)
                .content(createCommentRequestDto.getContent())
                .likeCount(0)
                .build();
        commentRepository.save(comment);
        // 4. Response
        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.CREATED.value(), "댓글 작성 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createCommentLike(String header, Long commentId) {
        // 1. 사용자 조회
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(header);
        // 1-1. 실패
        if (foundUser.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 사용자입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. 성공
        User user = foundUser.get();
        // 2. 댓글 조회
        Optional<Comment> foundComment = commentRepository.findById(commentId);
        // 2-1. 실패
        if (foundComment.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 댓글입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 2-2. 성공
        Comment comment = foundComment.get();
        // 3. 댓글 좋아요 생성
        // 3-1. 중복 검사
        if (commentLikeRepository.findByCommentAndUser(comment, user).isPresent()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.CONFLICT.value(), "이미 존재하는 좋아요입니다.");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(responseBody);
        }
        // 3-2. 객체 생성
        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();
        // 3-3. 저장
        commentLikeRepository.save(commentLike);
        // 4. Response
        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.CREATED.value(), "댓글 좋아요 추가 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
    @Override
    public ResponseEntity<CustomAPIResponse<?>> deleteCommentLike(String header, Long commentId) {
        return null;
    }
}
