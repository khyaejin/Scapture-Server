package com.server.scapture.video.repository;

import com.server.scapture.domain.Schedule;
import com.server.scapture.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("SELECT COUNT(*) FROM Video v WHERE v.schedule = :schedule")
    int countBySchedule(Schedule schedule);
    List<Video> findBySchedule(Schedule schedule);
    List<Video> findTop10ByOrderByLikeCountDesc();
    Optional<Video> findTop1ByOrderByLikeCountDesc();
}
