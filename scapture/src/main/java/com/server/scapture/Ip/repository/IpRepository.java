package com.server.scapture.Ip.repository;

import com.server.scapture.domain.Field;
import com.server.scapture.domain.Ip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpRepository extends JpaRepository<Ip, Long> {
    List<Ip> findByField(Field field);
}
