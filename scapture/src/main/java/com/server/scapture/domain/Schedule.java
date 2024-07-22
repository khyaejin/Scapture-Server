package com.server.scapture.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // PK
    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;            // 구장 FK
    private LocalDateTime startDate;// 이용 시간(시작)
    private LocalDateTime endDate;  // 이용 시간(종료)
    @ColumnDefault("false")
    private Boolean isReserved;     // 예약 여부
    private int price;              // 가격
}
