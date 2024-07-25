package com.server.scapture.subscribe.repository;

import com.server.scapture.domain.Subscribe;
import com.server.scapture.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    Optional<Subscribe> findByUserId(Long id);
}
