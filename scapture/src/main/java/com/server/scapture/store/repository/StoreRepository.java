package com.server.scapture.store.repository;

import com.server.scapture.domain.Store;
import com.server.scapture.domain.User;
import com.server.scapture.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByVideoAndUser(Video video, User user);

    List<Store> findByUser(User user);
}
