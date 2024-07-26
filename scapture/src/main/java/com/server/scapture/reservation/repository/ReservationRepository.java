package com.server.scapture.reservation.repository;

import com.server.scapture.domain.Reservation;
import com.server.scapture.domain.Schedule;
import com.server.scapture.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByScheduleAndUser(Schedule schedule, User user);

    Optional<List<Reservation>> findByUser(User user);
}
