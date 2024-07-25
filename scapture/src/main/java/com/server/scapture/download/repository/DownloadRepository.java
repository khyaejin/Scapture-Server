package com.server.scapture.download.repository;

import com.server.scapture.domain.Download;
import com.server.scapture.domain.User;
import com.server.scapture.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DownloadRepository extends JpaRepository<Download, Long> {
    Optional<Download> findByVideoAndUser(Video video, User user);
}
