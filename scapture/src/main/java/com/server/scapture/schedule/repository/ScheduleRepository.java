package com.server.scapture.schedule.repository;

import com.server.scapture.domain.Field;
import com.server.scapture.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT e FROM Schedule e WHERE e.field = :field AND MONTH(e.startDate) = :month AND DAY(e.startDate) = :day")
    List<Schedule> findScheduleByFieldBetweenMonthAndDay(Field field, int month, int day);
}
