package com.server.scapture.field.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@Builder
public class CreateFieldRequestDto {
    private Long stadiumId;
    private List<FieldAttributes> fields;
}
