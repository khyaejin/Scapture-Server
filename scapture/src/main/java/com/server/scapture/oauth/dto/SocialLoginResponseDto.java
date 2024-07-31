package com.server.scapture.oauth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SocialLoginResponseDto {
    private String token;
}
