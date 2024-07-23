package com.server.scapture.video.repository;

import com.server.scapture.domain.Schedule;
import com.server.scapture.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("SELECT COUNT(*) FROM Video v WHERE v.schedule = :schedule")
    int countBySchedule(Schedule schedule);
}
