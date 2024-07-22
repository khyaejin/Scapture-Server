package com.server.scapture.stadium.repository;

import com.server.scapture.domain.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {
    List<Stadium> findByCityAndState(String city, String state);
    List<Stadium> findByCity(String city);
}
