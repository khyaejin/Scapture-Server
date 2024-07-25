package com.server.scapture.video.controller;

import com.server.scapture.oauth.jwt.JwtUtil;
import com.server.scapture.util.response.CustomAPIResponse;
import com.server.scapture.video.dto.VideoCreateRequestDto;
import com.server.scapture.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    @GetMapping("/popular")
    public ResponseEntity<CustomAPIResponse<?>> getVideosByLikesCount() {
        return videoService.getVideosByLikeCount();
    }
    @GetMapping("/store")
    public ResponseEntity<CustomAPIResponse<?>> getStoredVideo(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @RequestParam("sort") String sort) {
        return videoService.getStoredVideo(header, sort);
    }
    @GetMapping("/{videoId}/details")
    public ResponseEntity<CustomAPIResponse<?>> getVideoDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @PathVariable("videoId") Long videoId) {
        return videoService.getVideoDetail(header, videoId);
    }
    @PostMapping("/{videoId}/likes")
    public ResponseEntity<CustomAPIResponse<?>> createLike(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @PathVariable("videoId") Long videoId) {
        return videoService.createLike(header, videoId);
    }
    @DeleteMapping("/{videoId}/likes")
    public ResponseEntity<CustomAPIResponse<?>> deleteLike(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @PathVariable("videoId") Long videoId) {
        return videoService.deleteLike(header, videoId);
    }
    @PostMapping("/{videoId}/store")
    public ResponseEntity<CustomAPIResponse<?>> createStore(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @PathVariable("videoId") Long videoId) {
        return videoService.createStore(header, videoId);
    }
    @DeleteMapping("/{videoId}/store")
    public ResponseEntity<CustomAPIResponse<?>> deleteStore(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @PathVariable("videoId") Long videoId) {
        return videoService.deleteStore(header, videoId);
    }

    @PostMapping("/{videoId}/download")
    public ResponseEntity<CustomAPIResponse<?>> createDownload(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @PathVariable("videoId") Long videoId) {
        return videoService.createDownload(header, videoId);
    }
}
