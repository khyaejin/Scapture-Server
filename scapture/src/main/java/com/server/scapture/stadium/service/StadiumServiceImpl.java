package com.server.scapture.stadium.service;

import com.server.scapture.domain.Image;
import com.server.scapture.domain.Stadium;
import com.server.scapture.image.repository.ImageRepository;
import com.server.scapture.stadium.dto.CreateStadiumRequestDto;
import com.server.scapture.stadium.dto.CreateStadiumResponseDto;
import com.server.scapture.stadium.dto.GetStadiumByCityAndStateResponseDto;
import com.server.scapture.stadium.repository.StadiumRepository;
import com.server.scapture.util.S3.S3Service;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService{
    private final StadiumRepository stadiumRepository;
    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    // 관리자 - 경기장 생성
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
    // Stadium - 경기장 조회
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getStadiumByCityAndState(String city, String state) {
        System.out.println(city + " " + state);
        // 1. 조건에 맞는 Stadium 조회
        List<Stadium> foundStadiums;
        // 1-1. City 1개 조회
        if(state.isEmpty()) foundStadiums = stadiumRepository.findByCity(city);
        // 1-2. City State 2개 조회
        else foundStadiums = stadiumRepository.findByCityAndState(city, state);
        // 2. Response
        // 2-1. data
        List<GetStadiumByCityAndStateResponseDto> data = new ArrayList<>();
        for (Stadium stadium : foundStadiums) {
            // 2-1-1. Image 1개 찾기
            Optional<Image> foundImage = imageRepository.findFirst1ByStadium(stadium);
            String image = null;
            // 2-1-2. Image가 없는 경우 -> null 값
            if (foundImage.isPresent()) image = foundImage.get().getImage();
            // 2-1-2. response 만들기
            GetStadiumByCityAndStateResponseDto response = GetStadiumByCityAndStateResponseDto.builder()
                    .stadiumId(stadium.getId())
                    .name(stadium.getName())
                    .location(stadium.getLocation())
                    .hours(stadium.getHours())
                    .isOutside(stadium.isOutside())
                    .parking(stadium.getParking())
                    .image(image)
                    .build();
            data.add(response);
        }
        // 2-2. ResponseBody
        CustomAPIResponse<List<GetStadiumByCityAndStateResponseDto>> responseBody = CustomAPIResponse.createSuccess(HttpStatus.OK.value(), data, "구장 조회 완료되었습니다.");
        // 2-3. ResponseEntity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }

}
