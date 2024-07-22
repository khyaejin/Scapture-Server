package com.server.scapture.field.service;

import com.server.scapture.field.dto.CreateFieldRequestDto;
import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

public interface FieldService {
    ResponseEntity<CustomAPIResponse<?>> createField(CreateFieldRequestDto createFieldRequestDto);
}
