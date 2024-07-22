package com.server.scapture.field.controller;

import com.server.scapture.field.dto.CreateFieldRequestDto;
import com.server.scapture.field.service.FieldService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/fields")
public class FieldController {
    private final FieldService fieldService;
    @PostMapping
    ResponseEntity<CustomAPIResponse<?>> createField(@RequestBody CreateFieldRequestDto createFieldRequestDto) {
        return fieldService.createField(createFieldRequestDto);
    }
}
