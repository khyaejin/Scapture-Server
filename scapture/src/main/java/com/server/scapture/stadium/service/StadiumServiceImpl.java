package com.server.scapture.stadium.service;

import com.server.scapture.domain.Image;
import com.server.scapture.domain.Stadium;
import com.server.scapture.image.repository.ImageRepository;
import com.server.scapture.stadium.dto.CreateStadiumRequestDto;
import com.server.scapture.stadium.repository.StadiumRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService{
    private final StadiumRepository stadiumRepository;
    private final ImageRepository imageRepository;
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createStadium(CreateStadiumRequestDto createStadiumRequestDto, List<MultipartFile> images) {
        // 1. Stadium 생성
        // 1-1. Stadium 생성
        Stadium stadium = Stadium.builder()
                .name(createStadiumRequestDto.getName())
                .description(createStadiumRequestDto.getDescription())
                .location(createStadiumRequestDto.getLocation())
                .city(createStadiumRequestDto.getCity())
                .state(createStadiumRequestDto.getState())
                .hours(createStadiumRequestDto.getHours())
                .isOutside(createStadiumRequestDto.isOutside())
                .parking(createStadiumRequestDto.getParking())
                .build();
        // 1-2. 저장
        stadiumRepository.save(stadium);
        // 2. Stadium Image 생성
        for (MultipartFile image : images) {
            String name = image.getOriginalFilename();
            Image stadiumImage = Image.builder()
                    .stadium(stadium)
                    .image(name)
                    .build();
            imageRepository.save(stadiumImage);
        }
        // 3. Response
        // 3-1. data
        // 3-2. responseBody
        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.CREATED.value(), "경기장 생성 완료되었습니다.");
        // 3-3. ResponseEntity
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
}
