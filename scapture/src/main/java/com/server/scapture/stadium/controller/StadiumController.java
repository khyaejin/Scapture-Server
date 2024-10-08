package com.server.scapture.stadium.controller;

import com.server.scapture.stadium.dto.CreateStadiumRequestDto;
import com.server.scapture.stadium.service.StadiumService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/stadiums")
public class StadiumController {
    private final StadiumService stadiumService;
    @GetMapping("/main")
    public ResponseEntity<CustomAPIResponse<?>> getMainInfo() {
        return stadiumService.getMainInfo();
    }
    @PostMapping
    public ResponseEntity<CustomAPIResponse<?>> createStadium(@RequestPart(value = "data") CreateStadiumRequestDto data, @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        return stadiumService.createStadium(data, images);
    }
    @GetMapping
    public ResponseEntity<CustomAPIResponse<?>> getStadiumsByCityAndState(@RequestParam("city") String city, @RequestParam("state") String state) {
        return stadiumService.getStadiumByCityAndState(city, state);
    }
    @GetMapping("/search")
    public ResponseEntity<CustomAPIResponse<?>> getStadiumsByKeyword(@RequestParam("keyword") String keyword){
        return stadiumService.getStadiumByKeyword(keyword);
    }
    @GetMapping("/{stadiumId}/detail")
    public ResponseEntity<CustomAPIResponse<?>> getStadiumDetail(@PathVariable("stadiumId") Long stadiumId) {
        return stadiumService.getStadiumDetail(stadiumId);
    }
    @GetMapping("/{fieldId}")
    public ResponseEntity<CustomAPIResponse<?>> getScheduleByFieldAndDate(@PathVariable("fieldId") Long fieldId, @RequestParam("date") String date) {
        return stadiumService.getScheduleByFieldAndDate(fieldId, date);
    }
}
