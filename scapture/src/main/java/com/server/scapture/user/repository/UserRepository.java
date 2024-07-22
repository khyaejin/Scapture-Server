package com.server.scapture.user.repository;

import com.server.scapture.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByProviderId(int providerId);
}
