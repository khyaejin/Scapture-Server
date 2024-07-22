package com.server.scapture.field.controller;

import com.server.scapture.field.dto.CreateFieldRequestDto;
import com.server.scapture.field.dto.FieldAttributes;
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
    @PostMapping
    ResponseEntity<CustomAPIResponse<?>> createField(@RequestBody CreateFieldRequestDto createFieldRequestDto) {
        return null;
    }
}
