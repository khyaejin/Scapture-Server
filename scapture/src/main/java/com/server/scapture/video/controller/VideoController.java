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

@RequestMapping("/api/video")
@RestController
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    // S3 업로드
    @PostMapping("/s3")
    public ResponseEntity<CustomAPIResponse<?>> upload(@RequestParam("image") List<MultipartFile> multipartFiles) throws IOException {
        return videoService.upload(multipartFiles);
    }

    @PostMapping
    public ResponseEntity<CustomAPIResponse<?>> createVideo(@RequestBody List<VideoCreateRequestDto> videoCreateRequestDtoList) {
        return videoService.createVideo(videoCreateRequestDtoList);
    }

    @GetMapping
    public ResponseEntity<CustomAPIResponse<?>> getVideo() {
        return videoService.getVideo();
    }

}
