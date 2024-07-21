package com.server.scapture.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Stadium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // PK
    private String name;                // 경기장 명
    @Column(columnDefinition = "TEXT")
    private String description;         // 경기장 소개글
    private String location;            // 경기장 위치
    private String city;                // 경기장 위치(도시)
    private String state;               // 경기장 위치(지역)
    private String hours;               // 운영 시간
    private boolean isOutside;          // 실내 / 실외
    private String parking;             // 주차 공간
}
