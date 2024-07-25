package com.server.scapture.commentLike.repository;

import com.server.scapture.domain.Comment;
import com.server.scapture.domain.CommentLike;
import com.server.scapture.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
