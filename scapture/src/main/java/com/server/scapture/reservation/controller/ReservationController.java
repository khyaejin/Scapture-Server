package com.server.scapture.reservation.controller;

import com.server.scapture.reservation.service.ReservationService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    @PostMapping("/{scheduleId}")
    public ResponseEntity<CustomAPIResponse<?>> createReservation(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,  @PathVariable("scheduleId") Long scheduleId) {
        return reservationService.createReservation(header, scheduleId);
    }

    @GetMapping("/{stadiumId}")
    public ResponseEntity<CustomAPIResponse<?>> getReservation(@PathVariable("stadiumId") Long stadiumId, @RequestParam("date") String date) {
        return reservationService.getReservation(stadiumId, date);
    }
}
