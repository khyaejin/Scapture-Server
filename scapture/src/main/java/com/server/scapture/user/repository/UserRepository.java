package com.server.scapture.user.repository;

import com.server.scapture.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByProviderId(String providerId);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
