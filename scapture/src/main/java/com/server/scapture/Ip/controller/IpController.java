package com.server.scapture.Ip.controller;

import com.server.scapture.Ip.dto.CreateIpRequestDto;
import com.server.scapture.Ip.service.IpService;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/ips")
@RestController
@RequiredArgsConstructor
public class IpController {
    private final IpService ipService;
    @PostMapping
    ResponseEntity<CustomAPIResponse<?>> createIp(@RequestBody CreateIpRequestDto requestDto) {
        return ipService.createIp(requestDto);
    }
}
