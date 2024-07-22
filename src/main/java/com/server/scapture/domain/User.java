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
    private int socialId;         // AccessToken을 통해 접근하여 받은 PK 값
    private String name;        // 이름(닉네임)
    private String team;        // 소속팀
    private String location;    // 활동 지역
    @ColumnDefault("0")
    private int banana;         // 코인
    private String image;       // 프로필 이미지
    private String email;       // 이메일
    @Enumerated(EnumType.STRING)
    private Role role;           // 권한
}
