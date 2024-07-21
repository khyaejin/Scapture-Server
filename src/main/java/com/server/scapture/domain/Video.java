package com.server.scapture.domain;

import com.server.scapture.util.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // PK
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;            // 운영 일정 FK
    private String name;            // 영상 제목
    private String image;           // 영상 썸네일
    private String video;           // 영상
    @ColumnDefault("0")
    private int likeCount;          // 좋아요 수
}
