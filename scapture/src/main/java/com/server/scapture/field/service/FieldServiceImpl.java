package com.server.scapture.field.service;

import com.server.scapture.domain.Field;
import com.server.scapture.domain.Stadium;
import com.server.scapture.field.dto.CreateFiedlResponseDto;
import com.server.scapture.field.dto.CreateFieldRequestDto;
import com.server.scapture.field.dto.FieldAttributes;
import com.server.scapture.field.repository.FieldRepository;
import com.server.scapture.stadium.repository.StadiumRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService{
    private final FieldRepository fieldRepository;
    private final StadiumRepository stadiumRepository;
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createField(CreateFieldRequestDto createFieldRequestDto) {
        // 1. 경기장 조회
        Optional<Stadium> foundStadium = stadiumRepository.findById(createFieldRequestDto.getStadiumId());
        // 1-1. 조회 실패 시 404 리턴
        if (foundStadium.isEmpty()) {
            // 1-1-1. responseBody
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 경기장입니다.");
            // 1-1-2. ResponseEntity
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. 조회 성공
        Stadium stadium = foundStadium.get();
        // 2. Response
        // 2-1. data
        List<CreateFiedlResponseDto> data = new ArrayList<>();
        // 2-2. 구장 생성
        for (FieldAttributes fieldAttributes : createFieldRequestDto.getFields()) {
            Field field = Field.builder()
                    .stadium(stadium)
                    .name(fieldAttributes.getName())
                    .type(fieldAttributes.getType())
                    .build();
            fieldRepository.save(field);
            // 2-3. data 주입
            CreateFiedlResponseDto responseDto = CreateFiedlResponseDto.builder()
                    .fieldId(field.getId())
                    .build();
            data.add(responseDto);
        }
        // 2-4. ResponseBody
        CustomAPIResponse<List<CreateFiedlResponseDto>> responseBody = CustomAPIResponse.createSuccess(HttpStatus.CREATED.value(), data, "구장 생성 완료되었습니다.");
        // 2-5. ResponseEntity
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
}
