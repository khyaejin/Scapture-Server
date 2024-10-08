package com.server.scapture.user.dto;

import com.server.scapture.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProfileEditDto {
    private String name;
    private String team;
    private String location;
    private String image;
}
