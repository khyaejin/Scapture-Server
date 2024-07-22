package com.server.scapture.field.repository;

import com.server.scapture.domain.Field;
import com.server.scapture.domain.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    List<Field> findByStadium(Stadium stadium);
}
