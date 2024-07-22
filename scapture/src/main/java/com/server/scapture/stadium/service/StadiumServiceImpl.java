package com.server.scapture.stadium.service;

import com.server.scapture.stadium.dto.CreateStadiumRequestDto;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService{
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createStadium(CreateStadiumRequestDto createStadiumRequestDto, List<MultipartFile> images) {
        return null;
    }
}
