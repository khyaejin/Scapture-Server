package com.server.scapture.Ip.service;

import com.server.scapture.Ip.dto.CreateIpRequestDto;
import com.server.scapture.Ip.repository.IpRepository;
import com.server.scapture.domain.Field;
import com.server.scapture.domain.Ip;
import com.server.scapture.field.repository.FieldRepository;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IpServiceImpl implements IpService{
    private final FieldRepository fieldRepository;
    private final IpRepository ipRepository;
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createIp(CreateIpRequestDto requestDto) {
        // 1. Field 확인
        Optional<Field> foundField = fieldRepository.findById(requestDto.getFieldId());
        // 1-1. 실패
        if (foundField.isEmpty()) {
            CustomAPIResponse<Object> responseBody = CustomAPIResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "존재하지 않는 구장입니다.");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(responseBody);
        }
        // 1-2. 성공
        Field field = foundField.get();
        // 2. IP 생성
        for (String ipAddress : requestDto.getIpList()) {
            Ip ip = Ip.builder()
                    .field(field)
                    .ip(ipAddress)
                    .build();
            ipRepository.save(ip);
        }
        // 3. response
        CustomAPIResponse<Object> responseBody = CustomAPIResponse.createSuccessWithoutData(HttpStatus.CREATED.value(), "IP 생성 완료되었습니다.");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }
}
