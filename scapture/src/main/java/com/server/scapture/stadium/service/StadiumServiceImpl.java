package com.server.scapture.stadium.service;

import com.server.scapture.domain.Field;
import com.server.scapture.domain.Image;
import com.server.scapture.domain.Stadium;
import com.server.scapture.field.dto.SimpleFieldResponseDto;
import com.server.scapture.field.repository.FieldRepository;
import com.server.scapture.image.dto.SimpleImageResponseDto;
import com.server.scapture.image.repository.ImageRepository;
import com.server.scapture.stadium.dto.CreateStadiumRequestDto;
import com.server.scapture.stadium.dto.CreateStadiumResponseDto;
import com.server.scapture.stadium.dto.GetStadiumDetailDto;
import com.server.scapture.stadium.dto.GetStadiumResponseDto;
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
    private final FieldRepository fieldRepository;
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
                .isOutside(data.getIsOutside())
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
        // 1. 조건에 맞는 Stadium 조회
        List<Stadium> foundStadiums;
        // 1-1. City 1개 조회
        if(state.isEmpty()) foundStadiums = stadiumRepository.findByCity(city);
        // 1-2. City State 2개 조회
        else foundStadiums = stadiumRepository.findByCityAndState(city, state);
        // 조회된 컨텐츠 없음
        if (foundStadiums.isEmpty()) {
            // 1-1. responseBody
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.OK.value(), "조회된 경기장이 없습니다.");
            // 1-2. ResponseEntity
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseBody);
        }
        // 2. Response
        // 2-1. data
        List<GetStadiumResponseDto> data = new ArrayList<>();
        for (Stadium stadium : foundStadiums) {
            // 2-1-1. Image 1개 찾기
            Optional<Image> foundImage = imageRepository.findFirst1ByStadium(stadium);
            String image = null;
            // 2-1-2. Image가 없는 경우 -> null 값
            if (foundImage.isPresent()) image = foundImage.get().getImage();
            // 2-1-2. response 만들기
            GetStadiumResponseDto response = GetStadiumResponseDto.builder()
                    .stadiumId(stadium.getId())
                    .name(stadium.getName())
                    .location(stadium.getLocation())
                    .hours(stadium.getHours())
                    .isOutside(stadium.getIsOutside())
                    .parking(stadium.getParking())
                    .image(image)
                    .build();
            data.add(response);
        }
        // 2-2. ResponseBody
        CustomAPIResponse<List<GetStadiumResponseDto>> responseBody = CustomAPIResponse.createSuccess(HttpStatus.OK.value(), data, "경기장 조회 완료되었습니다.");
        // 2-3. ResponseEntity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }
    // Stadium - 경기장 검색
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getStadiumByKeyword(String keyword) {
        // 1. Keyword를 통해 경기장 검색
        List<Stadium> foundStadium = stadiumRepository.findByNameContaining(keyword);
        // 조회된 컨텐츠 없음
        if (foundStadium.isEmpty()) {
            // 1-1. responseBody
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.OK.value(), "조회된 경기장이 없습니다.");
            // 1-2. ResponseEntity
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseBody);
        }
        // 2. Response
        // 2-1. data
        List<GetStadiumResponseDto> data = new ArrayList<>();
        for (Stadium stadium : foundStadium) {
            // 2-1-1. Image 1개 찾기
            Optional<Image> foundImage = imageRepository.findFirst1ByStadium(stadium);
            String image = null;
            // 2-1-2. Image가 없는 경우 -> null 값
            if (foundImage.isPresent()) image = foundImage.get().getImage();
            // 2-1-2. response 만들기
            GetStadiumResponseDto response = GetStadiumResponseDto.builder()
                    .stadiumId(stadium.getId())
                    .name(stadium.getName())
                    .location(stadium.getLocation())
                    .hours(stadium.getHours())
                    .isOutside(stadium.getIsOutside())
                    .parking(stadium.getParking())
                    .image(image)
                    .build();
            data.add(response);
        }
        // 2-2. ResponseBody
        CustomAPIResponse<List<GetStadiumResponseDto>> responseBody = CustomAPIResponse.createSuccess(HttpStatus.OK.value(), data, "경기장 검색 완료되었습니다.");
        // 2-3. ResponseEntity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }
    // Stadium - 경기자 세부 조회
    @Override
    public ResponseEntity<CustomAPIResponse<?>> getStadiumDetail(Long stadiumId) {
        // 1. Stadium 조회
        Optional<Stadium> foundStadium = stadiumRepository.findById(stadiumId);
        // 1-1. Stadium 조회 실패
        if (foundStadium.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 경기장입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. Stadium 조회 성공
        Stadium stadium = foundStadium.get();
        // 2. Image 조회
        List<SimpleImageResponseDto> images;
        List<Image> foundImages = imageRepository.findByStadium(stadium);
        // 2-1. Image 조회 실패
        if (foundImages.isEmpty()) {
            images = null;
        }
        // 2-2. Image 조회 성공
        else {
            images = new ArrayList<>();
            for (Image image : foundImages) {
                SimpleImageResponseDto imageDto = SimpleImageResponseDto.builder()
                        .imageId(image.getId())
                        .image(image.getImage())
                        .build();
                images.add(imageDto);
            }
        }
        // 3. Field 조회
        List<SimpleFieldResponseDto> fields;
        List<Field> foundFields = fieldRepository.findByStadium(stadium);
        // 3-1. Field 조회 실패
        if (foundFields.isEmpty()) {
            fields = null;
        }
        // 3-2. Field 조회 성공
        else {
            fields = new ArrayList<>();
            for (Field field : foundFields) {
                SimpleFieldResponseDto fieldDto = SimpleFieldResponseDto.builder()
                        .fieldId(field.getId())
                        .name(field.getName())
                        .build();
                fields.add(fieldDto);
            }
        }
        // 4. Response
        // 4-1. data
        GetStadiumDetailDto data = GetStadiumDetailDto.builder()
                .name(stadium.getName())
                .description(stadium.getDescription())
                .location(stadium.getLocation())
                .isOutside(stadium.getIsOutside())
                .parking(stadium.getParking())
                .images(images)
                .fields(fields)
                .build();
        // 4-2. responseBody
        CustomAPIResponse<GetStadiumDetailDto> responseBody = CustomAPIResponse.createSuccess(HttpStatus.OK.value(), data, "경기장 세부 조회 완료되었습니다.");
        // 4-3. ResponseEntity
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }

}
