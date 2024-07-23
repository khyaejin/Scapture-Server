package com.server.scapture.oauth.dto;

import com.server.scapture.domain.Role;
import com.server.scapture.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private String provider;
    private String providerId;
    private String name;
    private String email;
    // @Builder.Default //후에 기본값 지정해주기 -> 기본 카카오톡 프로필?
    private String image;
    private Role role;

    public User toEntity() {
        return User.builder()
                .provider(provider)
                .providerId(providerId)
                .name(name)
                .email(email)
                .image(image)
                .role(role)
                .build();
    }
}
