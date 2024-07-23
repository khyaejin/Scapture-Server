package com.server.scapture.video.service;

import com.server.scapture.domain.Field;
import com.server.scapture.domain.Schedule;
import com.server.scapture.domain.Stadium;
import com.server.scapture.domain.Video;
import com.server.scapture.field.repository.FieldRepository;
import com.server.scapture.schedule.repository.ScheduleRepository;
import com.server.scapture.schedule.service.ScheduleService;
import com.server.scapture.stadium.repository.StadiumRepository;
import com.server.scapture.util.S3.S3Service;
import com.server.scapture.util.response.CustomAPIResponse;
import com.server.scapture.video.dto.GetVideosResponseDto;
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
    private final FieldRepository fieldRepository;
    private final StadiumRepository stadiumRepository;
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
                    .likeCount(0)
                    .build();
            videoRepository.save(video);
        }

        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.CREATED.value(), "영상 등록이 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getVideos(Long scheduleId) {
        // 1. Schedule 조회
        Optional<Schedule> foundSchedule = scheduleRepository.findById(scheduleId);
        // 1-1. 실패
        if (foundSchedule.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 운영 일정입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. 성공
        Schedule schedule = foundSchedule.get();
        // 2. 영상 조회
        List<Video> videoList = videoRepository.findBySchedule(schedule);
        // 3. 경기장 조회
        // 3-1. 구장 조회
        Field field = fieldRepository.findById(schedule.getField().getId()).get();
        // 3-2. 경기장 조회
        Stadium stadium = stadiumRepository.findById(field.getStadium().getId()).get();
        // 4. Response
        // 4-1. data
        List<GetVideosResponseDto> data = null;
        if (!videoList.isEmpty()) {
            data = new ArrayList<>();
            for (Video video : videoList) {
                GetVideosResponseDto responseDto = GetVideosResponseDto.builder()
                        .videoId(video.getId())
                        .name(video.getName())
                        .image(video.getImage())
                        .stadiumName(stadium.getName())
                        .date(schedule.convertMonthAndDay())
                        .hours(schedule.convertHourAndMin())
                        .build();
                data.add(responseDto);
            }
        }
        // 4-2. responseBody
        CustomAPIResponse<List<GetVideosResponseDto>> responseBody = CustomAPIResponse.createSuccess(HttpStatus.OK.value(), data, "경기 영상 조회 완료되었습니다.");
        // 4-3. ResponseEntity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getVideosByLikeCount() {
        return null;
    }
}
