package com.server.scapture.Ip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@Builder
public class CreateIpRequestDto {
    private Long fieldId;
    private List<String> ipList;
}
