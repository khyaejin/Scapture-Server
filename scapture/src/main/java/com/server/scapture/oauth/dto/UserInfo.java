package com.server.scapture.oauth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private long id;
    private String nickname;
    private String email;
    private String profileImage;
}
