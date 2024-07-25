package com.server.scapture.reservation.controller;

import com.server.scapture.reservation.service.ReservationService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    @PostMapping("/{scheduleId}")
    public ResponseEntity<CustomAPIResponse<?>> createReservation(@PathVariable("scheduleId") Long scheduleId) {
        return null;
    }
}
