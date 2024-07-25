package com.server.scapture.reservation.service;

import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface ReservationService {
    ResponseEntity<CustomAPIResponse<?>> createReservation(String header, Long scheduleId);
    ResponseEntity<CustomAPIResponse<?>> getReservation(String header, Long scheduleId, String date);
}
