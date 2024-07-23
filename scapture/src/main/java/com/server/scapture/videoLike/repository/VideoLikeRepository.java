package com.server.scapture.videoLike.repository;

import com.server.scapture.domain.User;
import com.server.scapture.domain.Video;
import com.server.scapture.domain.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    boolean existsByVideoAndUser(Video video, User user);
}
