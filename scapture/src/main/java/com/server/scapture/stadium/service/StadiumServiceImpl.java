package com.server.scapture.stadium.service;

import com.server.scapture.domain.Image;
import com.server.scapture.domain.Stadium;
import com.server.scapture.image.repository.ImageRepository;
import com.server.scapture.stadium.dto.CreateStadiumRequestDto;
import com.server.scapture.stadium.dto.CreateStadiumResponseDto;
import com.server.scapture.stadium.repository.StadiumRepository;
import com.server.scapture.util.S3.S3Service;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService{
    private final StadiumRepository stadiumRepository;
    private final ImageRepository imageRepository;
    private final S3Service s3Service;
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createStadium(CreateStadiumRequestDto data, List<MultipartFile> images) throws IOException {
        // 1. Stadium 생성
        // 1-1. Stadium 생성
        Stadium stadium = Stadium.builder()
                .name(data.getName())
                .description(data.getDescription())
                .location(data.getLocation())
                .city(data.getCity())
                .state(data.getState())
                .hours(data.getHours())
                .isOutside(data.isOutside())
                .parking(data.getParking())
                .build();
        // 1-2. 저장
        stadiumRepository.save(stadium);
        // 2. Stadium Image 생성
        String dirName = stadium.getName();
        for (MultipartFile image : images) {
            String imageUrl = s3Service.upload(image, dirName);
            System.out.println(imageUrl);
            Image stadiumImage = Image.builder()
                    .stadium(stadium)
                    .image(imageUrl)
                    .build();
            imageRepository.save(stadiumImage);
        }
        // 3. Response
        // 3-1. data
        CreateStadiumResponseDto responseDto = CreateStadiumResponseDto.builder()
                .stadiumId(stadium.getId())
                .build();
        // 3-2. responseBody
        CustomAPIResponse<CreateStadiumResponseDto> responseBody = CustomAPIResponse.createSuccess(HttpStatus.CREATED.value(), responseDto, "경기장 생성 완료되었습니다.");
        // 3-3. ResponseEntity
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
}
