package com.server.scapture.video.service;

import com.server.scapture.util.response.CustomAPIResponse;
import com.server.scapture.video.dto.VideoCreateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    ResponseEntity<CustomAPIResponse<?>> createVideo(VideoCreateRequestDto videoCreateRequestDto);
    ResponseEntity<CustomAPIResponse<?>> getVideos(Long scheduleId);
    ResponseEntity<CustomAPIResponse<?>> getVideosByLikeCount();
}