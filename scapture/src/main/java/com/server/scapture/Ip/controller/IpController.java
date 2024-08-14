package com.server.scapture.Ip.controller;

import com.server.scapture.Ip.dto.CreateIpRequestDto;
import com.server.scapture.Ip.service.IpService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("api/ips")
@RestController
@RequiredArgsConstructor
public class IpController {
    private final IpService ipService;

    ResponseEntity<CustomAPIResponse<?>> createIp(@RequestBody List<CreateIpRequestDto> requestDtoList) {
        return null;
    }
}
