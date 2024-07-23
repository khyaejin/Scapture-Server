package com.server.scapture.schedule.repository;

import com.server.scapture.domain.Field;
import com.server.scapture.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.field = :field AND DATE(s.startDate) = :date")
    List<Schedule> findScheduleByFieldBetweenMonthAndDay(Field field, LocalDate date);
}
