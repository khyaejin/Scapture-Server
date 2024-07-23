package com.server.scapture.video.controller;

import com.server.scapture.util.response.CustomAPIResponse;
import com.server.scapture.video.dto.VideoCreateRequestDto;
import com.server.scapture.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/videos")
@RestController
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    @PostMapping
    public ResponseEntity<CustomAPIResponse<?>> createVideo(@RequestBody VideoCreateRequestDto videoCreateRequestDto) {
        return videoService.createVideo(videoCreateRequestDto);
    }
    @GetMapping("/{scheduleId}")
    public ResponseEntity<CustomAPIResponse<?>> getVideos(@PathVariable("scheduleId") Long scheduleId) {
        return videoService.getVideos(scheduleId);
    }
}
