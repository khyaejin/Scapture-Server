package com.server.scapture.image.repository;

import com.server.scapture.domain.Image;
import com.server.scapture.domain.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findFirst1ByStadium(Stadium stadium);
}
