package com.server.scapture.video.service;

import com.server.scapture.domain.Video;
import com.server.scapture.util.S3.S3Service;
import com.server.scapture.util.response.CustomAPIResponse;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService{
    private final VideoRepository videoRepository;
    private final S3Service s3Service;
    // S3 업로드
    @Override
    public ResponseEntity<CustomAPIResponse<?>> upload(List<MultipartFile> multipartFiles) throws IOException {
        List<String> imageUrlList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = UUID.randomUUID() + multipartFile.getOriginalFilename();

//            s3Service.upload(multipartFile, fileName);
//            String presignedURL = s3Service.getPresignedURL(fileName);
//            imageUrlList.add(presignedURL);
        }
        // data
        // resonseBody
        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccess(HttpStatus.CREATED.value(), null, "비디오 업로드 성공");
        // ResponseEntity
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @Override
    public ResponseEntity<CustomAPIResponse<?>> createVideo(List<VideoCreateRequestDto> videoCreateRequestDtoList) {
        for (VideoCreateRequestDto videoCreateRequestDto : videoCreateRequestDtoList) {
            Video video = Video.builder()
//                    .schedule()
                    .name(videoCreateRequestDto.getName())
                    .image(videoCreateRequestDto.getImage())
                    .video(videoCreateRequestDto.getVideo())
                    .build();
            videoRepository.save(video);
        }

        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.CREATED.value(), "영상 등록이 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
}
