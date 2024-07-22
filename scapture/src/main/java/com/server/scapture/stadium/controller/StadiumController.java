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
    @PostMapping
    ResponseEntity<CustomAPIResponse<?>> createStadium(@RequestPart CreateStadiumRequestDto data, @RequestPart List<MultipartFile> images) throws IOException {
        return stadiumService.createStadium(data, images);
    }
    @GetMapping
    ResponseEntity<CustomAPIResponse<?>> getStadiumsByCityAndState(@RequestParam("city") String city, @RequestParam("state") String state) {
        return stadiumService.getStadiumByCityAndState(city, state);
    }
    @GetMapping("/search")
    ResponseEntity<CustomAPIResponse<?>> getStadiumsByKeyword(@RequestParam("keyword") String keyword){
        return stadiumService.getStadiumByKeyword(keyword);
    }
    @GetMapping("/{stadiumId}")
    ResponseEntity<CustomAPIResponse<?>> getStadiumDetail(@PathVariable("stadiumId") Long stadiumId) {
        return stadiumService.getStadiumDetail(stadiumId);
    }
}
