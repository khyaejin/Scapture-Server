package com.server.scapture.Ip.service;

import com.server.scapture.Ip.dto.CreateIpRequestDto;
import com.server.scapture.util.response.CustomAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IpServiceImpl implements IpService{
    @Override
    public ResponseEntity<CustomAPIResponse<?>> createIp(CreateIpRequestDto requestDto) {
        return null;
    }
}
