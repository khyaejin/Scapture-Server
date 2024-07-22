package com.server.scapture.oauth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private long id;
    private String nickname;
    private String email;
    // @Builder.Default //후에 기본값 지정해주기 -> 기본 카카오톡 프로필?
    private String profileImage;
}
