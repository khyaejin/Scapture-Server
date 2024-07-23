package com.server.scapture.video.service;

import com.server.scapture.domain.Schedule;
import com.server.scapture.domain.Video;
import com.server.scapture.schedule.repository.ScheduleRepository;
import com.server.scapture.schedule.service.ScheduleService;
import com.server.scapture.util.S3.S3Service;
import com.server.scapture.util.response.CustomAPIResponse;
import com.server.scapture.video.dto.VideoCreateDetailDto;
import com.server.scapture.video.dto.VideoCreateRequestDto;
import com.server.scapture.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService{
    private final VideoRepository videoRepository;
    private final ScheduleRepository scheduleRepository;
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createVideo(VideoCreateRequestDto videoCreateRequestDto) {
        // 1. 운영 일정 조회
        Optional<Schedule> foundSchedule = scheduleRepository.findById(videoCreateRequestDto.getScheduleId());
        // 1-1. 실패
        if (foundSchedule.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 운영 시간입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. 성공
        Schedule schedule = foundSchedule.get();
        // 2. 영상 생성
        for (VideoCreateDetailDto videoCreateDetailDto : videoCreateRequestDto.getData()) {
            Video video = Video.builder()
                    .schedule(schedule)
                    .name(videoCreateDetailDto.getName())
                    .image(videoCreateDetailDto.getImage())
                    .video(videoCreateDetailDto.getVideo())
                    .build();
            videoRepository.save(video);
        }

        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.CREATED.value(), "영상 등록이 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
}
