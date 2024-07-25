package com.server.scapture.reservation.service;

import com.server.scapture.reservation.repository.ReservationRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServideImpl implements ReservationService{
    private final ReservationRepository reservationRepository;
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createReservation(Long scheduleId) {
        return null;
    }
}
