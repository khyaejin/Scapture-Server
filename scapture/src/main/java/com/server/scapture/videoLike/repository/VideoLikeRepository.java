package com.server.scapture.videoLike.repository;

import com.server.scapture.domain.User;
import com.server.scapture.domain.Video;
import com.server.scapture.domain.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    Optional<VideoLike> findByVideoAndUser(Video video, User user);
}
