package com.server.scapture.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // PK
    private String provider;    // 로그인 업체(Google/Kakao/Naver)
    private String providerId;  // AccessToken을 통해 접근하여 받은 PK 값
    private String name;        // 이름(닉네임)
    private String team;        // 소속팀
    private String location;    // 활동 지역
    @ColumnDefault("0")
    private int banana;         // 코인
    private String image;       // 프로필 이미지
    private String email;       // 이메일
    @Enumerated(EnumType.STRING)
    private Role role;           // 권한

    // 버내너 추가
    public void increaseTotalBananas(int balance) {
        this.banana += balance;
    }
    public void decreaseTotalBananas(int balance) {this.banana -= balance;}

    // 프로필 편집 - 이미지
    public void editProfileImage(String image) {
        this.image = image;
    }

    // 프로필 편집 - 이미지 제외
    public void editProfileWithoutImage(String name, String team, String location) {
        this.name = name;
        this.team = team;
        this.location = location;
    }
}
