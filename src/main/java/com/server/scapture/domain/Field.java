package com.server.scapture.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // PK
    @ManyToOne
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;            // 경기장 FK
    private String name;                // 구장 명
    private String type;                // 구장 유형
}
