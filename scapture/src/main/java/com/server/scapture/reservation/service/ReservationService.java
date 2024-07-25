package com.server.scapture.reservation.service;

import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface ReservationService {
    ResponseEntity<CustomAPIResponse<?>> createReservation(Long scheduleId);
}
