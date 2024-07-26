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
public class UserProfileDto {
    private String name;
    private String team;
    private String location;
    private Role role;
    private String endDate;
    private String image;

    //		"name" : "000님", //이름
    //		"team" : "스캡쳐", //팀
    //		"location" : "서울 마포구", //활동 지역
    //		"role" : "SUBSCRIBER", //회원 정보(BASIC, SUBSCRIBER, MANAGER)
    //    "endDate": "2024.08.20" //구독 정보. 구독중 아닐 시 null 리턴
    //"image" : "http://example.com/profile-image.jpg",//프로필 사진

}
