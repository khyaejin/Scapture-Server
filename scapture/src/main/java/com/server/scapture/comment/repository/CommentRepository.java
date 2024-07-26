package com.server.scapture.comment.repository;

import com.server.scapture.domain.Comment;
import com.server.scapture.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVideo(Video video);
}
