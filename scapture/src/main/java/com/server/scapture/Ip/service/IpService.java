package com.server.scapture.Ip.service;

import com.server.scapture.Ip.dto.CreateIpRequestDto;
import com.server.scapture.util.response.CustomAPIResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IpService {
    ResponseEntity<CustomAPIResponse<?>> createIp(List<CreateIpRequestDto> requestDtoList);
}
