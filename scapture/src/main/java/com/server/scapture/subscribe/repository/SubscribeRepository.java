package com.server.scapture.subscribe.repository;

import com.server.scapture.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscribeRepository {
    Optional<User> findByUserId(Long id);
}
