package com.server.scapture.reservation.service;

import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ReservationService {
    ResponseEntity<CustomAPIResponse<?>> createReservation(String header, Long scheduleId);
    ResponseEntity<CustomAPIResponse<?>> getReservation(Long stadiumId, Long fieldId, String date);
}
