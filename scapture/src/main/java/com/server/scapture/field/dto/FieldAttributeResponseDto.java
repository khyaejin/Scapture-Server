package com.server.scapture.field.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class FieldAttributeResponseDto {
    private String name;
    private String type;
}
